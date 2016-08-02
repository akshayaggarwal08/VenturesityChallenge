package com.practice.venturesitychallenge.parser;

import com.google.gson.Gson;
import com.practice.venturesitychallenge.model.ModelTO;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class JSONHandler {

    public static ModelTO toModel(String s){
        return new Gson().fromJson(s,ModelTO.class);
    }

    public static String toJSON(ModelTO model){
       return new Gson().toJson(model);
    }

}
