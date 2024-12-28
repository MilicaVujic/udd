package com.example.udd_security_incidents.controller;

import com.example.udd_security_incidents.dto.DocumentContentDto;
import com.example.udd_security_incidents.dto.DummyDocumentFileDTO;
import com.example.udd_security_incidents.dto.DummyDocumentFileResponseDTO;
import com.example.udd_security_incidents.dto.IncidentDocumentFileDto;
import com.example.udd_security_incidents.service.interfaces.IndexingService;
import com.example.udd_security_incidents.service.interfaces.ParsingAndIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final ParsingAndIndexingService parsingAndIndexingService;

/*
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DummyDocumentFileResponseDTO addDocumentFile(
        @ModelAttribute DummyDocumentFileDTO documentFile) {
        var serverFilename = indexingService.indexDocument(documentFile.file());
        return new DummyDocumentFileResponseDTO(serverFilename);
    }
*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentContentDto addIncidentDocumentFile(
            @ModelAttribute IncidentDocumentFileDto documentFile) {
        return parsingAndIndexingService.indexDocument(documentFile.multipartFile());
    }
}
