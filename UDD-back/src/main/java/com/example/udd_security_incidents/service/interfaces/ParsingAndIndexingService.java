package com.example.udd_security_incidents.service.interfaces;

import com.example.udd_security_incidents.dto.DocumentContentDto;
import com.example.udd_security_incidents.dto.IndexCreationDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ParsingAndIndexingService {
    DocumentContentDto parseDocument(MultipartFile documentFile);
    String indexDocument (MultipartFile documentFile, IndexCreationDto documentContentDto);

}
