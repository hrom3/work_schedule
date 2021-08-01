package by.bsuir.controller.exception;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger log = Logger.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleOthersException(Exception e) {
        /* Handles all other exceptions. Status code 500. */
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(2L, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedException(Exception e) {
        /* Handles Unauthorized exceptions. Status code 401. */
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(401L, e.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnconfirmedUserException.class)
    public ResponseEntity<ErrorMessage> handleUnconfirmedUserException(Exception e) {
        /* Handles Unconfirmed user exceptions. Status code 423. */
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(423L, e.getMessage()),
                HttpStatus.LOCKED);
    }

    @ExceptionHandler(PresentEntityException.class)
    public ResponseEntity<ErrorMessage> handlePresentDepartmentException(Exception e) {
        /* Handles Present entity exceptions. Status code 200. */
        log.error(e.getMessage(), e);
        log.info(e.getMessage(), e);
        System.out.println(e.getMessage());
        return new ResponseEntity<>(new ErrorMessage(200L, e.getMessage()),
                HttpStatus.OK);
    }
}
