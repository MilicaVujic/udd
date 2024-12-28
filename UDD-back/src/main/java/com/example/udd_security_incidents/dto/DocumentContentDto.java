package com.example.udd_security_incidents.dto;


import com.example.udd_security_incidents.model.Severity;

public class DocumentContentDto {
    public String EmployeeName;
    public String EmployeeSurname;
    public String SecurityOrganization;
    public String AffectedOrganization;
    public Severity Severity;
    public String AffectedOrganizationAddress;
    public DocumentContentDto(){}

    public DocumentContentDto(String employeeName, String employeeSurname, String securityOrganization, String affectedOrganization, com.example.udd_security_incidents.model.Severity severity, String affectedOrganizationAddress) {
        EmployeeName = employeeName;
        EmployeeSurname = employeeSurname;
        SecurityOrganization = securityOrganization;
        AffectedOrganization = affectedOrganization;
        Severity = severity;
        AffectedOrganizationAddress = affectedOrganizationAddress;
    }
}