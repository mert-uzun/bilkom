package com.bilkom.entity;

import java.io.Serializable;
import java.util.Objects;

public class ClubExecutivePK implements Serializable {

    private User user;
    private Club club;

    public ClubExecutivePK() {}

    public ClubExecutivePK(User user, Club club) {
        this.user = user;
        this.club = club;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClubExecutivePK)) return false;
        ClubExecutivePK that = (ClubExecutivePK) o;
        return Objects.equals(user, that.user) && Objects.equals(club, that.club);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, club);
    }
}
