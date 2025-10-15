package com.library.backend.mappers;

import com.library.backend.dtos.requests.UserCreationRequest;
import com.library.backend.dtos.requests.UserUpdateRequest;
import com.library.backend.dtos.responses.UserDetailResponse;
import com.library.backend.dtos.responses.V1AccountResponse;
import com.library.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    User toUser(UserCreationRequest request);

    void update(@MappingTarget User user, UserUpdateRequest request);

    UserDetailResponse toUserDetailResponse(User user);

    V1AccountResponse toV1AccountResponse(User user);

}
