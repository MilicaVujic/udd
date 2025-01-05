package com.example.udd_security_incidents.indexmodel;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.elasticsearch.annotations.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "incidents_index")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")
public class IncidentsIndex {
    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "employee_name")
    private String employeeName;

    @Field(type = FieldType.Text, store = true, name = "security_organization")
    private String securityOrganization;

    @Field(type = FieldType.Text, store = true, name = "affected_organization")
    private String affectedOrganization;

    @Field(type = FieldType.Text, store = true, name = "severity")
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
