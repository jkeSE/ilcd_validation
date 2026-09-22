package com.okworx.ilcd.validation.test.analyze.flow;

import com.okworx.ilcd.validation.analyze.flows.util.FlowData;
import com.okworx.ilcd.validation.analyze.flows.util.FlowDirections;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class FlowDataTest {

    @Test
    public void testEmpty() {
        FlowData fd = new FlowData();
        assertEquals(FlowDirections.EMPTY, fd.getFlowDirections());

        fd.inputs = 0;
        assertEquals(FlowDirections.EMPTY, fd.getFlowDirections());

        fd.outputs = 0;
        assertEquals(FlowDirections.EMPTY, fd.getFlowDirections());

        FlowData fd1 = new FlowData();
        fd1.outputs = 0;
        assertEquals(FlowDirections.EMPTY, fd1.getFlowDirections());
    }

    @Test
    public void testInputs() {
        FlowData fd = new FlowData();

        fd.inputs = 1;
        assertEquals(FlowDirections.INPUTS_ONLY, fd.getFlowDirections());

        fd.outputs = 0;
        assertEquals(FlowDirections.INPUTS_ONLY, fd.getFlowDirections());

        fd.inputs = 42;
        assertEquals(FlowDirections.INPUTS_ONLY, fd.getFlowDirections());
    }

    @Test
    public void testOutputs() {
        FlowData fd = new FlowData();

        fd.outputs = 1;
        assertEquals(FlowDirections.OUTPUTS_ONLY, fd.getFlowDirections());

        fd.inputs = 0;
        assertEquals(FlowDirections.OUTPUTS_ONLY, fd.getFlowDirections());

        fd.outputs = 42;
        assertEquals(FlowDirections.OUTPUTS_ONLY, fd.getFlowDirections());
    }

    @Test
    public void testMixed() {
        FlowData fd = new FlowData();

        fd.inputs = 1;
        fd.outputs = 1;
        assertEquals(FlowDirections.MIXED, fd.getFlowDirections());

        fd.inputs = 42;
        fd.outputs = 42;
        assertEquals(FlowDirections.MIXED, fd.getFlowDirections());
    }

}
