package com.lediya.networklib.exception;

import java.io.IOException;

public class NetworkCommonException extends IOException {

    private TYPE type;

    public NetworkCommonException(TYPE type) {
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    public enum TYPE {
        NO_INTERNET,
        BASE_URL_NOT_FOUND,
        ClASS_CANNOT_CAST,
        URL_NOT_FOUND,
        PARAMETER_NOT_FOUND,
        SECURITY_ISSUE,
        CLASS_NOT_FOUND
    }
}
