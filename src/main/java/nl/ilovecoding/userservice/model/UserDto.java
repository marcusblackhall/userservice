package nl.ilovecoding.userservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Integer id;

    @NotEmpty(message = "A name must be entered")
    private String name;

    @NotEmpty(message = "An email must be entered")
    @Email(message = "Invalid email address")
    private String email;
}
