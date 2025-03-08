package ru.etu.t1logstarter.configuration.properties;

import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logging.starter.common")
public record LogProperties(String enable, Level level) {
    public LogProperties(String enable, Level level) {
        this.enable = enable;
        this.level = level != null ? level : Level.INFO; // default val
    }
}
