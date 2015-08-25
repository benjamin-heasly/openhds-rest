package org.openhds.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by ben on 8/24/15.
 * <p>
 * Cross-cutting logging and method timing.
 *
 */
@Component
@Aspect
public class LoggingAspect {

    private final Log log = LogFactory.getLog(this.getClass());

    @Pointcut("execution(* org.openhds.resource..register*(..))")
    public void resourceRegister(){}

    @Before("resourceRegister()")
    public void nameAndArgs(JoinPoint joinPoint) throws Throwable {
        StringBuilder stringBuilder = new StringBuilder();
        printMethodName(stringBuilder, joinPoint);
        printMethodArgs(stringBuilder, joinPoint);
        log.info(stringBuilder.toString());
    }

    @Pointcut("execution(* org.openhds.service..record*(..))")
    public void serviceRecord(){}

    @Pointcut("execution(* org.openhds.service..find*(..))")
    public void serviceFind(){}

    @Around("serviceFind() || serviceRecord()")
    private Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // time the method execution
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        StringBuilder stringBuilder = new StringBuilder();
        printMethodName(stringBuilder, joinPoint);
        printMethodArgs(stringBuilder, joinPoint);
        printResult(stringBuilder, result);
        printExecutionTime(stringBuilder, stopWatch);

        log.info(stringBuilder.toString());
        return result;
    }

    private static void printMethodName(StringBuilder stringBuilder, JoinPoint joinPoint) {
        stringBuilder.append("\n  ")
                .append(joinPoint.getTarget().getClass().getName())
                .append(".")
                .append(joinPoint.getSignature().getName());
    }

    private static void printMethodArgs(StringBuilder stringBuilder, JoinPoint joinPoint) {
        stringBuilder.append("\n  ").append("with args:");
        for (Object object : joinPoint.getArgs()) {
            stringBuilder.append("\n    ")
                    .append(object);
        }
    }

    private static void printResult(StringBuilder stringBuilder, Object result) {
        stringBuilder.append("\n  ").append("with result:");
        stringBuilder.append("\n    ")
                .append(result);
    }

    private static void printExecutionTime(StringBuilder stringBuilder, StopWatch stopWatch) {
        stringBuilder.append("\n  ").append("execution took ")
                .append(stopWatch.getTotalTimeMillis())
                .append(" ms");
    }

}
