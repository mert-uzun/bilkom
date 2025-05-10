package com.bilkom.entity;

import java.io.Serializable;
import java.util.Objects;


public class ClubExecutivePK implements Serializable {

    private Long user;  // corresponds to user_id
    private Long club;  // corresponds to club_id

    public ClubExecutivePK() {}

    public ClubExecutivePK(Long user, Long club) {
        this.user = user;
        this.club = club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClubExecutivePK)) return false;
        ClubExecutivePK that = (ClubExecutivePK) o;
        return Objects.equals(user, that.user) &&
               Objects.equals(club, that.club);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, club);
    }
}