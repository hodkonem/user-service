package ru.itwizardry.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.itwizardry.userservice.domain.User;
import ru.itwizardry.userservice.dto.UserCreateRequest;
import ru.itwizardry.userservice.dto.UserDto;
import ru.itwizardry.userservice.dto.UserUpdateRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserCreateRequest request);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget User user, UserUpdateRequest request);

    List<UserDto> toDtoList(List<User> users);
}
