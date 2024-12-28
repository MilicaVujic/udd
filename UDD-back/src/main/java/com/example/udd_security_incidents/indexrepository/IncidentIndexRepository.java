package com.example.udd_security_incidents.indexrepository;

import com.example.udd_security_incidents.indexmodel.IncidentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentIndexRepository extends ElasticsearchRepository<IncidentIndex, String> {
}
