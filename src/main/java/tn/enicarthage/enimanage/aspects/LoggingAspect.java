package tn.enicarthage.enimanage.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Simplified format that matches your console pattern
    private static final String METHOD_ENTRY = "╔═ Method Entry ────────────────────────────────────────";
    private static final String METHOD_EXIT = "╚═ Method Exit ─────────────────────────────────────────";
    private static final String LINE_PREFIX = "║ ";

    // Controller layer logging (will appear in both console and file)
    @Around("execution(* tn.enicarthage.enimanage.Controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("\n{}\n{} {}.{}() called with args: {}",
                METHOD_ENTRY,
                LINE_PREFIX,
                className,
                methodName,
                formatArguments(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("{} {}.{}() executed in {} ms\n{} Return: {}\n{}",
                    LINE_PREFIX,
                    className,
                    methodName,
                    duration,
                    LINE_PREFIX,
                    formatReturnValue(result),
                    METHOD_EXIT);

            return result;
        } catch (Exception e) {
            logger.error("{} {}.{}() failed after {} ms\n{} Exception: {}\n{} Stacktrace: {}\n{}",
                    LINE_PREFIX,
                    className,
                    methodName,
                    System.currentTimeMillis() - startTime,
                    LINE_PREFIX,
                    e.toString(),
                    LINE_PREFIX,
                    Arrays.toString(Arrays.stream(e.getStackTrace())
                            .limit(5) // Only show top 5 stack trace elements
                            .toArray()),
                    METHOD_EXIT);
            throw e;
        }
    }

    // Service layer logging (DEBUG level - won't appear with your current config)
    @Around("execution(* tn.enicarthage.enimanage.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isDebugEnabled()) {
            String methodName = joinPoint.getSignature().getName();
            logger.debug("Service method executed: {}", methodName);
        }
        return joinPoint.proceed();
    }

    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) return "none";
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    // Mask sensitive data
                    String str = arg.toString();
                    if (str.toLowerCase().contains("password")) return "[PROTECTED]";
                    if (str.length() > 50) return str.substring(0, 50) + "...";
                    return str;
                })
                .collect(Collectors.joining(", "));
    }

    private String formatReturnValue(Object value) {
        if (value == null) return "void";
        String str = value.toString();
        return str.length() > 100 ? str.substring(0, 100) + "..." : str;
    }
}