package com.example.udd_security_incidents.service.impl;

import ai.djl.translate.TranslateException;
import com.example.udd_security_incidents.dto.DocumentContentDto;
import com.example.udd_security_incidents.dto.IndexCreationDto;
import com.example.udd_security_incidents.exceptionhandling.exception.LoadingException;
import com.example.udd_security_incidents.indexmodel.IncidentsIndex;
import com.example.udd_security_incidents.indexrepository.IncidentsIndexRepository;
import com.example.udd_security_incidents.model.Severity;
import com.example.udd_security_incidents.service.interfaces.FileService;
import com.example.udd_security_incidents.service.interfaces.ParsingAndIndexingService;
import com.example.udd_security_incidents.util.VectorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class ParsingAndIndexingServiceImpl implements ParsingAndIndexingService {
    private final FileService fileService;
    private final IncidentsIndexRepository incidentIndexRepository;

    @Autowired
    public ParsingAndIndexingServiceImpl(FileService fileService, IncidentsIndexRepository incidentIndexRepository){
        this.fileService=fileService;
        this.incidentIndexRepository=incidentIndexRepository;
    }
    @Override
    public DocumentContentDto parseDocument(MultipartFile documentFile) {

        String documentContent=extractDocumentContent(documentFile);

        String employeeName = extractField(documentContent, "Ime zaposlenog:");
        String affectedOrganization = extractField(documentContent, "Naziv pogođene organizacije:");
        String securityOrganization = extractField(documentContent, "Naziv bezbednosne organizacije:");
        String severity = extractField(documentContent, "Ozbiljnost incidenta:");
        String affectedOrganizationAddress = extractField(documentContent, "Adresa pogođene organizacije:");

        return new DocumentContentDto(employeeName,affectedOrganization, securityOrganization,
                getSeverityByName(severity),affectedOrganizationAddress);
    }
    private Severity getSeverityByName(String name){
        if(name.equalsIgnoreCase("niska"))
            return Severity.NISKA;
        else if (name.equalsIgnoreCase("srednja")) {
            return  Severity.SREDNJA;
        }else if (name.equalsIgnoreCase("visoka")){
            return Severity.VISOKA;
        }else{
            return Severity.KRITICNA;
        }
    }
    private String extractField(String text, String fieldName) {
        return text.substring(text.indexOf(fieldName) + fieldName.length()).split("\n")[0].trim();
    }
    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }

        return documentContent;
    }

    @Override
    public String indexDocument(MultipartFile documentFile, IndexCreationDto indexCreationDto) {
        var newIndex = new IncidentsIndex();
        newIndex.setEmployeeName(indexCreationDto.employeeName);
        newIndex.setSecurityOrganization(indexCreationDto.securityOrganization);
        newIndex.setAffectedOrganization(indexCreationDto.affectedOrganization);
        newIndex.setSeverity(indexCreationDto.severity.toString());
        newIndex.setAffectedOrganizationAddress(indexCreationDto.affectedOrganizationAddress);
        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setFilePath(serverFilename);

        try {
            newIndex.setVectorizedContent(VectorizationUtil.getEmbedding(extractDocumentContent(documentFile)));
        } catch (TranslateException e) {
            log.error("Could not calculate vector representation for document.");
        }
        incidentIndexRepository.save(newIndex);

        return serverFilename;
    }
}
