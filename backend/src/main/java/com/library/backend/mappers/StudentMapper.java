package com.library.backend.mappers;

import com.library.backend.dtos.requests.StudentCreationRequest;
import com.library.backend.dtos.responses.StudentDetailResponse;
import com.library.backend.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentMapper {
    default StudentDetailResponse toStudentDetailResponse(Student student) {
        StudentDetailResponse response = new StudentDetailResponse();
        response.setId(student.getUserId());
        response.setEmail(student.getUser().getEmail());
        response.setPhone(student.getUser().getPhone());
        response.setUsername(student.getUser().getUsername());
        response.setPassword(student.getUser().getPassword());
        response.setFullName(student.getUser().getFullName());
        return response;
    }
    Student toStudent(StudentCreationRequest request);

}
