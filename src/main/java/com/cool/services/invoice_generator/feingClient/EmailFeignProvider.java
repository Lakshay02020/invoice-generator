package com.cool.services.invoice_generator.feingClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "todo-app", url = "http://localhost:8089", contextId = "emailFeignProvider")
public interface EmailFeignProvider {
    @GetMapping("/tasks")
    List<Object> getTasks(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize);

    @PostMapping(value = "/sendEmail", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> sendMail(
            @RequestParam String toEmail,
            @RequestParam String text,
            @RequestParam String subject,
            @RequestPart(required = false) MultipartFile resource);
}

