package com.example.udd_security_incidents.indexrepository;

import com.example.udd_security_incidents.indexmodel.IncidentDocumentIndex;
import com.example.udd_security_incidents.indexmodel.IncidentIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentIndexRepository extends ElasticsearchRepository<IncidentDocumentIndex, String> {
    Page<IncidentDocumentIndex> getIncidentIndicesByEmployeeNameAndSeverity(String employeeName, String severity, Pageable pageable);
    Page<IncidentDocumentIndex> getIncidentIndicesBySecurityOrganizationAndAffectedOrganization(String securityOrganization, String affectedOrganization, Pageable pageable);
}
