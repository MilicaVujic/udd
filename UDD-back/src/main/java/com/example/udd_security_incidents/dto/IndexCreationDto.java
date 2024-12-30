package com.example.udd_security_incidents.dto;

import com.example.udd_security_incidents.model.Severity;

public class IndexCreationDto {
    public String employeeName;
    public String securityOrganization;
    public String affectedOrganization;
    public Severity severity;
    public String affectedOrganizationAddress;

    public IndexCreationDto(){}
}
