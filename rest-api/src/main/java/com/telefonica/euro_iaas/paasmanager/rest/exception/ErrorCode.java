/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.rest.exception;

public enum ErrorCode {

    DB_CONNECTION(20, "Could not open connection to database", "(.*)JDBCConnectionException(.*)", 500),
    HIBERNATE(10, "Problem in database backend", "(.*)org.hibernate(.*)", 500),
    ENTITY_NOT_FOUND(30, "Entity not found", "(.*)EntityNotFoundException(.*)", 404),
    ENVIRONMENT_IN_USE(40,
            "The environment is being used by an instance",
            "(.*)InvalidEnvironmentRequestException: (.*)is being used(.*)",
            403),
    INFRASTRUCTURE(50, "Openstack infrastructure failure", "(.*)InfrastructureException(.*)", 500),
    DEFAULT(500, "Internal PaasManager Server Error", "(.*)",

    500);

    private final int code;
    private final String publicMessage;
    private final String pattern;
    private final int httpCode;

    private ErrorCode(Integer code, String publicMessage, String pattern, Integer httpCode) {
        this.code = code;
        this.publicMessage = publicMessage;
        this.pattern = pattern;
        this.httpCode = httpCode;
    }

    public String getPublicMessage() {
        return publicMessage;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + "(" + httpCode + "): " + publicMessage;
    }

    public static ErrorCode find(String value) {
        if (value == null) {
            return ErrorCode.DEFAULT;
        }

        ErrorCode[] errors = ErrorCode.values();
        int i = 0;
        while (i < errors.length && !value.matches(errors[i].getPattern())) {
            i++;
        }
        return errors[i];
    }

    public String getPattern() {
        return pattern;
    }

    public Integer getHttpCode() {
        return this.httpCode;
    }
}