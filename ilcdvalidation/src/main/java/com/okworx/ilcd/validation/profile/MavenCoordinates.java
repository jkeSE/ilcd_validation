package com.okworx.ilcd.validation.profile;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MavenCoordinates {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public MavenCoordinates(@Nonnull String groupId, @Nonnull String artifactId, @Nullable String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MavenCoordinates that = (MavenCoordinates) o;
        return groupId.equals(that.groupId) && artifactId.equals(that.artifactId) && Objects.equals(
            version, that.version);
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + artifactId.hashCode();
        result = 31 * result + Objects.hashCode(version);
        return result;
    }
}
