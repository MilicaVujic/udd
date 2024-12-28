package com.example.udd_security_incidents.service.impl;

import com.example.udd_security_incidents.dto.DocumentContentDto;
import com.example.udd_security_incidents.exceptionhandling.exception.LoadingException;
import com.example.udd_security_incidents.model.Severity;
import com.example.udd_security_incidents.service.interfaces.FileService;
import com.example.udd_security_incidents.service.interfaces.ParsingAndIndexingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ParsingAndIndexingServiceImpl implements ParsingAndIndexingService {
    @Autowired
    private final FileService fileService;

    @Autowired
    public ParsingAndIndexingServiceImpl(FileService fileService){
        this.fileService=fileService;
    }
    @Override
    public DocumentContentDto indexDocument(MultipartFile documentFile) {

        String documentContent=extractDocumentContent(documentFile);

        String employeeName = extractField(documentContent, "Ime zaposlenog:");
        String employeeSurname = extractField(documentContent, "Prezime zaposlenog:");
        String affectedOrganization = extractField(documentContent, "Naziv pogođene organizacije:");
        String securityOrganization = extractField(documentContent, "Naziv bezbednosne organizacije:");
        String severity = extractField(documentContent, "Ozbiljnost incidenta:");
        String affectedOrganizationAddress = extractField(documentContent, "Adresa pogođene organizacije:");


        DocumentContentDto documentContentDto=new DocumentContentDto(employeeName, employeeSurname, affectedOrganization, securityOrganization,
                getSeverityByName(severity),affectedOrganizationAddress);

        return documentContentDto;
    }
    private Severity getSeverityByName(String name){
        if(name.toLowerCase().equals("niska"))
            return Severity.NISKA;
        else if (name.toLowerCase().equals("srednja")) {
            return  Severity.SREDNJA;
        }else if (name.toLowerCase().equals("visoka")){
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
}
