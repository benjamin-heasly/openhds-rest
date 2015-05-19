package org.openhds.resource;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Ben on 5/18/15.
 */
@ControllerAdvice
class ExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VndErrors generalException(Exception ex) {
        return new VndErrors("general error: ", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public VndErrors userNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return new VndErrors("user not found: ", ex.getMessage());
    }
}
