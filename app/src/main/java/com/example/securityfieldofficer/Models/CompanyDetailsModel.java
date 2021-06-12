package com.example.securityfieldofficer.Models;

public class CompanyDetailsModel {

    String company_name,company_city;
    Integer visit_done;
    Integer visit_id;

    public CompanyDetailsModel(){

    }

    public CompanyDetailsModel(String company_name, String company_city,Integer visit_done) {
        this.company_name = company_name;
        this.company_city = company_city;
        this.visit_done = visit_done;
    }

    public Integer getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(Integer visit_id) {
        this.visit_id = visit_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_city() {
        return company_city;
    }

    public void setCompany_city(String company_city) {
        this.company_city = company_city;
    }

    public Integer getVisit_done() {
        return visit_done;
    }

    public void setVisit_done(Integer visit_done) {
        this.visit_done = visit_done;
    }
}
