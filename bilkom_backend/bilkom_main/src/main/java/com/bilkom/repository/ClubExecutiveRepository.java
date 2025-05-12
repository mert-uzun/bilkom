package com.bilkom.repository;

import com.bilkom.entity.ClubExecutive;
import com.bilkom.entity.ClubExecutiveId;
import com.bilkom.entity.Club;
import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubExecutiveRepository extends JpaRepository<ClubExecutive, ClubExecutiveId> {

    List<ClubExecutive> findByClubClubId(Long clubId);
    Optional<ClubExecutive> findByUserUserIdAndClubClubId(Long userId, Long clubId);
    Optional<ClubExecutive> findByUserAndClub(User user, Club club);

    List<ClubExecutive> findByClubAndIsActiveTrue(Club club);
    List<ClubExecutive> findByUserAndIsActiveTrue(User user);
    List<ClubExecutive> findByUserUserIdAndIsActiveTrue(Long userId);

    boolean existsByUserAndClubAndIsActiveTrue(User user, Club club);
    boolean existsByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);
    boolean existsByUserUserIdAndClubClubId(Long userId, Long clubId);

    Optional<ClubExecutive> findByUserUserIdAndClubClubIdAndIsActiveTrue(Long userId, Long clubId);

    List<ClubExecutive> findByClub(Club club);
}
