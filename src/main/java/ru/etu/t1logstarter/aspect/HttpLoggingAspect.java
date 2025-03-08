package ru.etu.t1logstarter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.etu.t1logstarter.configuration.properties.LogHttpProperties;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
        HttpServletRequest request = getHttpRequest();
        if (request == null) {
            return;
        }

        logAtLevel("HTTP Request: {} {}: | Headers: {} | Method: {}.{} | Parsed Body: {}",
                request.getMethod(), request.getRequestURI(), getRequestHeaders(request),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "logHttpMethods()", returning = "result")
    public void logHttpResponse(JoinPoint joinPoint, Object result) {
        HttpServletResponse response = getHttpResponse();
        if (response == null) {
            return;
        }

        logAtLevel("HTTP Response: {} | Method: {}.{} | Body: {}",
                response.getStatus(), joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result);
    }

    private HttpServletRequest getHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private HttpServletResponse getHttpResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
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
