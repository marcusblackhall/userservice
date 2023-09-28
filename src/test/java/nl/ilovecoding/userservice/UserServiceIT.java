package nl.ilovecoding.userservice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;
import nl.ilovecoding.userservice.model.UserDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15.4-alpine3.18://localhost:5432/userdb",
        "spring.datasource.username=postgres",
        "spring.datasource.password=marcus"
})
@Sql(scripts = { "classpath:initdb/schema.sql" })
@Testcontainers
@Slf4j
public class UserServiceIT {

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
    void shouldEditAUser() {
        UserDto user = addUser();
        user.setName("John Doe");
        user.setEmail("jdoe@email.nl");

        HttpEntity<UserDto> requestEntity = new HttpEntity<>(user);
        ResponseEntity<UserDto> responseEntity = restTemplate.exchange(restUrl + "/" + user.getId(), HttpMethod.PUT, requestEntity, UserDto.class);

        UserDto updatedUser = responseEntity.getBody();
        assertNotNull(updatedUser);
        assertThat(updatedUser.getName()).isEqualTo("John Doe");
        assertThat(updatedUser.getEmail()).isEqualTo("jdoe@email.nl");
    }

    @Test
    void shouldAddAUser() {

        UserDto body = addUser();
        assertThat(body.getName()).isEqualTo("John Ball");

    }

    @NotNull
    private UserDto addUser() {
        UserDto user = UserDto.builder()
                .name("John Ball")
                .email("jball@email.nl").build();

        ResponseEntity<UserDto> userDtoResponseEntity = restTemplate.postForEntity(restUrl, user, UserDto.class);
        UserDto body = userDtoResponseEntity.getBody();
        assertNotNull(body);
        log.info("Created user {}", body.getId());
        return body;
    }

    @Test
    void shouldDeleteUser() {

        UserDto body = addUser();
        assertThat(body.getName()).isEqualTo("John Ball");

        restTemplate.delete(restUrl + "/" + body.getId());
        ResponseEntity<UserDto> deletedUser = restTemplate.getForEntity(restUrl + "/" + body.getId(), UserDto.class);
        assertThat(deletedUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnNotFoundOnDeleteOfNonExistingUser() {

        ResponseEntity<Void> response = this.restTemplate.exchange(restUrl + "/333", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @AfterAll
    static void cleanup() {
        log.info("Finished user it tests");
    }

    @Test
    void shouldReturnEmailIsInvalid() {
        UserDto user = UserDto.builder().name("Fred").email("invalidemail").build();

        RequestEntity<UserDto> request = RequestEntity
                .post(URI.create(restUrl))
                .body(user);

        ResponseEntity<Map<String, String>> responseEntity = restTemplate
                .exchange(request, new ParameterizedTypeReference<>() {
                });

        Map<String, String> response = responseEntity.getBody();
        assertNotNull(response);
        assertThat(response.get("email")).isEqualTo("Invalid email address");
    }

}
