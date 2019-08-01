package com.bopems.module.bridge.consult.web.rest.util;

import com.bopems.module.bridge.consult.kafka.LogSep;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static LogSep log = (LogSep) LogFactory.getLog(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-moduloConsultaApp-alert", message);
        headers.add("X-moduloConsultaApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error(new StringBuilder().append("Entity processing failed, ").append(defaultMessage).toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-moduloConsultaApp-error", defaultMessage);
        headers.add("X-moduloConsultaApp-params", entityName);
        return headers;
    }
}
