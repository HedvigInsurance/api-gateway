package com.hedvig.gateway.dto;

public class ReassignMemberRequest {
    private final String oldMemberId;
    private final String newMemberId;

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
}
