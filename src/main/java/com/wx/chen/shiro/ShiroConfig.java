package com.wx.chen.shiro;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.wx.chen.config.Config;
import com.wx.chen.shiro.cache.UserCacheManager;
import com.wx.chen.shiro.filter.JWTFilter;
import com.wx.chen.shiro.realm.UserRealm;

/**
 * @ClassName: ShiroConfig
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: guanghuiChen
 * @date: 2020年3月11日 下午4:17:03
 * 
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
@Configuration
public class ShiroConfig {
	/**
	 * @Title: defaultWebSecurityManager @Description:
	 *         TODO(配置使用自定义Realm，关闭Shiro自带的session) @param: @param userRealm
	 *         自定义的Realm @param: @return @return: DefaultWebSecurityManager @throws
	 */
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Bean("securityManager")
	public DefaultWebSecurityManager defaultWebSecurityManager(UserRealm userRealm) {
		DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
		// 使用自定义Realm
		defaultWebSecurityManager.setRealm(userRealm);
		// 关闭Shiro自带的session
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		defaultWebSecurityManager.setSubjectDAO(subjectDAO);
		// 设置自定义Cache缓存
		defaultWebSecurityManager.setCacheManager(new UserCacheManager());
		return defaultWebSecurityManager;
	}

	/**
	 * @Title: shiroFilterFactoryBean @Description:
	 * TODO(添加自己的过滤器规则--详情见文档shiroRules) @param: @param
	 * securityManager @param: @return @return: ShiroFilterFactoryBean @throws
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		// 添加自己的过滤器取名为jwt
		Map<String, Filter> filterMap = new HashMap<>(16);
		filterMap.put("jwt", new JWTFilter());
		factoryBean.setFilters(filterMap);
		factoryBean.setSecurityManager(securityManager);
		// 自定义url规则使用LinkedHashMap有序Map
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>(16);
		// Swagger接口文档
		filterChainDefinitionMap.put("/v2/api-docs", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/swagger-resources/**", "anon");
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		filterChainDefinitionMap.put("/doc.html", "anon");
		// 所有请求通过我们自己的JWTFilter
		filterChainDefinitionMap.put("/**", "jwt");
//		if (Config.shiroConfig) {
//			filterChainDefinitionMap.put("/**", "jwt"); // 放行所有
//		} else {
//			filterChainDefinitionMap.put("/login/**", "anon");
//		}
		
		factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return factoryBean;

	}
	
    /**
     * 下面的代码是添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题，https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
    
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
