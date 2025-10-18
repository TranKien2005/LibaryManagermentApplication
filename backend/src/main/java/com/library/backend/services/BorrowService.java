package com.library.backend.services;

import com.library.backend.dtos.requests.BorrowCreationRequest;
import com.library.backend.dtos.requests.BorrowUpdateRequest;
import com.library.backend.dtos.responses.BorrowDetailResponse;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import com.library.backend.entities.Book;
import com.library.backend.entities.Borrow;
import com.library.backend.entities.Student;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.BorrowMapper;
import com.library.backend.mappers.ReturnMapper;
import com.library.backend.repositories.BookRepository;
import com.library.backend.repositories.BorrowRepository;
import com.library.backend.repositories.ReturnRepository;
import com.library.backend.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowService {

    BorrowRepository borrowRepository;
    BorrowMapper borrowMapper;
    BookRepository bookRepository;
    StudentRepository studentRepository;
    ReturnRepository returnRepository;
    ReturnMapper returnMapper;

    public List<BorrowDetailResponse> getAll() {
        List<Borrow> borrows = borrowRepository.findAll();
        return borrows.stream().map(borrowMapper::toBorrowDetailResponse).toList();
    }

    public BorrowDetailResponse create(BorrowCreationRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        if (book.getAvailableCopies() == 0) {
            throw new GeneralException(ResponseCode.NOT_ENOUGH_BOOK);
        }
        Student student = studentRepository.findById(request.getStudentId())
                        .orElseThrow(() -> new GeneralException(ResponseCode.STUDENT_NOT_FOUND));
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        Borrow borrow = borrowMapper.toBorrow(request);
        borrow.setStudent(student);
        borrow.setBook(Book.builder().id(request.getBookId()).build());
        borrow.setBorrowDate(LocalDate.now());
        borrow.setStatus(Borrow.Type.Borrowed);
        borrow = borrowRepository.save(borrow);
        return borrowMapper.toBorrowDetailResponse(borrow);
    }

    public BorrowDetailResponse update(Integer id, BorrowUpdateRequest request) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.BORROW_NOT_FOUND));
        borrowMapper.update(borrow, request);
        borrow = borrowRepository.save(borrow);
        return borrowMapper.toBorrowDetailResponse(borrow);
    }

    public void delete(Integer id) {
        borrowRepository.deleteById(id);
    }

    public BorrowDetailResponse getById(Integer id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.BORROW_NOT_FOUND));
        return borrowMapper.toBorrowDetailResponse(borrow);
    }

    public BorrowDetailResponse find(
            Integer studentId,
            Integer bookId,
            LocalDate borrowDate,
            LocalDate expectedReturnDate,
            Borrow.Type status
    ) {
        Borrow borrow = borrowRepository.findByStudentUserIdAndBookIdAndBorrowDateAndExpectedReturnDateAndStatus(
                studentId, bookId, borrowDate, expectedReturnDate, status
        ).orElseThrow(() -> new GeneralException(ResponseCode.BORROW_NOT_FOUND));
        return borrowMapper.toBorrowDetailResponse(borrow);
    }

    public List<Integer> getAllIds() {
        List<Borrow> borrows = borrowRepository.findAll();
        return borrows.stream().map(Borrow::getId).toList();
    }

    public boolean isBorrowed(Integer studentId, Integer bookId) {
        return borrowRepository.existsByStudentUserIdAndBookIdAndStatus(studentId, bookId, Borrow.Type.Borrowed);
    }

    public boolean isInit() {
        return borrowRepository.count() > 0;
    }

    // LEFT JOIN style helpers to combine Borrow with optional Return
    public List<ReturnDetailResponse> getAllBorrowReturns() {
        List<Borrow> borrows = borrowRepository.findAll();
        return borrows.stream().map(b ->
                returnRepository.findByBorrowId(b.getId())
                        .map(returnMapper::toReturnDetailResponse)
                        .orElseGet(() -> ReturnDetailResponse.builder()
                                .id(null)
                                .borrow(borrowMapper.toBorrowDetailResponse(b))
                                .returnDate(null)
                                .damagePercentage(null)
                                .build())
        ).toList();
    }

    public List<ReturnDetailResponse> getBorrowReturnsByStudentId(Integer studentId) {
        List<Borrow> borrows = borrowRepository.findByStudentUserId(studentId);
        return borrows.stream().map(b ->
                returnRepository.findByBorrowId(b.getId())
                        .map(returnMapper::toReturnDetailResponse)
                        .orElseGet(() -> ReturnDetailResponse.builder()
                                .id(null)
                                .borrow(borrowMapper.toBorrowDetailResponse(b))
                                .returnDate(null)
                                .damagePercentage(null)
                                .build())
        ).toList();
    }

}
