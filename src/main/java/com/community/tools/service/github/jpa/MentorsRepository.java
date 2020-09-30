package com.community.tools.service.github.jpa;


import java.util.Optional;
import com.community.tools.model.Mentors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorsRepository extends JpaRepository<Mentors,Long> {
  Optional<Mentors> findByGitNick (String gitNick);
}
