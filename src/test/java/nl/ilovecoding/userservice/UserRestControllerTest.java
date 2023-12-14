package nl.ilovecoding.userservice;

import nl.ilovecoding.userservice.domain.User;
import nl.ilovecoding.userservice.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserRestController.class)
@AutoConfigureMockMvc
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserJpaRepository userJpaRepository;

    @MockBean
    private UserMapper userMapper;

    @Test
    void createUser() throws Exception {

        String body = """
                { "name" : "fred",
                  "email" : "fred@gmail.com",
                  "userType" : "ADMIN"
                }
                """;


        User user = new User();

        when(userJpaRepository.save(any())).thenReturn(user);


        GrantedAuthority ga = new SimpleGrantedAuthority("users:write");

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .with(jwt().authorities(List.of(ga)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)

        ).andExpect(status().isCreated());

    }
}