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
    StudentDetailResponse toStudentDetailResponse(Student student);
    Student toStudent(StudentCreationRequest request);
}
