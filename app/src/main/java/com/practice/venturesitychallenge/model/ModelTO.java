package com.practice.venturesitychallenge.model;

import java.io.Serializable;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class ModelTO implements Serializable {

    private DataTO data;
    private boolean success;

    public DataTO getData() {
        return data;
    }

    public void setData(DataTO data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
