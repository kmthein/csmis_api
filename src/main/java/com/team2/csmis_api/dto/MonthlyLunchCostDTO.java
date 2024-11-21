package com.team2.csmis_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonthlyLunchCostDTO {
    private String departmentName;
    private String month;
    private String year;
    private String date;
    private String start;
    private String end;
    private Double totalCost;

    public MonthlyLunchCostDTO(String departmentName, String time, Double totalCost) {
        this.departmentName = departmentName;
        if(time.length() == 4) {
            this.year = time;
        } else if(time.length() > 4) {
            this.date = time;
        } else {
            this.month = time;
        }
        this.totalCost = totalCost;
    }

    public MonthlyLunchCostDTO(String departmentName, String month, String year, Double totalCost) {
        this.departmentName = departmentName;
        if(month.length() > 4) {
            this.start = month;
        } else {
            this.month = month;
        }
        if(year.length() > 4) {
            this.end = year;
        } else {
            this.year = year;
        }
        this.totalCost = totalCost;
    }
}
