package nl.ilovecoding.userservice.model;

import lombok.extern.slf4j.Slf4j;
import nl.ilovecoding.userservice.domain.UserType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
class UserDtoTest {

    @Test
    void userTypeTest() {

        UserDto user1 = new UserDto(1, "user1", "ab1@ilovecoding.nl", UserType.STANDARD);
        UserDto user2 = new UserDto(2, "user2", "ab2@ilovecoding.nl", UserType.STANDARD);
        UserDto user3 = new UserDto(3, "user3", "ab3@ilovecoding.nl", UserType.ADMIN);
        UserDto user4 = new UserDto(4, "user4", "ab4@ilovecoding.nl", UserType.STANDARD);
        UserDto user5 = new UserDto(5, "user5", "ab5@ilovecoding.nl", UserType.ADMIN);

        List<UserDto> users = List.of(user1, user2, user3, user4, user5);
        Map<UserType, List<UserDto>> groups = users.stream()
                .collect(Collectors.groupingBy(UserDto::userType));

        List<UserDto> adminGroup = groups.get(UserType.ADMIN);
        log.info("Admin group {}", adminGroup);
        assertThat(groups.size())
                .as("There should be 2 groups")
                .isEqualTo(2);
        assertThat(adminGroup.size()).isEqualTo(2);
        assertThat(adminGroup).contains(user3)
                .contains(user5)
                .doesNotContain(user1)
                .doesNotContain(user2)
                .doesNotContain(user4);


    }

}