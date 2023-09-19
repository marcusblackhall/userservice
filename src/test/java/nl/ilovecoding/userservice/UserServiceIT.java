package nl.ilovecoding.userservice;

import lombok.extern.slf4j.Slf4j;
import nl.ilovecoding.userservice.model.UserDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:15.4-alpine3.18://localhost:5432/userdb",
                "spring.datasource.username=postgres",
                "spring.datasource.password=marcus"
        }
)
@Sql(scripts = {"classpath:initdb/schema.sql"})
@Testcontainers
@Slf4j
public class UserServiceIT {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Autowired
    private TestRestTemplate restTemplate;

    private String restUrl;

    @BeforeEach
    void setup() {
        int port = webServerAppCtxt.getWebServer().getPort();
        log.info("port  in use {}", port);
        restUrl = "http://localhost:" + port + "/users";


    }

    @Test
    void shouldAddAUser() {

        UserDto body = addUser();
        assertThat(body.getName()).isEqualTo("John Ball");

    }

    @NotNull
    private UserDto addUser() {
        UserDto user = new UserDto();
        user.setName("John Ball");
        user.setEmail("jball@email.nl");
        ResponseEntity<UserDto> userDtoResponseEntity = restTemplate.postForEntity(restUrl, user, UserDto.class);
        UserDto body = userDtoResponseEntity.getBody();
        log.info("Created user {}", body.getId());
        return body;
    }

    @Test
    void shouldDeleteUser() {

        UserDto body = addUser();
        assertThat(body.getName()).isEqualTo("John Ball");

        restTemplate.delete(restUrl + "/" + body.getId());


    }

    @Test
    void shouldReturnNotFoundOnDeleteOfNonExistingUser() {

        ResponseEntity<Void> response = this.restTemplate.exchange(restUrl + "/333", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @AfterAll
    static void cleanup() {
        log.info("Finished user it tests");
    }


}
