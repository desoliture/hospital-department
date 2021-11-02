package com.kozka.hospitaldepartment.configs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kozka Ivan
 */
@Configuration
public class BeansConfig {

    @Bean("logger")
    public Logger getLogger() {
        return LogManager.getLogger();
    }
}
