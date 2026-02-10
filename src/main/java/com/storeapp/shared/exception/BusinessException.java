package com.storeapp.shared.exception;

/**
 * Eccezione base per tutte le eccezioni business dell'applicazione.
 * Estendere questa classe per creare eccezioni custom con codice errore e HTTP status.
 */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    /**
     * Costruttore per BusinessException
     *
     * @param message Messaggio descrittivo dell'errore
     * @param errorCode Codice errore univoco (es. "USER_NOT_FOUND")
     * @param httpStatus HTTP status code da ritornare
     */
    protected BusinessException(String message, String errorCode, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Costruttore con causa
     */
    protected BusinessException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
