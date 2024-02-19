package com.jc.jcsports.model;

public enum CalculateType {
    ADD("Add"),
    SUBSTRACT("Substract"),
    NONE("None");
    private final String label;

    CalculateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
