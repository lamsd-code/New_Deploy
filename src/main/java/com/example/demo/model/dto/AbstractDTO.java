package com.example.demo.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class  AbstractDTO<T> implements Serializable {
    private static final long serialVersionUID = 7213600440729202783L;
    private Long id;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
    private int maxPageItems = 3;
    private int page = 1;
    private List<T> listResult = new ArrayList<>();
    private int totalItems = 0;
    private String tableId = "tableList";
    private Integer limit;
    private Integer totalPage;
    private Integer totalItem;
    private String searchValue;

    public String getSearchValue() { return searchValue; }
    public void setSearchValue(String v) { this.searchValue = v; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getMaxPageItems() { return maxPageItems; }
    public void setMaxPageItems(int m) { this.maxPageItems = m; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int t) { this.totalItems = t; }
    public List<T> getListResult() { return listResult; }
    public void setListResult(List<T> l) { this.listResult = l; }
}
