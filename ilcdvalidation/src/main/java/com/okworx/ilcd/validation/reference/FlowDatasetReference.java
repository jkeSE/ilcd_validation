package com.okworx.ilcd.validation.reference;

import com.okworx.ilcd.validation.common.DatasetType;

public class FlowDatasetReference extends DatasetReference {

    private String flowType;

    public FlowDatasetReference(String uuid, DatasetType type) {
        super(uuid, type);
    }

    public FlowDatasetReference(String uuid, String version, DatasetType type) {
        super(uuid, version, type);
    }

    public FlowDatasetReference(String uuid, String version, String absoluteFileName, String shortFileName) {
        super(uuid, version, absoluteFileName, shortFileName);
    }

    public FlowDatasetReference(String absoluteFileName, String shortFileName) {
        super(absoluteFileName, shortFileName);
    }

    public FlowDatasetReference(String uuid, String uri, DatasetType type, String absoluteFileName, String shortFileName) {
        super(uuid, uri, type, absoluteFileName, shortFileName);
    }

    public FlowDatasetReference(String uuid, String uri, DatasetType type, String absoluteFileName, String shortFileName, String origin) {
        super(uuid, uri, type, absoluteFileName, shortFileName, origin);
    }

    public FlowDatasetReference(String uuid, String version, String uri, String absoluteFileName, String shortFileName) {
        super(uuid, version, uri, absoluteFileName, shortFileName);
    }

    public FlowDatasetReference(String uuid, String version, String uri, DatasetType type, String absoluteFileName, String shortFileName) {
        super(uuid, version, uri, type, absoluteFileName, shortFileName);
    }

    public FlowDatasetReference(String uuid, String version, String uri, DatasetType type, String absoluteFileName, String shortFileName, String origin) {
        super(uuid, version, uri, type, absoluteFileName, shortFileName, origin);
    }

    public FlowDatasetReference(IDatasetReference ref) {
        super(ref.getUuid(), ref.getVersion(), ref.getAbsoluteFileName(), ref.getShortFileName());
        this.datasetType = DatasetType.FLOW;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

}
