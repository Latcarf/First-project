package com.latcarf.testproject.repository;

import com.latcarf.testproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
