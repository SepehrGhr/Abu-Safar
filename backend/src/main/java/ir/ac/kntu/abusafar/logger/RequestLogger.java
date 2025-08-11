package ir.ac.kntu.abusafar.logger;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

@Aspect
@Component
public class RequestLogger {

    private final Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointCut(){

    }

    @Around("restControllerPointCut()")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable{
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        logger.info("------- Request Details --------");
        logger.info("URL: {}", httpServletRequest.getRequestURL().toString());
        logger.info("Http Method {}", httpServletRequest.getMethod());
        logger.info("Headers: ");
        Enumeration<String> headers = httpServletRequest.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            logger.info("{}: {}", name, httpServletRequest.getHeader(name));
        }

        logger.info("Query Params = {}", httpServletRequest.getQueryString());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        logger.info("Method Name, {}", signature.getMethod().getName());
        logger.info("Arguments, {}", joinPoint.getArgs());

        Object response;
        try {
            response = joinPoint.proceed();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        logger.info("Response, {}", response);
        return response;
    }
}
