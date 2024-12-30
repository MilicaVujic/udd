package com.example.udd_security_incidents.controller;

import com.example.udd_security_incidents.dto.SearchDto;
import com.example.udd_security_incidents.dto.SearchQueryDTO;
import com.example.udd_security_incidents.indexmodel.DummyIndex;
import com.example.udd_security_incidents.indexmodel.IncidentDocumentIndex;
import com.example.udd_security_incidents.indexmodel.IncidentIndex;
import com.example.udd_security_incidents.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
/*
    @PostMapping("/simple")
    public Page<DummyIndex> simpleSearch(@RequestParam Boolean isKnn,
                                         @RequestBody SearchQueryDTO simpleSearchQuery,
                                         Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable, isKnn);
    }

    @PostMapping("/advanced")
    public Page<DummyIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                           Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }
    */
    @PostMapping
    public ResponseEntity<Page<IncidentDocumentIndex>> search(@RequestBody SearchDto searchQuery){
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
