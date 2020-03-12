package com.wx.chen.shiro.filter;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.wx.chen.config.Config;
import com.wx.chen.constant.CommonConstant;
import com.wx.chen.shiro.jwt.JWTToken;
import com.wx.chen.shiro.jwt.JwtUtil;
import com.wx.chen.util.PropertiesUtil;
import com.wx.chen.util.RedisUtil;

/**
 * @ClassName: JWTFilter
 * @Description:TODO(JWT的过滤器)
 * @author: guanghuiChen
 * @date: 2020年3月11日 下午12:35:49 实现过程：
 *        preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

	/**
	 * 
	 * <p>
	 * Title: preHandle
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 对跨域提供支持 Implementation that handles path-matching behavior before a request
	 * is evaluated. If the path matches and the filter
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @see org.apache.shiro.web.filter.PathMatchingFilter#preHandle(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}

	/**
	 * <p>
	 * Title: isAccessAllowed
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 这里是对http请求进行处理 * 这里我们详细说明下为什么最终返回的都是true，即允许访问 例如我们提供一个地址 GET /article
	 * 登入用户和游客看到的内容是不同的 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
	 * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
	 * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
	 * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
	 * 
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return
	 * @see org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#isAccessAllowed(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, java.lang.Object)
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		// 先对当前请求的URI进行判断是否放行
		HttpServletRequest req = (HttpServletRequest) request;
		String requestURI = req.getRequestURI();
		if (ReleaseAddressUtil.confirm(requestURI)) {// 对于不用进行验证的接口直接放行
			return true;
		}
		if (isLoginAttempt(request, response)) {// 有token
			// 进行Shiro的登录UserRealm
			try {
				this.executeLogin(request, response);
			} catch (Exception e) {// 这里对登录异常进行处理
				// 认证出现异常，传递错误信息msg
				String msg = e.getMessage();
				// 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
				Throwable throwable = e.getCause();
				if (throwable instanceof SignatureVerificationException) {
					// 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
					msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
				} else if (throwable instanceof TokenExpiredException) {
					// 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
					// 刷新token
					if (refreshToken(request, response)) {
						return true;
					} else {
						msg = "Token已过期(" + throwable.getMessage() + ")";
					}
					return true;
				} else {
					// 应用异常不为空
					if (throwable != null) {
						// 获取应用异常msg
						msg = throwable.getMessage();
					}
				}
				this.response401(request, response, msg);
				return false;
			}
		} else {// 没有token
				// 没有携带Token
			HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
			// 获取当前请求类型
			String httpMethod = httpServletRequest.getMethod();
			// 获取当前请求URI
			logger.info("当前请求 {} Authorization属性(Token)为空 请求类型 {}", requestURI, httpMethod);
			// mustLoginFlag = true 开启任何请求必须登录才可访问
			if (!Config.mustLoginFlag) {// 是否开放游客权限
				this.response401(httpServletRequest, response, "请先登录");
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Title: isLoginAttempt
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 检测header里是否含有Authorization字段。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @see org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter#isLoginAttempt(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		String token = this.getAuthzHeader(request);// 返回header中Authorization对应的token的值
		return token != null;
	}

	/**
	 * <p>
	 * Title: executeLogin
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 进行AccessToken登录认证授权
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @see org.apache.shiro.web.filter.authc.AuthenticatingFilter#executeLogin(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		// 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		JWTToken token = new JWTToken(this.getAuthzHeader(request));
		// 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获
		this.getSubject(request, response).login(token);
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	/**
	 * @Title: refreshToken @Description:
	 *         TODO(刷新token,具体原理：先查看redis是否存在该key。如果存在取出时间。然后与AccessToken中的时间进行比对。相同则进行更新redis中key的失效时间。提交给shiro进行再次登录，将新生成的token写入到response中返回) @param: @param
	 *         request @param: @param response @param: @return @return:
	 *         boolean @throws
	 */
	private boolean refreshToken(ServletRequest request, ServletResponse response) {
		// 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		String token = this.getAuthzHeader(request);
		// 获取当前Token的帐号信息
		String account = JwtUtil.getClaim(token, CommonConstant.ACCOUNT);
		// 判断Redis中RefreshToken是否存在
		if (RedisUtil.hasKey(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
			// Redis中RefreshToken还存在，获取RefreshToken的时间戳
			String currentTimeMillisRedis = RedisUtil.get(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + account);
			// 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
			if (JwtUtil.getClaim(token, CommonConstant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
				// 获取当前最新时间戳
				String currentTimeMillis = String.valueOf(System.currentTimeMillis());
				// 读取配置文件，获取refreshTokenExpireTime属性
				PropertiesUtil.readProperties("application.yml");
				String refreshTokenExpireTime = PropertiesUtil.getProperty("refreshTokenExpireTime");
				// 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
				RedisUtil.setEx(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis,
						Long.parseLong(refreshTokenExpireTime), TimeUnit.SECONDS);
				// 刷新AccessToken延长过期时间，设置时间戳为当前最新时间戳
				token = JwtUtil.sign(account, currentTimeMillis);
				// 将新刷新的AccessToken再次进行Shiro的登录
				JWTToken jwtToken = new JWTToken(token);
				// 提交给userRealm进行认证,如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
				this.getSubject(request, response).login(jwtToken);
				// 将token刷入response的header中
				HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
				httpServletResponse.setHeader("Authorization", token);
				httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
				return true;
			}
		}
		return false;
	}

	// 缺少权限内部转发至401处理
	private void response401(ServletRequest request, ServletResponse response, String msg) {
		HttpServletRequest req = (HttpServletRequest) request;
		try {
			req.getRequestDispatcher("/user/401").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
