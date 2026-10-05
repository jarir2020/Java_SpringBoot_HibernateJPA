package com.jarirahmed.springboot.controller;

import com.jarirahmed.springboot.model.FileUploadResponse;
import com.jarirahmed.springboot.storage.AttachmentStore;
import com.jarirahmed.springboot.storage.StoredAttachment;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

/** Demonstrates multipart upload and binary download without a database. */
@RestController
@RequestMapping("/api/files")
public class AttachmentController {
    private final AttachmentStore store;

    public AttachmentController(AttachmentStore store) {
        this.store = store;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        StoredAttachment stored = store.save(file);
        FileUploadResponse response = new FileUploadResponse(
                stored.id(),
                stored.fileName(),
                stored.contentType(),
                stored.content().length);
        return ResponseEntity.created(URI.create("/api/files/" + stored.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable int id) {
        StoredAttachment stored = store.findById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(stored.contentType()));
        headers.setContentLength(stored.content().length);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(stored.fileName())
                .build());
        return new ResponseEntity<>(stored.content(), headers, HttpStatus.OK);
    }
}
