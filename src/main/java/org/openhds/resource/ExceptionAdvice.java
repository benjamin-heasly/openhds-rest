package org.openhds.resource;

import org.openhds.errors.model.ErrorLog;
import org.openhds.errors.model.ErrorLogException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

/**
 * Created by Ben on 5/18/15.
 * <p>
 * Catch and handle exceptions thrown by REST resource controllers.
 */
@ControllerAdvice
class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VndErrors generalException(Exception ex) {
        // print full stack trace for unexpected errors
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        return new VndErrors(ex.getClass().getSimpleName() + ": " + ex.getMessage(), writer.toString());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public VndErrors constraintException(DataIntegrityViolationException ex) {
        return new VndErrors("Data Conflict", ex.getMessage());
    }

    @ExceptionHandler(ErrorLogException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public VndErrors errorLogException(ErrorLogException ex) {
        ErrorLog errorLog = ex.getErrorLog();
        String detailMessage = errorLog.getDetails();
        return new VndErrors("Validation Error", detailMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public VndErrors constraintException(ConstraintViolationException ex) {
        return new VndErrors("Constraint Violation", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public VndErrors notFoundException(NoSuchElementException ex) {
        return new VndErrors("Nothing found", ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public VndErrors userNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return new VndErrors("User not found", ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public VndErrors userNotFoundExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        return new VndErrors("Method not allowed", ex.getMessage());
    }
}
