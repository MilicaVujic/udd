package com.example.udd_security_incidents.service.interfaces;

import ai.djl.translate.TranslateException;
import com.example.udd_security_incidents.dto.SearchDto;

import com.example.udd_security_incidents.indexmodel.IncidentIndex;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {

   // Page<DummyIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN);

  //  Page<DummyIndex> advancedSearch(List<String> expression, Pageable pageable);

    Page<IncidentIndex> search(SearchDto searchDto, Pageable pageable) throws TranslateException, JsonProcessingException;
}
