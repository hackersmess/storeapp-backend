package com.storeapp.activity.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando le split di una spesa non corrispondono al totale
 */
public class InvalidExpenseSplitException extends BusinessException {

    public InvalidExpenseSplitException(String message) {
        super(message, "INVALID_EXPENSE_SPLIT", 400);
    }
}
