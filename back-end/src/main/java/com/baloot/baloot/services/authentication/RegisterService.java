package com.baloot.baloot.services.authentication;
import com.baloot.baloot.Exceptions.EmailAlreadyExistsException;
import com.baloot.baloot.Exceptions.UsernameAlreadyExistsException;
import com.baloot.baloot.services.BalootService;
import com.baloot.baloot.Exceptions.ForbiddenValueException;
import com.baloot.baloot.Exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeParseException;

@Service
public class RegisterService {
    @Autowired
    private BalootService balootService;

    public String registerUser(String username, String password, String email, String birthdate, String address) throws Exception {
        if(balootService.usernameExists(username))
            throw new UsernameAlreadyExistsException();
        if(balootService.emailExists(email))
            throw new EmailAlreadyExistsException();
        try {
            balootService.addUser(username, password, birthdate, email, address);
            return balootService.loginByEmail(email, password);
        }
        catch (DateTimeParseException e) {
            throw new ForbiddenValueException();
        }
    }
}
