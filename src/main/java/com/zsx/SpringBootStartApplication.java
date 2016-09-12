package com.zsx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
/**
 * 修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法
 * @author ZSX
 *
 */
public class SpringBootStartApplication extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootStartApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		// return super.configure(builder);
		return builder.sources(Application.class);
	}

}
