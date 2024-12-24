package com.example.udd_security_incidents.indexmodel;

import com.example.udd_security_incidents.dto.SearchQueryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.annotations.*;
import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "incident_documents")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")

public class IncidentIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "employee_name")
    private String employeeName;

    @Field(type = FieldType.Text, store = true, name = "employee_surname")
    private String employeeSurname;

    @Field(type = FieldType.Text, store = true, name = "security_organization")
    private String securityOrganization;

    @Field(type = FieldType.Text, store = true, name = "affected_organization")
    private String affectedOrganization;

    @Field(type = FieldType.Keyword, store = true, name = "severity")
    private String severity;

    @Field(type = FieldType.Text, store = true, name = "affected_organization_address")
    private String affectedOrganizationAddress;

    @Field(type = FieldType.Keyword, store = true, name = "file_path")
    private String filePath; // Путања у MiniIO

    @Field(type = FieldType.Dense_Vector, dims = 384, similarity = "cosine")
    private float[] vectorizedContent;

    @GeoPointField
    @Field(store = true, name = "organization_location")
    private GeoPoint organizationLocation;

}
