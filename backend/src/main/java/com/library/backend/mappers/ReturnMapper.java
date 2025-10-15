package com.library.backend.mappers;

import com.library.backend.dtos.requests.ReturnCreationRequest;
import com.library.backend.dtos.requests.ReturnUpdateRequest;
import com.library.backend.dtos.responses.ReturnDetailResponse;
import com.library.backend.entities.Return;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
                BorrowMapper.class
        }
)
public interface ReturnMapper {

    ReturnDetailResponse toReturnDetailResponse(Return r);

    Return toReturn(ReturnCreationRequest request);

    void update(@MappingTarget Return r, ReturnUpdateRequest request);

}
