package com.okworx.ilcd.validation.analyze.flows.util;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;

public class ProcessSummaryDTO implements Comparable<ProcessSummaryDTO> {
    public ProcessSummaryDTO() {
    }

    public String uuid = null;
    public String version = null;
    public String name = null;
    public String geo = null;
    public String owner = null;
    public String processType = null;
    public SortedMap<String, FlowData> elementaryExchanges = null;
    public SortedMap<String, FlowData> otherExchanges = null;
    public List<String> referenceExchanges = null;

    public int getElementaryExchangesCount() {
        int result = 0;
        if (this.elementaryExchanges != null)
            result = this.elementaryExchanges.values().stream()
                    .mapToInt(x -> x.occurrences)
                    .sum();
        return result;
    }

    public int getOtherExchangesCount() {
        int result = 0;
        if (this.otherExchanges != null)
            result = this.otherExchanges.values().stream()
                    .mapToInt(x -> x.occurrences)
                    .sum();
        return result;
    }

    @Override
    public String toString() {
        return "ProcessSummaryDTO{" +
                "uuid='" + uuid + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", geo='" + geo + '\'' +
                ", owner='" + owner + '\'' +
                ", processType='" + processType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessSummaryDTO)) return false;
        ProcessSummaryDTO that = (ProcessSummaryDTO) o;
        return uuid.equals(that.uuid) && version.equals(that.version) && Objects.equals(name, that.name) && Objects.equals(geo, that.geo) && Objects.equals(owner, that.owner) && Objects.equals(processType, that.processType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, version, name, geo, owner, processType);
    }

    @Override
    public int compareTo(ProcessSummaryDTO that) {
        if (this.name == null && that.name == null) {
            // pass
        } else if (this.name == null) {
            return -1;
        } else if (that.name == null) {
            return 1;
        } else {
            int nameComparison = this.name.compareTo(that.name);
            if (nameComparison != 0) {
                return nameComparison < 0 ? -1 : 1;
            }
        }

        if (this.geo == null && that.geo == null) {
            // pass
        } else if (this.geo == null) {
            return -1;
        } else if (that.geo == null) {
            return 1;
        } else {
            int geoComparison = this.geo.compareTo(that.geo);
            if (geoComparison != 0) {
                return geoComparison < 0 ? -1 : 1;
            }
        }

        if (this.processType == null && that.processType == null) {
            // pass
        } else if (this.processType == null) {
            return -1;
        } else if (that.processType == null) {
            return 1;
        } else {
            int processTypeComparison = this.processType.compareTo(that.processType);
            if (processTypeComparison != 0) {
                return processTypeComparison < 0 ? -1 : 1;
            }
        }

        if (this.owner == null && that.owner == null) {
            // pass
        } else if (this.owner == null) {
            return -1;
        } else if (that.owner == null) {
            return 1;
        } else {
            int ownerComparison = this.owner.compareTo(that.owner);
            if (ownerComparison != 0) {
                return ownerComparison < 0 ? -1 : 1;
            }
        }

        if (this.uuid == null && that.uuid == null) {
            // pass
        } else if (this.uuid == null) {
            return -1;
        } else if (that.uuid == null) {
            return 1;
        } else {
            int uuidComparison = this.uuid.compareTo(that.uuid);
            if (uuidComparison != 0) {
                return uuidComparison < 0 ? -1 : 1;
            }
        }

        if (this.version == null && that.version == null) {
            return 0;
        } else if (this.version == null) {
            return -1;
        } else if (that.version == null) {
            return 1;
        } else {
            return this.version.compareTo(that.version);
        }
    }

}
