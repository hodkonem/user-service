package ru.itwizardry.userservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.itwizardry.userservice.api.error.ApiError;
import ru.itwizardry.userservice.api.hateoas.UserModelAssembler;
import ru.itwizardry.userservice.dto.UserCreateRequest;
import ru.itwizardry.userservice.dto.UserDto;
import ru.itwizardry.userservice.dto.UserUpdateRequest;
import ru.itwizardry.userservice.service.UserService;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static ru.itwizardry.userservice.api.docs.SwaggerExamples.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @Operation(summary = "Create user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_VALIDATION)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_EMAIL_EXISTS)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> create(@Valid @RequestBody UserCreateRequest request) {
        UserDto created = userService.createUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(created));
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_USER_NOT_FOUND)
                    )
            )
    })
    @GetMapping("/{id}")
    public EntityModel<UserDto> getById(
            @Parameter(description = "User id", example = "1")
            @PathVariable Long id
    ) {
        UserDto user = userService.getUserById(id);
        return assembler.toModel(user);
    }

    @Operation(summary = "Get users", description = "Returns all users or filters by age")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAll(
            @Parameter(description = "Optional age filter", example = "30")
            @RequestParam(required = false) Integer age
    ) {
        List<UserDto> users = (age != null)
                ? userService.findByAge(age)
                : userService.getAllUsers();

        List<EntityModel<UserDto>> content = users.stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(
                content,
                linkTo(methodOn(UserController.class).getAll(age)).withSelfRel()
        );
    }

    @Operation(summary = "Update user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_VALIDATION)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_USER_NOT_FOUND)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_EMAIL_EXISTS)
                    )
            )
    })
    @PutMapping("/{id}")
    public EntityModel<UserDto> update(
            @Parameter(description = "User id", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserDto updated = userService.updateUser(id, request);
        return assembler.toModel(updated);
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = ERROR_USER_NOT_FOUND)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User id", example = "1")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
