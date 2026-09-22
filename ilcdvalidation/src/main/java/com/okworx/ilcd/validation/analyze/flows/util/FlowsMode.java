package com.okworx.ilcd.validation.analyze.flows.util;

public enum FlowsMode {
    ELEMENTARIES("elementaries"),
    OTHERS("others");

    private final String value;

    FlowsMode(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public String getValue() {
        return this.value;
    }

}
