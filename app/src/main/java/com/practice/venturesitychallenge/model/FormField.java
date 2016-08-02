package com.practice.venturesitychallenge.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by akshayaggarwal08 on 7/31/16.
 */
public class FormField implements Serializable {

    private int componentId;

    public int getValidationWarningId() {
        return validationWarningId;
    }

    public void setValidationWarningId(int validationWarningId) {
        this.validationWarningId = validationWarningId;
    }

    private int validationWarningId;
    private String component;
    private String description;
    private String label;
    private String validation;
    private String autofill;

    private List<String> options;
    private List<String> autoselect;

    private List<String> userInputSelected;
    public List<String> getUserInputSelected() {
        return userInputSelected;
    }

    public void setUserInputSelected(List<String> optionsSelected) {
        this.userInputSelected = optionsSelected;
    }

    private Map<String, Integer> optionsId;

    private Boolean editable;
    private Boolean required;

    private String userInput;


    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getAutofill() {
        return autofill;
    }

    public void setAutofill(String autofill) {
        this.autofill = autofill;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getAutoselect() {
        return autoselect;
    }

    public void setAutoselect(List<String> autoselect) {
        this.autoselect = autoselect;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public Map<String, Integer> getOptionsId() {
        return optionsId;
    }

    public void setOptionsId(Map<String, Integer> optionsId) {
        this.optionsId = optionsId;
    }
}
