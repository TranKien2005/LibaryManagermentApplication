package com.library.backend.repositories;

import com.library.backend.entities.Manager;
import com.library.backend.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    Optional<Manager> findByUserUsernameAndUserPassword(String username, String password);

}
