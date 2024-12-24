package com.example.udd_security_incidents.service.impl;

import ai.djl.translate.TranslateException;
import com.example.udd_security_incidents.exceptionhandling.exception.LoadingException;
import com.example.udd_security_incidents.exceptionhandling.exception.StorageException;
import com.example.udd_security_incidents.indexmodel.DummyIndex;
import com.example.udd_security_incidents.indexrepository.DummyIndexRepository;
import com.example.udd_security_incidents.model.DummyTable;
import com.example.udd_security_incidents.repository.DummyRepository;
import com.example.udd_security_incidents.service.interfaces.FileService;
import com.example.udd_security_incidents.service.interfaces.IndexingService;
import com.example.udd_security_incidents.util.VectorizationUtil;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.language.detect.LanguageDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class IndexingServiceImpl implements IndexingService {
    @Autowired
    private final DummyIndexRepository dummyIndexRepository;
    @Autowired
    private final DummyRepository dummyRepository;
    @Autowired
    private final FileService fileService;
    @Autowired
    private final LanguageDetector languageDetector;
   // private final IncidentIndexRepository incidentIndexRepository;

    public IndexingServiceImpl(DummyIndexRepository dummyIndexR, DummyRepository dummyR, FileService fileService, LanguageDetector languageDetector){
        this.dummyIndexRepository=dummyIndexR;
        this.dummyRepository=dummyR;
        this.fileService=fileService;
        this.languageDetector=languageDetector;

    }


    @Override
    @Transactional
    public String indexDocument(MultipartFile documentFile) {
        //Page<IncidentIndex> v=incidentIndexRepository.findIncidentIndexByAffectedOrganizationOrSecurityOrganization("a","a");
        //Page<IncidentIndex> a=incidentIndexRepository.findIncidentIndexByEmployeeNameOrEmployeeSurnameOrSeverity("a","a","a");

        var newEntity = new DummyTable();
        var newIndex = new DummyIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setTitle(title);
        newEntity.setTitle(title);

        var documentContent = extractDocumentContent(documentFile);
        if (detectLanguage(documentContent).equals("SR")) {
            newIndex.setContentSr(documentContent);
        } else {
            newIndex.setContentEn(documentContent);
        }
        newEntity.setContent(documentContent);

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);

        newEntity.setMimeType(detectMimeType(documentFile));
        var savedEntity = dummyRepository.save(newEntity);

        try {
            newIndex.setVectorizedContent(VectorizationUtil.getEmbedding(title));
        } catch (TranslateException e) {
            log.error("Could not calculate vector representation for document with ID: {}",
                savedEntity.getId());
        }
        newIndex.setDatabaseId(savedEntity.getId());
        dummyIndexRepository.save(newIndex);

        return serverFilename;
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

    private String detectLanguage(String text) {
        var detectedLanguage = languageDetector.detect(text).getLanguage().toUpperCase();
        if (detectedLanguage.equals("HR")) {
            detectedLanguage = "SR";
        }

        return detectedLanguage;
    }

    private String detectMimeType(MultipartFile file) {
        var contentAnalyzer = new Tika();

        String trueMimeType;
        String specifiedMimeType;
        try {
            trueMimeType = contentAnalyzer.detect(file.getBytes());
            specifiedMimeType =
                Files.probeContentType(Path.of(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (IOException e) {
            throw new StorageException("Failed to detect mime type for file.");
        }

        if (!trueMimeType.equals(specifiedMimeType) &&
            !(trueMimeType.contains("zip") && specifiedMimeType.contains("zip"))) {
            throw new StorageException("True mime type is different from specified one, aborting.");
        }

        return trueMimeType;
    }
}