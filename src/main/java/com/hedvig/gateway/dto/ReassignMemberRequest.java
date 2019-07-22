package com.hedvig.gateway.dto;

public class ReassignMemberRequest {
    private String oldMemberId;
    private String newMemberId;

    public ReassignMemberRequest() {}

    public ReassignMemberRequest(String oldMemberId, String newMemberId) {
        this.oldMemberId = oldMemberId;
        this.newMemberId = newMemberId;
    }

    public String getOldMemberId() {
        return oldMemberId;
    }

    public String getNewMemberId() {
        return newMemberId;
    }

    public void setOldMemberId(String oldMemberId) {
        this.oldMemberId = oldMemberId;
    }

    public void setNewMemberId(String newMemberId) {
        this.newMemberId = newMemberId;
    }
}
