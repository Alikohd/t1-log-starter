package ru.etu.t1logstarter.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.etu.t1logstarter.configuration.properties.LogProperties;

import java.util.Arrays;

@Aspect
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final LogProperties logProperties;

    public LoggingAspect(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    private void logAtLevel(String msg, Object... args) {
        log.atLevel(logProperties.level()).log(msg, args);
    }

    @Around("@annotation(ru.etu.t1logstarter.aspect.annotation.LogTimeExecution)")
    public Object executionBenchmark(ProceedingJoinPoint joinPoint) {
        logAtLevel("Measuring {} method..", joinPoint.getSignature().getName());
        long startTime = System.currentTimeMillis();

        Object proceededResult;
        try {
            proceededResult = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(String.format("Error when executing the method %s with arguments %s",
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs())), e);
        }

        long endTime = System.currentTimeMillis();
        logAtLevel("Method {} executed in {} ms", joinPoint.getSignature().getName(), endTime - startTime);

        return proceededResult;
    }

    @Before("@annotation(ru.etu.t1logstarter.aspect.annotation.LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        logAtLevel("Executing method {} with arguments {} of {}", joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()), joinPoint.getTarget().getClass());
    }

    @AfterThrowing(value = "@annotation(ru.etu.t1logstarter.aspect.annotation.LogException)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        logAtLevel("Exception {} while executing method {}", ex.getMessage(), joinPoint.getSignature().getName());
    }

    @AfterReturning(value = "@annotation(ru.etu.t1logstarter.aspect.annotation.LogReturning)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        logAtLevel("Method {} executed successfully. Result: {}", joinPoint.getSignature().getName(), result);
    }
}
