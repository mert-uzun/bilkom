package com.bilkom.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClubExecutiveId implements Serializable {

    private Long userId;
    private Long clubId;

    public ClubExecutiveId() {}

    public ClubExecutiveId(Long userId, Long clubId) {
        this.userId = userId;
        this.clubId = clubId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClubExecutiveId)) return false;
        ClubExecutiveId that = (ClubExecutiveId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(clubId, that.clubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, clubId);
    }
}
