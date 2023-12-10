package nl.ilovecoding.userservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import nl.ilovecoding.userservice.domain.UserType;

public record  UserDto(Integer id,
                      @NotEmpty(message = "A name must be entered") String name,
                      @Email(message = "Invalid email address")
                      @NotEmpty(message = "An email must be entered")
                      String email,
                      @JsonProperty("userType")
                       UserType userType
                       )

{
}

