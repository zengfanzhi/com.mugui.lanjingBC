package com.mugui;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.mugui.DB.FileRead;
import com.mugui.DB.SqlUtils;
import com.mugui.lanjingBC.net.ModuleMessage;
import com.mugui.model.ModelManagerInterface;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class MuguiApplication extends WebMvcConfigurationSupport {

	private static String APPLICATION_PATH = null;
	public static MuguiApplication Application = null;
	static {
		System.setProperty("sun.jnu.encoding", "utf-8");
		APPLICATION_PATH = MuguiApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			APPLICATION_PATH = URLDecoder.decode(new File(APPLICATION_PATH).getParent(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public static void run(String[] args) throws Exception {

		LogInit(args);
		ApplicationContext app = SpringApplication.run(MuguiApplication.class, args);
		ModelManagerInterface modelManagerInterface = (ModelManagerInterface) System.getProperties()
				.get("ModuleMessage");
		if (modelManagerInterface != null) {
			modelManagerInterface.invokeFunction("init", MuguiApplication.class);
		} else {
			modelManagerInterface = new ModuleMessage();
			modelManagerInterface.invokeFunction("init", MuguiApplication.class);
			System.getProperties().put("ModuleMessage", modelManagerInterface);
		}
		FileRead.Init(APPLICATION_PATH + "/WEB-INF/config/sql.db");
		SqlUtils.init("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/lanjing_bb?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false&autoReconnect=true&failOverReadOnly=false&maxReconnects=1000", "root", "admin");
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return super.getApplicationContext();
	}
	/**
	 * 日志系统初始化
	 * 
	 * @param args
	 */
	private static void LogInit(String[] args) {
		PrintStream out = new PrintStream(System.out) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			@Override
			public void print(String s) {
				super.print(format.format(new Date()) + ":" + s);
			}

			@Override
			public void println(String x) {
				print(x + "\r\n");
			}

			@Override
			public void print(Object obj) {
				print(String.valueOf(obj));
			}

			@Override
			public void println(Object x) {
				print(String.valueOf(x));
			}

			@Override
			public void print(char c) {
				print(String.valueOf(c));
			}

			@Override
			public void print(boolean b) {
				print(String.valueOf(b));
			}

			@Override
			public void print(float f) {
				print(String.valueOf(f));
			}

			@Override
			public void print(int i) {
				print(String.valueOf(i));
			}

			@Override
			public void println(char c) {
				print(String.valueOf(c));
			}

			@Override
			public void println(boolean b) {
				print(String.valueOf(b));
			}

			@Override
			public void println(float f) {
				print(String.valueOf(f));
			}

			@Override
			public void println(int i) {
				print(String.valueOf(i));
			}
		};
		System.setOut(out);
	}

	@Override
	protected void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
		Application = this;
	}
}
