package io.github.orczykowski.springbootfeatureflags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ConditionalOnExpression("${feature-flags.enabled} and ${feature-flags.api.expose.enabled}")
class FeatureFlagExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagExceptionHandler.class);

    @ExceptionHandler(FeatureFlagsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFoundHandler(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({InvalidFeatureFlagsException.class,
            InvalidCommandException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ErrorResponse userError(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({DuplicatedFeatureFlagException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse conflict(final Exception ex) {
        log.debug(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    record ErrorResponse(String message) {
    }
}
