package com.food.backend.controller;

import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.User;
import com.food.backend.responses.LoginResponse;
import com.food.backend.service.AuthenticationService;
import com.food.backend.service.EmailService;
import com.food.backend.service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    // temporary
    private final EmailService emailService;
    @Value("${temp.email}")
    private String tempEmail;

    private final Logger logger = Logger.getLogger(AuthenticationController.class.getName());
    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, EmailService emailService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User user = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try{
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(token, jwtService.getJwtExpirationTime());
            return ResponseEntity.ok(loginResponse);

        }
        catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().
                    body(new LoginResponse("Invalid username or password", 0));
        }


    }

    // temporary for email testing
    @GetMapping("/sendtest")
    public ResponseEntity<String> sendTest() {
        sendTestEmail();
        return ResponseEntity.ok("Email sent");
    }

    public void sendTestEmail() {
        String subject = "Test Email";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendTestEmail(tempEmail, subject, htmlMessage);
        }
        catch (MessagingException e) {
            logger.warning("Error sending email: " + e.getMessage());
        }
    }

}
