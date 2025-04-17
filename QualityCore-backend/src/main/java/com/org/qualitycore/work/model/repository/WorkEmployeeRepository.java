package com.org.qualitycore.work.model.repository;

import com.org.qualitycore.work.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkEmployeeRepository extends JpaRepository<Employee, String> { }
