package com.bilkom.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

/**
 * Composite primary key class for ClubMember entity.
 * This class represents the composite key consisting of club and member IDs
 * used to uniquely identify a ClubMember record.
 * Implements Serializable as required for JPA composite keys.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
@Embeddable
public class ClubMemberPK implements Serializable {
    private Long club; 
    private Long member;
    
    public ClubMemberPK() {}
    
    /**
     * Constructor for ClubMemberPK.
     * @param club The ID of the club.
     * @param member The ID of the member.
     */
    public ClubMemberPK(Long club, Long member) {
        this.club = club;
        this.member = member;
    }
    
    /**
     * Equals method for ClubMemberPK.
     * @param o The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        
        ClubMemberPK that = (ClubMemberPK) o;
        return club.equals(that.club) && member.equals(that.member);
    }
    
    /**
     * HashCode method for ClubMemberPK.
     * @return The hash code of the composite key.
     */
    @Override
    public int hashCode() {
        return club.hashCode() ^ member.hashCode();
    }

    /**
     * toString method for ClubMemberPK.
     * @return A string representation of the composite key.
     */
    @Override
    public String toString() {
        return "ClubMemberPK{" + "club=" + club + ", member=" + member + '}';
    }

    // GETTERS AND SETTERS
    public Long getClub() {
        return club;
    }

    public void setClub(Long club) {
        this.club = club;
    }

    public Long getMember() {
        return member;
    }

    public void setMember(Long member) {
        this.member = member;
    }
}