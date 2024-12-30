package com.example.udd_security_incidents.dto;

import com.example.udd_security_incidents.model.SearchType;

public class SearchDto {
    private String  searchText;
    private SearchType type;
    public SearchDto(){}

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
