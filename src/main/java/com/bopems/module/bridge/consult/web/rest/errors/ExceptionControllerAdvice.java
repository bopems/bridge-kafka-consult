package com.bopems.module.bridge.consult.web.rest.errors;

import com.bopems.module.bridge.consult.kafka.LogSep;
import io.undertow.util.BadRequestException;
import org.apache.commons.logging.LogFactory;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.zalando.problem.Status;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

/**
 *
 * @author Max Jeison Prass
 *
 */
@ControllerAdvice("com.bopems.modulo.bridge.consulta.web.rest")
public class ExceptionControllerAdvice {

    private final LogSep LOGGER = (LogSep) LogFactory.getLog(this.getClass());

    @ExceptionHandler(InternalServerErrorException.class)
    public ModelAndView doInternalServerErrorException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("InternalServerErrorException, StatusCode: " +
                Status.INTERNAL_SERVER_ERROR.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ModelAndView doHttpClientErrorException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("HttpClientErrorException, StatusCode: " +
                Status.BAD_REQUEST.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(NoRouteToHostException.class)
    public ModelAndView doNoRouteToHostException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("NoRouteToHostException, StatusCode: " +
                Status.NOT_FOUND.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.NOT_FOUND.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView doNotFoundException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("NotFoundException, StatusCode: " +
                Status.NOT_FOUND.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.NOT_FOUND.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(BadRequestException.class)
    public ModelAndView doBadRequestException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("BadRequestException, StatusCode: " +
                Status.BAD_REQUEST.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(UnsupportedHttpVersionException.class)
    public ModelAndView doUnsupportedHttpVersionException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("UnsupportedHttpVersionException, StatusCode: " +
                Status.HTTP_VERSION_NOT_SUPPORTED.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.HTTP_VERSION_NOT_SUPPORTED.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(AuthorizationException.class)
    public ModelAndView doAuthorizationException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("AuthorizationException, StatusCode: " +
                Status.UNAUTHORIZED.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.UNAUTHORIZED.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ModelAndView doHttpMediaTypeException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("HttpMediaTypeException, StatusCode: " +
                Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ModelAndView doHttpMediaTypeNotAcceptableException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("HttpMediaTypeNotAcceptableException, StatusCode: " +
                Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(NoSuchMethodException.class)
    public ModelAndView doNoSuchMethodException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("NoSuchMethodException, StatusCode: " +
                Status.METHOD_NOT_ALLOWED.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.METHOD_NOT_ALLOWED.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ModelAndView doSocketTimeoutException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("SocketTimeoutException, StatusCode: " +
                Status.REQUEST_TIMEOUT.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.REQUEST_TIMEOUT.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(TimeoutException.class)
    public ModelAndView doTimeoutException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("TimeoutException, StatusCode: " +
                Status.REQUEST_TIMEOUT.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.REQUEST_TIMEOUT.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(LockedException.class)
    public ModelAndView doLockedException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("LockedException, StatusCode: " +
                Status.LOCKED.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.LOCKED.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(UnsatisfiedDependencyException.class)
    public ModelAndView doUnsatisfiedDependencyException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("UnsatisfiedDependencyException, StatusCode: " +
                Status.FAILED_DEPENDENCY.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.FAILED_DEPENDENCY.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(UnavailableException.class)
    public ModelAndView doUnavailableException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("UnavailableException, StatusCode: " +
                Status.SERVICE_UNAVAILABLE.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.SERVICE_UNAVAILABLE.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ModelAndView doAuthenticationException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("AuthenticationException, StatusCode: " +
                Status.NETWORK_AUTHENTICATION_REQUIRED.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.NETWORK_AUTHENTICATION_REQUIRED.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView doIllegalStateException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("IllegalStateException, StatusCode: " +
                Status.NOT_ACCEPTABLE.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.NOT_ACCEPTABLE.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView doException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        LOGGER.error("Exception, StatusCode: " +
                Status.INTERNAL_SERVER_ERROR.getStatusCode() +
                "Message: " + ex.getMessage());

        response.sendError(Status.INTERNAL_SERVER_ERROR.getStatusCode(), ex.getMessage());
        return new ModelAndView();
    }

}