package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_GROUPS_ND_TEAMS")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GRP_NAME", nullable = false, unique = true)
    private String grpName;

    @Column(name = "GRP_DESCRIPTION")
    private String grpDescription;

    @Column(name = "GROUP_OWNER")
    private String groupOwner;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    public GroupEntity() {
    }

    public GroupEntity(String grpName, String grpDescription, String groupOwner, String isActive) {
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

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public void setGrpDescription(String grpDescription) {
        this.grpDescription = grpDescription;
    }

    public void setGroupOwner(String groupOwner) {
        this.groupOwner = groupOwner;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
