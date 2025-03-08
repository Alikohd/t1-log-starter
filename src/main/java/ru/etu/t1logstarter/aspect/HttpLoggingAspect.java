package ru.etu.t1logstarter.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.etu.t1logstarter.configuration.properties.LogHttpProperties;

import java.util.Arrays;

@Aspect
public class HttpLoggingAspect {
    private final Logger log = LoggerFactory.getLogger(HttpLoggingAspect.class);
    private final LogHttpProperties logHttpProperties;

    public HttpLoggingAspect(LogHttpProperties logProperties) {
        this.logHttpProperties = logProperties;
    }

    private void logAtLevel(String msg, Object... args) {
        log.atLevel(logHttpProperties.level()).log(msg, args);
    }

    @Pointcut("@within(ru.etu.t1logstarter.aspect.annotation.LogHttp)")
    public void logHttpMethods() {
    }

    @Before("logHttpMethods()")
    public void logHttpRequest(JoinPoint joinPoint) {
        logAtLevel("HTTP request received. Executing method {}.{} with parsed Body: {}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "logHttpMethods()", returning = "result")
    public void logHttpResponse(JoinPoint joinPoint, Object result) {
        logAtLevel("Method: {}.{} executed successfully. Response body: {}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), result);
    }

}
