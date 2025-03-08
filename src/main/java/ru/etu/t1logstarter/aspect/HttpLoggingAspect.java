package ru.etu.t1logstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.etu.t1logstarter.configuration.LogProperties;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
public class HttpLoggingAspect {
    private final Logger log = LoggerFactory.getLogger(HttpLoggingAspect.class);
    private final LogProperties logProperties;

    public HttpLoggingAspect(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    private void logAtLevel(String msg, Object... args) {
        log.atLevel(logProperties.level()).log(msg, args);
    }

    @Pointcut("@within(ru.etu.t1logstarter.aspect.annotation.LogHttp)")
    public void logHttpMethods() {
    }

    @Before("logHttpMethods()")
    public void logHttpRequest(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpRequest();
        if (request == null) {
            return;
        }

        logAtLevel("HTTP Request: {} {} | Headers: {} | Method: {}.{} | Parsed Body: {}",
                request.getMethod(), request.getRequestURI(), getRequestHeaders(request),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "logHttpMethods()", returning = "result")
    public void logHttpResponse(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = getHttpRequest();
        if (request == null) {
            return;
        }

        logAtLevel("HTTP Response: {} {} | Method: {}.{} | Returned: {}",
                request.getMethod(), request.getRequestURI(),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                result
        );
    }

    private HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }

        return headers;
    }
}
