package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_SR_SEQUENCE")
public class SrSequenceEntity {

    @Id
    @Column(name = "SEQ_NAME")
    private String seqName;

    @Column(name = "NEXT_VAL", nullable = false)
    private Long nextVal;

    public String getSeqName() {
        return seqName;
    }

    public Long getNextVal() {
        return nextVal;
    }

    public void setNextVal(Long nextVal) {
        this.nextVal = nextVal;
    }
}
