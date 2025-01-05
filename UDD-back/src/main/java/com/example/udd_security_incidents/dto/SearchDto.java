package com.example.udd_security_incidents.dto;

import com.example.udd_security_incidents.model.SearchType;

public class SearchDto {
    private String  searchText;
    private SearchType type;
    private String city;
    public SearchDto(){}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }
}
