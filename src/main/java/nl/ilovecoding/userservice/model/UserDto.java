package nl.ilovecoding.userservice.model;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;
    private String name;
    private String email;
}
