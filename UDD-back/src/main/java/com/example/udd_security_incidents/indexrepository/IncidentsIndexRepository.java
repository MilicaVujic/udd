package com.example.udd_security_incidents.indexrepository;

import com.example.udd_security_incidents.indexmodel.IncidentsIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentsIndexRepository extends ElasticsearchRepository<IncidentsIndex, String> {
    Page<IncidentsIndex> getIncidentIndicesByEmployeeNameAndSeverity(String employeeName, String severity, Pageable pageable);
    Page<IncidentsIndex> getIncidentIndicesBySecurityOrganizationAndAffectedOrganization(String securityOrganization, String affectedOrganization, Pageable pageable);
}
