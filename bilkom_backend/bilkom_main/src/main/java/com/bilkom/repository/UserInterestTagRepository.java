package com.bilkom.repository;

import com.bilkom.entity.UserInterestTag;
import com.bilkom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserInterestTagRepository extends JpaRepository<UserInterestTag, Long> {
    List<UserInterestTag> findByUser(User user);
}
