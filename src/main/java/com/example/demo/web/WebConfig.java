package com.example.demo.web;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptors.RequestProcessingTimeInterceptors;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// registering the mvc interceptor so that request the comes in can be intercepted
		registry.addInterceptor(new RequestProcessingTimeInterceptors());
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	


	
	

}