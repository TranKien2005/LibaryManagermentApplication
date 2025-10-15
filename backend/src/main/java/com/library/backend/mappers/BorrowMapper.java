package com.library.backend.mappers;

import com.library.backend.dtos.requests.BorrowCreationRequest;
import com.library.backend.dtos.requests.BorrowUpdateRequest;
import com.library.backend.dtos.responses.BorrowDetailResponse;
import com.library.backend.entities.Borrow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
                StudentMapper.class,
                BookMapper.class
        }
)
public interface BorrowMapper {

    BorrowDetailResponse toBorrowDetailResponse(Borrow borrow);

    Borrow toBorrow(BorrowCreationRequest request);

    void update(@MappingTarget Borrow borrow, BorrowUpdateRequest request);

}
