package nl.ilovecoding.userservice;


import lombok.RequiredArgsConstructor;
import nl.ilovecoding.userservice.domain.User;
import nl.ilovecoding.userservice.mappers.UserMapper;
import nl.ilovecoding.userservice.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserJpaRepository userJpaRepository;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userJpaRepository.findAll();
        return ResponseEntity.ok(
                users.stream().map(userMapper::userToUserDto).collect(Collectors.toList())
        );

    }


    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {

        User user = userMapper.userDtoToUser(userDto);
        return
                ResponseEntity.ok(
                        userMapper.userToUserDto(userJpaRepository.save(user))
                );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable Integer id) {

        Optional<User> byId = userJpaRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userJpaRepository.deleteById(id);
        return ResponseEntity.ok().build();

    }
}
