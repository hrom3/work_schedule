package by.bsuir.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger log = LoggerFactory
            .getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleOthersException(Exception e) {
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(401L, e.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedException(Exception e) {
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(401L, e.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnconfirmedUserException.class)
    public ResponseEntity<ErrorMessage> handleUnconfirmedUserException(Exception e) {
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(423L, e.getMessage()),
                HttpStatus.LOCKED);
    }

    @ExceptionHandler(PresentEntityException.class)
    public ResponseEntity<ErrorMessage> handlePresentDepartmentException(Exception e) {
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(200L, e.getMessage()),
                HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<ErrorMessage> handleNoSuchEntityException(Exception e) {
        log.error(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(404L, e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}


