package com.example.udd_security_incidents.controller;

import com.example.udd_security_incidents.dto.*;
import com.example.udd_security_incidents.service.interfaces.IndexingService;
import com.example.udd_security_incidents.service.interfaces.ParsingAndIndexingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final ParsingAndIndexingService parsingAndIndexingService;

    @PostMapping("/parse")
    @ResponseStatus(HttpStatus.OK)
    public DocumentContentDto addIncidentDocumentFile(
            @ModelAttribute IncidentDocumentFileDto documentFile) {
        return parsingAndIndexingService.parseDocument(documentFile.multipartFile());
    }

    @PostMapping
    public ResponseEntity<IncidentDocumentFileResponseDto> createDocument(
            @RequestPart("document") MultipartFile document,
            @RequestPart("data") String data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        IndexCreationDto dto = objectMapper.readValue(data, IndexCreationDto.class);
        String filename = parsingAndIndexingService.indexDocument(document, dto);
        return new ResponseEntity<>(new IncidentDocumentFileResponseDto(filename), HttpStatus.CREATED);
    }


}
