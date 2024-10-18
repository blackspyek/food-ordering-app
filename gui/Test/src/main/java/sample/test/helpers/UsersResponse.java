package sample.test.helpers;

import lombok.Getter;
import lombok.Setter;
import sample.test.model.User;
import java.util.List;

@Setter
@Getter
public class UsersResponse {
    private List<User> data;
}
