package sample.test.dto;

import lombok.Getter;
import lombok.Setter;
import sample.test.model.Role;

import java.util.Set;
@Getter
@Setter
public class UpdateUserDto {
    private String userName;

    private Set<Role> roles;
}
