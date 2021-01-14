package com.github.davidfantasy.mybatisplus.generatorui;

import com.github.davidfantasy.mybatisplus.generatorui.mbp.NameConverter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Map;


/**
 * 不使用@SpringBootApplication和@EnableAutoConfiguration
 * 注解，避免启动时被宿主系统的自动配置所干扰，直接注入需要的配置类
 */
@SpringBootConfiguration
@Import({
        DispatcherServletAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        MultipartAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        WebMvcAutoConfiguration.class})
@ComponentScan("com.github.davidfantasy.mybatisplus.generatorui")
@Slf4j
public class MybatisPlusToolsApplication {

    /**
     * 自己修改的
     * @param args
     */
    public static void main(String[] args) {
        GeneratorConfig config = GeneratorConfig.builder().jdbcUrl("jdbc:mysql://10.192.33.171:3306/codegen")
                .userName("root")
                .password("mysql171171")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                //数据库schema，POSTGRE_SQL,ORACLE,DB2类型的数据库需要指定
                //.schemaName("myBusiness")
                //如果需要修改各类生成文件的默认命名规则，可自定义一个NameConverter实例，覆盖相应的名称转换方法：
                .nameConverter(new NameConverter() {
                    /**
                     * 自定义Service类文件的名称规则
                     */
                    @Override
                    public String serviceNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Service";
                    }
                    /**
                     * 自定义Controller类文件的名称规则
                     */
                    @Override
                    public String controllerNameConvert(String tableName) {
                        return this.entityNameConvert(tableName) + "Controller";
                    }
                })
                .basePackage("com.github.davidfantasy.mybatisplustools.example")
                .port(8068)
                .build();
        MybatisPlusToolsApplication.run(config);
        //SpringApplication.run(MybatisPlusToolsApplication.class, args);
    }

    private static GeneratorConfig generatorConfig;

    public static void run(GeneratorConfig generatorConfig) {
        if (Strings.isNullOrEmpty(generatorConfig.getJdbcUrl())) {
            throw new IllegalArgumentException("jdbcUrl必须要设置");
        }
        MybatisPlusToolsApplication.generatorConfig = generatorConfig;
        Map<String, Object> props = Maps.newHashMap();
        props.put("spring.resources.static-locations", "classpath:/generator-ui/");
        new SpringApplicationBuilder()
                .properties(props)
                .sources(MybatisPlusToolsApplication.class)
                .run(new String[0]);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerConfig(GeneratorConfig config) {
        return factory -> {
            if (config.getPort() != null) {
                factory.setPort(MybatisPlusToolsApplication.generatorConfig.getPort());
            } else {
                factory.setPort(8080);
            }
            factory.setContextPath("");
        };
    }

    @Bean
    public GeneratorConfig generatorConfig() {
        return MybatisPlusToolsApplication.generatorConfig;
    }


}

