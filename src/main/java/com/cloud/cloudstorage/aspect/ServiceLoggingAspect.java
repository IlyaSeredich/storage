package com.cloud.cloudstorage.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ServiceLoggingAspect {
    @Pointcut("execution(public * com.cloud.cloudstorage.service.*.*(..))")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logServiceMethod(ProceedingJoinPoint jp) throws Throwable {
        long startTime = System.currentTimeMillis();

        logStartMethod(jp);

        Object result = jp.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        logEndMethod(jp, executionTime);

        return result;
    }

    private void logStartMethod(ProceedingJoinPoint jp) {
        log.info("RUN SERVICE: SERVICE_METHOD: {}.{}.",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName());
    }

    private void logEndMethod(ProceedingJoinPoint jp, long executionTime) {
        log.info("Service method {}.{} executed successfully in {}ms ",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                executionTime);
    }
}
