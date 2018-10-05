package com.example.demo.mybatisplus;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {"classpath:/mybatis/spring-mybatis.xml"})
//@MapperScan("cn.lqdev.learning.springboot.chapter9.biz.dao")
//@EnableTransactionManagement
public class MybatisPlusConfig {

}
