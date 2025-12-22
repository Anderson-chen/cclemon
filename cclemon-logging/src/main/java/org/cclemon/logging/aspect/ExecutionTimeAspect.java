package org.cclemon.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    // 定義一個 Pointcut，用於匹配所有在 org.cclemon 包及其子包中的公共方法
    @Pointcut("execution(public * org.cclemon..*.*(..))")
    public void serviceMethods() {
    }

    // 攔截帶有 @LogExecutionTime 註解的方法
    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 取得方法資訊與描述
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        // 2. 開始計時
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        try {
            // 3. 執行原方法
            result = joinPoint.proceed();
        } finally {
            // 4. 停止計時並紀錄日誌 (無論方法是否拋出例外都會執行)
            stopWatch.stop();
            log.info("Performance Log - [{}]: {} ms", methodName, stopWatch.getTotalTimeMillis());
        }

        return result;
    }
}