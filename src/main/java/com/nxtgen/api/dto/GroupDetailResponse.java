package com.nxtgen.api.dto;

/**
 * Full detail payload used to populate the Edit Group form when an existing
 * group record is opened from the Groups & Teams grid.
 */
public class GroupDetailResponse {

    private final Long id;
    private final String grpName;
    private final String grpDescription;
    private final String groupOwner;
    private final String isActive;

    public GroupDetailResponse(Long id, String grpName, String grpDescription, String groupOwner, String isActive) {
        this.id = id;
        this.grpName = grpName;
        this.grpDescription = grpDescription;
        this.groupOwner = groupOwner;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public String getGrpName() {
        return grpName;
    }

    public String getGrpDescription() {
        return grpDescription;
    }

    public String getGroupOwner() {
        return groupOwner;
    }

    public String getIsActive() {
        return isActive;
    }
}
