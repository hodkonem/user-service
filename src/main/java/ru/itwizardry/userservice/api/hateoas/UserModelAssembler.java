package ru.itwizardry.userservice.api.hateoas;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.itwizardry.userservice.api.UserController;
import ru.itwizardry.userservice.dto.UserDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(UserDto user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll(null)).withRel("users"),
                linkTo(UserController.class).slash(user.id()).withRel("update"),
                linkTo(UserController.class).slash(user.id()).withRel("delete")
        );
    }
}
