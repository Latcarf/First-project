package com.latcarf.convert;

import com.latcarf.dto.UserDTO;
import com.latcarf.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserConvert {
    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(user);
    }
}
