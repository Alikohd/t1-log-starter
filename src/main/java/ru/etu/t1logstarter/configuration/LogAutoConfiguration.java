package ru.etu.t1logstarter.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.etu.t1logstarter.aspect.HttpLoggingAspect;
import ru.etu.t1logstarter.aspect.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "log-starter.enable", havingValue = "true", matchIfMissing = true)
    public LoggingAspect loggingAspect(LogProperties logProperties) {
        return new LoggingAspect(logProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "log-starter.enable", havingValue = "true", matchIfMissing = true)
    public HttpLoggingAspect httpLoggingAspect(LogProperties logProperties) {
        return new HttpLoggingAspect(logProperties);
    }
}
