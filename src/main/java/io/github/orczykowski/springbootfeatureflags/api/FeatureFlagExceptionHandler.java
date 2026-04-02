package io.github.orczykowski.springbootfeatureflags.api;

import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagDuplicatedFeatureFlagException;
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagsNotFoundException;
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidCommandException;
import io.github.orczykowski.springbootfeatureflags.exceptions.FeatureFlagInvalidFeatureFlagsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ConditionalOnExpression("${feature-flags.enabled:false} and (${feature-flags.api.expose.enabled:false} or ${feature-flags.api.manage.enabled:false})")
public class FeatureFlagExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagExceptionHandler.class);

    @ExceptionHandler(FeatureFlagsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFoundHandler(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({FeatureFlagInvalidFeatureFlagsException.class,
            FeatureFlagInvalidCommandException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErrorResponse userError(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({FeatureFlagDuplicatedFeatureFlagException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse conflict(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    record ErrorResponse(String message) {
    }
}
