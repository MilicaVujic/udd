package com.example.udd_security_incidents.indexrepository;

import com.example.udd_security_incidents.indexmodel.DummyIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyIndexRepository
    extends ElasticsearchRepository<DummyIndex, String> {
}
