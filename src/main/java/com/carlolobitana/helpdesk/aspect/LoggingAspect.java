package com.carlolobitana.helpdesk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.carlolobitana.helpdesk.service.impl.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed(); // Execute the actual method

        long executionTime = System.currentTimeMillis() - start;
        log.info(">>> AOP LOG: {} executed in {}ms", joinPoint.getSignature().getName(), executionTime);

        return proceed;
    }
}