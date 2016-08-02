package com.practice.venturesitychallenge.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class DataTO implements Serializable {

    private String form_id;
    private String form_name;
    private List<FormField> form_fields;

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public List<FormField> getForm_fields() {
        return form_fields;
    }

    public void setForm_fields(List<FormField> form_fields) {
        this.form_fields = form_fields;
    }
}
