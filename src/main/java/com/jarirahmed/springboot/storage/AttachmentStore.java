package com.jarirahmed.springboot.storage;

import com.jarirahmed.springboot.error.AttachmentNotFoundException;
import com.jarirahmed.springboot.error.EmptyAttachmentException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** In-memory file storage; durable storage belongs to a later persistence phase. */
@Service
public class AttachmentStore {
    private final Map<Integer, StoredAttachment> files = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public StoredAttachment save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyAttachmentException();
        }
        try {
            int id = nextId.getAndIncrement();
            String fileName = file.getOriginalFilename() == null
                    ? "upload.bin"
                    : file.getOriginalFilename();
            String contentType = file.getContentType() == null
                    ? "application/octet-stream"
                    : file.getContentType();
            StoredAttachment stored = new StoredAttachment(id, fileName, contentType, file.getBytes());
            files.put(id, stored);
            return stored;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read the uploaded file.", exception);
        }
    }

    public StoredAttachment findById(int id) {
        StoredAttachment stored = files.get(id);
        if (stored == null) {
            throw new AttachmentNotFoundException(id);
        }
        return stored;
    }
}
