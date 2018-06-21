package com.hubpd.uar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 服务启动类
 *
 * @author cpc
 * @create 2018-06-20 16:48
 **/
@EnableAutoConfiguration
@EnableScheduling
@SpringBootApplication
@PropertySource(value = {"classpath:config/constant/constant.properties"},encoding="utf-8")
public class XYApp {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(XYApp.class);
        springApplication.run(args);
    }

    //定时任务中的线程池
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        //设置线程池大小
        taskScheduler.setPoolSize(5);
        //线程名字前缀
        taskScheduler.setThreadNamePrefix("XYApp-Thread-Pool");
        return taskScheduler;
    }
}
