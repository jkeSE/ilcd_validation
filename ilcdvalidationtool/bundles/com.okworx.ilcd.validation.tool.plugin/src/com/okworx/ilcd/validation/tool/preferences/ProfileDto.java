package com.okworx.ilcd.validation.tool.preferences;

import com.okworx.ilcd.validation.tool.util.NaturalOrderComparator;

public class ProfileDto implements Comparable<ProfileDto> {

	private String name;
	private String version;
	private String path;

	public ProfileDto() {

	}

	public ProfileDto(String name, String version, String path) {
		this.name = name;
		this.version = version;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || (getClass() != other.getClass())) {
			return false;
		}
		ProfileDto oProfile = (ProfileDto) other;
		return oProfile.getName().equals(getName()) && oProfile.getVersion().equals(getVersion())
				&& oProfile.getPath().equals(getPath());
	}

	@Override
	public int compareTo(ProfileDto other) {
		if(equals(other) || other.getClass() != getClass()) {
			return 0;
		} 
		ProfileDto oProfile = (ProfileDto) other;
		
		if (getName().equals(oProfile.getName())) {
			return new NaturalOrderComparator().compare(getVersion(), oProfile.getVersion());
		} else {
			return new NaturalOrderComparator().compare(getName(), oProfile.getName());
		}
	}
	
	@Override
	public String toString() {
		return this.getName() + " v" + this.getVersion();
	}
}
