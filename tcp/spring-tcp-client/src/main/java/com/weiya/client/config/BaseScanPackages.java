package com.weiya.client.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 基础包扫描路径
 * Created by liuyin on 2017/10/11.
 */
@Configuration
@ComponentScan(basePackages = "com.weiya.client")
public class BaseScanPackages {
}
