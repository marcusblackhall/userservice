package nl.ilovecoding.userservice;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nl.ilovecoding.userservice.domain.User;
import nl.ilovecoding.userservice.mappers.UserMapper;
import nl.ilovecoding.userservice.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
@Slf4j
public class UserRestController {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public UserRestController(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Retrieving all users");
        List<User> users = userJpaRepository.findAll();
        return ResponseEntity.ok(
                users.stream().map(userMapper::userToUserDto).collect(Collectors.toList()));

    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable("id") Integer id, @Valid @RequestBody UserDto userDto) {
        Optional<User> user = userJpaRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User updatedUser = userMapper.userDtoToUser(userDto);
        updatedUser.setId(id);
        return ResponseEntity.ok(userMapper.userToUserDto(userJpaRepository.save(updatedUser)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id) {
        Optional<User> user = userJpaRepository.findById(id);

        return user.map(value -> ResponseEntity.ok(userMapper.userToUserDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {

        log.debug("Adding user: {} of type: {}",userDto.name(),userDto.userType());
        User user = userMapper.userDtoToUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userMapper.userToUserDto(userJpaRepository.save(user)));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {

        Optional<User> byId = userJpaRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userJpaRepository.deleteById(id);
        return ResponseEntity.ok().build();

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
