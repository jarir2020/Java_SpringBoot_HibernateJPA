package com.jarirahmed.springboot.storage;

/** Storage value used by the file upload/download lesson. */
public record StoredAttachment(
        int id,
        String fileName,
        String contentType,
        byte[] content) {
}
