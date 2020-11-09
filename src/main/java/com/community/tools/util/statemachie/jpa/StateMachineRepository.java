package com.community.tools.util.statemachie.jpa;

import java.util.Optional;
import com.community.tools.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMachineRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserID (String userID);
    Optional<User> findByGitName (String gitName);
}
