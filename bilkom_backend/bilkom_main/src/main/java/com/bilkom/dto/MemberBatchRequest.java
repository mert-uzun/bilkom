package com.bilkom.dto;

import java.util.List;

public class MemberBatchRequest {
    private List<Long> userIds;
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}
