package com.cool.services.invoice_generator.helper;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {
    private final byte[] input;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    public CustomMultipartFile(byte[] input, String name, String originalFilename, String contentType) {
        this.input = input;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    public byte[] getBytes() {
        return input;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(input);
        }
    }
}
