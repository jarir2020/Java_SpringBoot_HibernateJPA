package com.jarirahmed.springboot.error;

/** Raised when an in-memory upload id is not available. */
public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(int id) {
        super("Attachment " + id + " was not found.");
    }
}
