package ru.etu.t1logstarter.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.etu.t1logstarter.aspect.HttpLoggingAspect;
import ru.etu.t1logstarter.aspect.LoggingAspect;
import ru.etu.t1logstarter.configuration.properties.LogHttpProperties;
import ru.etu.t1logstarter.configuration.properties.LogProperties;

@Configuration
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "logging.starter.common.enable", havingValue = "true", matchIfMissing = true)
    public LoggingAspect loggingAspect(LogProperties logProperties) {
        return new LoggingAspect(logProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "logging.starter.http.enable", havingValue = "true", matchIfMissing = true)
    public HttpLoggingAspect httpLoggingAspect(LogHttpProperties logHttpProperties) {
        return new HttpLoggingAspect(logHttpProperties);
    }
}
