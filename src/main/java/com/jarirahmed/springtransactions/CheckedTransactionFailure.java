package com.jarirahmed.springtransactions;

/** Checked failure used to demonstrate @Transactional(rollbackFor = ...). */
public class CheckedTransactionFailure extends Exception {
    public CheckedTransactionFailure(String message) {
        super(message);
    }
}
