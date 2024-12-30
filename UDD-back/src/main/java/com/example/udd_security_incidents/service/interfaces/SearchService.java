package com.example.udd_security_incidents.service.interfaces;

import com.example.udd_security_incidents.dto.SearchDto;
import com.example.udd_security_incidents.indexmodel.DummyIndex;
import java.util.List;

import com.example.udd_security_incidents.indexmodel.IncidentDocumentIndex;
import com.example.udd_security_incidents.indexmodel.IncidentIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {

   // Page<DummyIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN);

  //  Page<DummyIndex> advancedSearch(List<String> expression, Pageable pageable);

    Page<IncidentDocumentIndex> search(SearchDto searchDto, Pageable pageable);
}
