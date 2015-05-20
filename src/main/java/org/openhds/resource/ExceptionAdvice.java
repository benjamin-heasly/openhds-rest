package org.openhds.resource;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * Created by Ben on 5/18/15.
 *
 * Catch and handle exceptions thrown by REST resource controllers.
 *
 */
@ControllerAdvice
class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VndErrors generalException(Exception ex) {
        return new VndErrors(ex.getClass().getSimpleName() + ": ", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndErrors notFoundException(NoSuchElementException ex) {
        return new VndErrors("Nothing found: ", ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public VndErrors userNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return new VndErrors("User not found: ", ex.getMessage());
    }
}
