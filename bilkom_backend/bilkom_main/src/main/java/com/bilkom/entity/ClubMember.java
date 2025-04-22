package com.bilkom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "club_members")
@IdClass(ClubMemberPK.class)
public class ClubMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
}

// Composite primary key class
class ClubMemberPK implements Serializable {
    private Long club; // Must match the field name "club" (not clubId)
    private Long member; // Must match the field name "member" (not memberId)
    
    // Required default constructor
    public ClubMemberPK() {}
    
    // Constructor, equals, and hashCode methods
    public ClubMemberPK(Long club, Long member) {
        this.club = club;
        this.member = member;
    }
    
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
    
    @Override
    public int hashCode() {
        return club.hashCode() ^ member.hashCode();
    }
}
