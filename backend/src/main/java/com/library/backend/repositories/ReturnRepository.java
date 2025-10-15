package com.library.backend.repositories;

import com.library.backend.entities.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Integer> {
    List<Return> findByBorrowStudentUserId(Integer studentId);
}
