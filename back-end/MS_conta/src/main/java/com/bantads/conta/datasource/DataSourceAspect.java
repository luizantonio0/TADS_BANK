package com.bantads.conta.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class DataSourceAspect {

    @Around("@annotation(transactional)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceContextHolder.setContext(DataSourceType.READER);
            } else {
                DataSourceContextHolder.setContext(DataSourceType.WRITER);
            }
            return proceedingJoinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearContext();
        }
    }
}