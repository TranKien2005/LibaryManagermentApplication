package com.library.backend.repositories;

import com.library.backend.entities.Borrow;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    List<Borrow> findByStudentUserId(Integer studentId);

    List<Borrow> findByBorrowDateAfter(LocalDate date);

    Optional<Borrow> findByStudentUserIdAndBookIdAndBorrowDateAndExpectedReturnDateAndStatus(
            Integer studentId,
            Integer bookId,
            LocalDate borrowDate,
            LocalDate expectedReturnDate,
            Borrow.Type status
    );

    boolean existsByStudentUserIdAndBookIdAndStatus(
            Integer studentId,
            Integer bookId,
            Borrow.Type status
    );

}
