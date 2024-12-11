package com.example.taskmanagement.infrastructure.repository;

import com.example.taskmanagement.infrastructure.entity.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskInfraRepository extends MongoRepository<TaskEntity, String> {
}
