package ru.itwizardry.userservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.itwizardry.userservice.api.hateoas.UserModelAssembler;
import ru.itwizardry.userservice.dto.UserCreateRequest;
import ru.itwizardry.userservice.dto.UserDto;
import ru.itwizardry.userservice.dto.UserUpdateRequest;
import ru.itwizardry.userservice.exception.EmailAlreadyExistsException;
import ru.itwizardry.userservice.exception.UserNotFoundException;
import ru.itwizardry.userservice.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserModelAssembler userModelAssembler;


    private static final LocalDateTime CREATED_AT = LocalDateTime.parse("2026-01-19T15:48:43.944");
    private static final String CREATED_AT_JSON = "2026-01-19T15:48:43.944";

    @Test
    void create_returns201_andLocationHeader() throws Exception {
        mockAssemblerToModel();

        var request = new UserCreateRequest("Ivan", "ivan@test.com", 25);
        var created = new UserDto(1L, "Ivan", "ivan@test.com", 25, CREATED_AT);

        Mockito.when(userService.createUser(any(UserCreateRequest.class))).thenReturn(created);


        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated()).andExpect(header().string("Location", containsString("/api/users/1"))).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Ivan")).andExpect(jsonPath("$.email").value("ivan@test.com")).andExpect(jsonPath("$.age").value(25)).andExpect(jsonPath("$.createdAt").value(CREATED_AT_JSON));
    }

    @Test
    void getById_returns200() throws Exception {
        mockAssemblerToModel();
        var dto = new UserDto(1L, "Ivan", "ivan@test.com", 25, CREATED_AT);
        Mockito.when(userService.getUserById(1L)).thenReturn(dto);


        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.createdAt").value(CREATED_AT_JSON));
    }

    @Test
    void getById_whenNotFound_returns404_withApiError() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(get("/api/users/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.code").value("USER_NOT_FOUND")).andExpect(jsonPath("$.path").value("/api/users/1"));
    }

    @Test
    void getAll_withoutAge_returns200_list() throws Exception {
        mockAssemblerToModel();
        mockAssemblerToCollectionModel();

        var users = List.of(new UserDto(1L, "Ivan", "ivan@test.com", 25, CREATED_AT), new UserDto(2L, "Petr", "petr@test.com", 30, CREATED_AT));
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.userDtoList.length()").value(2)).andExpect(jsonPath("$._embedded.userDtoList[0].id").value(1)).andExpect(jsonPath("$._embedded.userDtoList[0].createdAt").value(CREATED_AT_JSON));
    }

    @Test
    void getAll_withAgeParam_returns200_filtered() throws Exception {
        mockAssemblerToModel();
        mockAssemblerToCollectionModel();

        var users = List.of(new UserDto(1L, "Ivan", "ivan@test.com", 25, CREATED_AT));
        Mockito.when(userService.findByAge(25)).thenReturn(users);
        mockMvc.perform(get("/api/users").param("age", "25")).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.userDtoList.length()").value(1)).andExpect(jsonPath("$._embedded.userDtoList[0].age").value(25));
    }

    @Test
    void update_returns200() throws Exception {
        mockAssemblerToModel();
        var request = new UserUpdateRequest("Ivan Updated", "ivan@test.com", 26);
        var updated = new UserDto(1L, "Ivan Updated", "ivan@test.com", 26, CREATED_AT);

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Ivan Updated")).andExpect(jsonPath("$.age").value(26)).andExpect(jsonPath("$.createdAt").value(CREATED_AT_JSON));
    }

    @Test
    void update_whenEmailExists_returns409_withApiError() throws Exception {
        var request = new UserUpdateRequest("Ivan", "dup@test.com", 25);

        Mockito.when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenThrow(new EmailAlreadyExistsException("dup@test.com"));

        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS")).andExpect(jsonPath("$.path").value("/api/users/1"));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    void create_whenValidationFails_returns400() throws Exception {

        var badJson = """
                {"name":"", "email":"not-email", "age":0}
                """;

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(badJson)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.code").value("VALIDATION_ERROR")).andExpect(jsonPath("$.path").value("/api/users"));
    }

    private void mockAssemblerToModel() {
        Mockito.when(userModelAssembler.toModel(any(UserDto.class))).thenAnswer(inv -> EntityModel.of(inv.getArgument(0)));
    }

    @SuppressWarnings("unchecked")
    private void mockAssemblerToCollectionModel() {
        Mockito.when(userModelAssembler.toCollectionModel(any())).thenAnswer(inv -> {
            var list = (List<UserDto>) inv.getArgument(0);
            var models = list.stream().map(EntityModel::of).toList();
            return CollectionModel.of(models);
        });
    }

}
