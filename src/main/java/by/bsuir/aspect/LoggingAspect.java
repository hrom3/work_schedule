package by.bsuir.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger log = Logger.getLogger(LoggingAspect.class);

//    @Before("aroundRepositoryPointcut()")
//    public void logBefore(JoinPoint joinPoint) {
//        log.info("Method " + joinPoint.getSignature().getName() + " start");
//    }
//
//    @AfterReturning(pointcut = "aroundRepositoryPointcut()")
//    public void doAccessCheck(JoinPoint joinPoint) {
//        log.info("Method " + joinPoint.getSignature().getName() + " finished");
//    }

    @Pointcut("execution(* by.bsuir.repository.impl.JdbcTemplateUserRepository.*(..))")
    public void aroundRepositoryPointcut() {
    }

   /* @Around("aroundRepositoryPointcut()")
    public Object logAroundMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Method " + joinPoint.getSignature().getName() + " start");
        long time = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long time = System.currentTimeMillis() - time;
        log.info("Method " + joinPoint.getSignature().getName() + " finished" +
                "\n \t\t Execute time = = " + time +" ms");

        return proceed;
    } Мой метод
    */

    @Around("aroundRepositoryPointcut()")
    public Object logAroundMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Method " + joinPoint.getSignature().getName() + " start");
        StopWatch timer = new StopWatch();
        timer.start();
        Object proceed = joinPoint.proceed();
        timer.stop();
        log.info("Method " + joinPoint.getSignature().getName() + " finished. \n \t\t Execute time =" + timer.getTotalTimeMillis() + " ms");
        return proceed;
    }
}
