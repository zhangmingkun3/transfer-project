package com.lp.transfer.transferproject;

import com.lp.transfer.transferproject.service.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TransferProjectApplication  extends SpringBootServletInitializer {

	/**
	 * 使用外部的tomcat启动需要重写此方法
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TransferProjectApplication.class);
	}

	/**
	 * 注释掉pom中的<scope>provided</scope>后可使用main方法启动
	 */
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TransferProjectApplication.class, args);
		applicationContext.getBean(SocketServer.class).start();//在spring容器启动后，取到已经初始化的SocketServer，启动Socket服务
	}

}
