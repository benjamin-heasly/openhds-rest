package org.openhds.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by ben on 8/24/15.
 * <p>
 * Add basic logging as a cross-cutting aspect.  Add timing for service methods.
 *
 */
@Component
@Aspect
public class LoggingAspect {

    private final Log log = LogFactory.getLog(this.getClass());

    @Around("execution(* org.openhds.service..find*(..))")
    public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        // time the method execution
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object retVal = joinPoint.proceed();
        stopWatch.stop();

        // method name
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(joinPoint.getTarget().getClass().getName())
                .append(".")
                .append(joinPoint.getSignature().getName());

        // method args
        logMessage.append("\n").append("with args:");
        for (Object object : joinPoint.getArgs()) {
            logMessage.append("\n").append(object);
        }

        // method return
        logMessage.append("\n").append("with result:");
        logMessage.append("\n").append(retVal);

        // method execution time
        logMessage.append("\n").append("took ")
                .append(stopWatch.getTotalTimeMillis())
                .append(" ms");

        log.info(logMessage.toString());
        return retVal;
    }

}
