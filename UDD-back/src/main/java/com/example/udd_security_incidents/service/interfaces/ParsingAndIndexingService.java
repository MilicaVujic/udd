package com.example.udd_security_incidents.service.interfaces;

import com.example.udd_security_incidents.dto.DocumentContentDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ParsingAndIndexingService {
    DocumentContentDto indexDocument(MultipartFile documentFile);

}
