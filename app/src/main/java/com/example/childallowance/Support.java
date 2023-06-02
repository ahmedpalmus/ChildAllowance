package com.example.childallowance;

import java.io.Serializable;

public class Support implements Serializable {

    private String supp_id, supp_title, supp_detail, supp_date, supp_response, username;

    public String getSupp_id() {
        return supp_id;
    }

    public void setSupp_id(String supp_id) {
        this.supp_id = supp_id;
    }

    public String getSupp_title() {
        return supp_title;
    }

    public void setSupp_title(String supp_title) {
        this.supp_title = supp_title;
    }

    public String getSupp_detail() {
        return supp_detail;
    }

    public void setSupp_detail(String supp_detail) {
        this.supp_detail = supp_detail;
    }

    public String getSupp_date() {
        return supp_date;
    }

    public void setSupp_date(String supp_date) {
        this.supp_date = supp_date;
    }

    public String getSupp_response() {
        return supp_response;
    }

    public void setSupp_response(String supp_response) {
        this.supp_response = supp_response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
