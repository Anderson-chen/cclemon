package org.cclemon.cclemon.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserAop {

    // @Before("@annotation(org.cclemon.cclemon.annotation.SayHi)")
    // public void sayHi() {
    //     System.err.println("Say Hi!!!!");
    // }

    @Pointcut("@annotation(org.cclemon.cclemon.annotation.checkId)")
    public void idAround(){}

    @Around("@annotation(org.cclemon.cclemon.annotation.SayHi)")
    public Object sayHi(ProceedingJoinPoint jp) throws Throwable {

        System.err.println("Say Hi!!!!");

       return jp.proceed();
    }

    @Around("idAround()")
    public Object find(ProceedingJoinPoint jp) throws Throwable {

        Object[] ids = {2L};



       return jp.proceed(ids);
    }

}
