package com.example.udd_security_incidents.controller;

import ai.djl.translate.TranslateException;
import com.example.udd_security_incidents.dto.SearchDto;
import com.example.udd_security_incidents.indexmodel.IncidentsIndex;
import com.example.udd_security_incidents.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    @PostMapping
    public ResponseEntity<Page<IncidentsIndex>> search(@RequestBody SearchDto searchQuery) throws TranslateException, JsonProcessingException {
        return new ResponseEntity<>(searchService.search(searchQuery, new Pageable() {
            @Override
            public int getPageNumber() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 10;
            }

            @Override
            public long getOffset() {
                return 0;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public Pageable next() {
                return null;
            }

            @Override
            public Pageable previousOrFirst() {
                return null;
            }

            @Override
            public Pageable first() {
                return null;
            }

            @Override
            public Pageable withPage(int pageNumber) {
                return null;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }
        }), HttpStatus.OK);
    }
}
