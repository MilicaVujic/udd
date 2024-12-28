package com.example.udd_security_incidents.dto;

import org.springframework.web.multipart.MultipartFile;

public record IncidentDocumentFileDto(MultipartFile multipartFile) {
}
