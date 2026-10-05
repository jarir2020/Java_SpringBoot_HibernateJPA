package com.jarirahmed.springboot.error;

/** Raised when a multipart request contains no file content. */
public class EmptyAttachmentException extends RuntimeException {
    public EmptyAttachmentException() {
        super("The uploaded file must not be empty.");
    }
}
