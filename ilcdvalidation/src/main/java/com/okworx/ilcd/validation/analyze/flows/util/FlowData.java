package com.okworx.ilcd.validation.analyze.flows.util;

public class FlowData {
    public FlowData() {
    }

    public Integer occurrences = null;
    public Integer inputs = null;
    public Integer outputs = null;
    public Double sum = null;

    public FlowDirections getFlowDirections() {
        if (inputs != null && inputs > 0 && (outputs == null || outputs == 0))
            // exclusively inputs
            return FlowDirections.INPUTS_ONLY;
        else if (outputs != null && outputs > 0 && (inputs == null || inputs == 0))
            // exclusively outputs
            return FlowDirections.OUTPUTS_ONLY;
        else if ((inputs == null || inputs == 0) && (outputs == null || outputs == 0))
            // no flows
            return FlowDirections.EMPTY;
        else
            // mixed
            return FlowDirections.MIXED;
    }
}
