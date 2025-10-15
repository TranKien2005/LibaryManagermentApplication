package com.library.backend.mappers;

import com.library.backend.dtos.requests.ManagerCreationRequest;
import com.library.backend.dtos.responses.ManagerDetailResponse;
import com.library.backend.entities.Manager;
import com.library.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ManagerMapper {
    Manager toManager(ManagerCreationRequest request);
    ManagerDetailResponse toManagerDetailResponse(Manager manager);
    void extraMap(@MappingTarget ManagerDetailResponse response, User user);
}
