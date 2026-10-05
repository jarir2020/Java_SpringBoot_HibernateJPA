package com.jarirahmed.springboot.model;

/** Metadata returned after a successful multipart upload. */
public record FileUploadResponse(
        int id,
        String fileName,
        String contentType,
        long size) {
}
