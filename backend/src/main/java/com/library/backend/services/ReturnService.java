package com.library.backend.services;

import com.library.backend.dtos.requests.ReturnCreationRequest;
import com.library.backend.dtos.requests.ReturnUpdateRequest;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import com.library.backend.entities.Book;
import com.library.backend.entities.Borrow;
import com.library.backend.entities.Return;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.ReturnMapper;
import com.library.backend.repositories.BookRepository;
import com.library.backend.repositories.BorrowRepository;
import com.library.backend.repositories.ReturnRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnService {

    ReturnRepository returnRepository;
    ReturnMapper returnMapper;
    BorrowRepository borrowRepository;
    BookRepository bookRepository;

    public ReturnDetailResponse getById(Integer id) {
        Return r = returnRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.RETURN_NOT_FOUND));
        return returnMapper.toReturnDetailResponse(r);
    }

    public ReturnDetailResponse create(ReturnCreationRequest request) {
        Borrow borrow = borrowRepository.findById(request.getBorrowId())
                .orElseThrow(() -> new GeneralException(ResponseCode.BORROW_NOT_FOUND));
        borrow.setStatus(Borrow.Type.Returned);
        borrowRepository.save(borrow);
        Book book = bookRepository.findById(borrow.getBook().getId())
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        Return r = returnMapper.toReturn(request);
        r.setReturnDate(LocalDate.now());
        r.setBorrow(borrow);
        r.setReturnDate(LocalDate.now());
        r.setDamagePercentage(0);
        r = returnRepository.save(r);
        return returnMapper.toReturnDetailResponse(r);
    }

    public ReturnDetailResponse init_create(ReturnCreationRequest request) {
        Borrow borrow = borrowRepository.findById(request.getBorrowId())
                .orElseThrow(() -> new GeneralException(ResponseCode.BORROW_NOT_FOUND));
        borrow.setStatus(Borrow.Type.Returned);
        borrowRepository.save(borrow);
        Book book = bookRepository.findById(borrow.getBook().getId())
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        Return r = returnMapper.toReturn(request);
        r.setReturnDate(LocalDate.now());
        r.setBorrow(borrow);
        r.setReturnDate(LocalDate.now());
        r.setDamagePercentage(0);
        r = returnRepository.save(r);
        return returnMapper.toReturnDetailResponse(r);
    }

    public List<ReturnDetailResponse> getByStudentId(Integer studentId) {
        List<Return> rs = returnRepository.findByBorrowStudentUserId(studentId);
        return rs.stream().map(returnMapper::toReturnDetailResponse).toList();
    }

    public List<ReturnDetailResponse> getAll() {
        List<Return> rs = returnRepository.findAll();
        return rs.stream().map(returnMapper::toReturnDetailResponse).toList();
    }

    public List<ReturnDetailResponse> init_getAll() {
        List<Return> rs = returnRepository.findAll();
        return rs.stream().map(returnMapper::toReturnDetailResponse).toList();
    }

    public ReturnDetailResponse update(Integer id, ReturnUpdateRequest request) {
        Return r = returnRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.RETURN_NOT_FOUND));
        returnMapper.update(r, request);
        r = returnRepository.save(r);
        return returnMapper.toReturnDetailResponse(r);
    }

    public void delete(Integer id) {
        returnRepository.deleteById(id);
    }

    public boolean isInit() {
        return returnRepository.count() > 0;
    }

}
