package com.library.backend.services;

import com.library.backend.dtos.requests.StudentCreationRequest;
import com.library.backend.dtos.responses.StudentDetailResponse;
import com.library.backend.entities.Student;
import com.library.backend.entities.User;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.StudentMapper;
import com.library.backend.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {

    StudentRepository studentRepository;
    StudentMapper studentMapper;

    public StudentDetailResponse create(StudentCreationRequest request) {
        Student student = studentMapper.toStudent(request);
        student = studentRepository.save(student);
        return StudentDetailResponse.builder()
                .id(student.getUserId())
                .fullName(student.getUser().getFullName())
                .email(student.getUser().getEmail())
                .phone(student.getUser().getPhone())
                .build();
    }

    public StudentDetailResponse getById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.STUDENT_NOT_FOUND));
        return StudentDetailResponse.builder()
                .id(student.getUserId())
                .fullName(student.getUser().getFullName())
                .email(student.getUser().getEmail())
                .phone(student.getUser().getPhone())
                .build();
    }

    public List<Integer> getAllIds() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(Student::getUserId).toList();
    }

    public List<StudentDetailResponse> getAll() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(
                student -> StudentDetailResponse.builder()
                        .id(student.getUserId())
                        .fullName(student.getUser().getFullName())
                        .email(student.getUser().getEmail())
                        .phone(student.getUser().getPhone())
                        .build()
        ).toList();
    }

    public StudentDetailResponse getByUsernameAndPassword(String username, String password) {
        Student student = studentRepository.findByUserUsernameAndUserPassword(username, password)
                .orElseThrow(() -> new GeneralException(ResponseCode.STUDENT_NOT_FOUND));
        return StudentDetailResponse.builder()
                .id(student.getUserId())
                .fullName(student.getUser().getFullName())
                .email(student.getUser().getEmail())
                .phone(student.getUser().getPhone())
                .build();
    }

    public boolean isInit() {
        return studentRepository.count() > 0;
    }



}
