package com.booking.external.amadeus.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AmadeusTimerAspect {
    @Value("${amadeus.aspect.timer}")
    private boolean isAmadeusTimerOn;

    @Around("execution(* com.booking.external.amadeus.services..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!isAmadeusTimerOn) {
            return joinPoint.proceed();
        }
        long timerStart = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long duration = System.currentTimeMillis() - timerStart;
        log.info("Method {} executed with {}ms", joinPoint.getSignature(), duration);
        return proceed;
    }
}
