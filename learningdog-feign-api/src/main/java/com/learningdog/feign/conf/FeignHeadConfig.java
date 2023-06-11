package com.learningdog.feign.conf;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author: getjiajia
 * @description:
 *      自定义请求头处理类，将认证token设置到feign的请求头中
 * @version: 1.0
 */
@Configuration
@Slf4j
public class FeignHeadConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes attributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes!=null){
                HttpServletRequest request=attributes.getRequest();
                //认证信息可能在请求头中
                Enumeration<String> headerNames=request.getHeaderNames();
                if (headerNames!=null){
                    while(headerNames.hasMoreElements()){
                        String name=headerNames.nextElement();
                        String value=request.getHeader(name);
                        if ("authorization".equalsIgnoreCase(name)){
                            log.debug("添加自定义请求头key:{},value:{}",name,value);
                            requestTemplate.header(name,value);
                            return;
                        }
                    }
                }
                //认证信息可能在cookies中
                Cookie[] cookies = request.getCookies();
                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        if("jwt".equalsIgnoreCase(cookie.getName())){
                            log.debug("添加自定义请求头key:authorization,value:{}",cookie.getValue());
                            requestTemplate.header("authorization", "Bearer "+cookie.getValue());
                        }
                    }
                }

            }

        };
    }

}
