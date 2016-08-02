package com.practice.venturesitychallenge.model;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class SingletonModel {

    private ModelTO modelTO = new ModelTO();
    private static SingletonModel singletonModel = new SingletonModel();
    private SingletonModel(){

    }

    public static SingletonModel getSingletonModel() {
        return singletonModel;
    }

    public static void setSingletonModel(SingletonModel singletonModel) {
        SingletonModel.singletonModel = singletonModel;
    }

    public ModelTO getModelTO() {
        return modelTO;
    }

    public void setModelTO(ModelTO modelTO) {
        this.modelTO = modelTO;
    }
}
