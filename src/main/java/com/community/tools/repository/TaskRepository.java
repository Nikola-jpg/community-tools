package com.community.tools.repository;

import com.community.tools.model.Task;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

  Task findByUserIdAndTaskNumber(String userId, Integer taskNumber);
}
