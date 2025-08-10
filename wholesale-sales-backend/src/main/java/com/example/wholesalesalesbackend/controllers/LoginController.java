package com.example.wholesalesalesbackend.controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.wholesalesalesbackend.dto.EmailRequest;
import com.example.wholesalesalesbackend.dto.LoginRequest;
import com.example.wholesalesalesbackend.dto.ResetPassowrd;
import com.example.wholesalesalesbackend.dto.ResetPasswordRequest;
import com.example.wholesalesalesbackend.model.OtpVerification;
import com.example.wholesalesalesbackend.model.User;
import com.example.wholesalesalesbackend.repository.OtpRepository;
import com.example.wholesalesalesbackend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        return userRepository
                .findByUsernameOrMobileNumberOrMail(request.getUsername(), request.getMobileNumber(), request.getMail())
                .map(user -> {
                    if (user.getPassword().equals(request.getPassword())) {
                        return ResponseEntity.ok("Login successful!");
                    } else {
                        return ResponseEntity.status(401).body("Invalid password");
                    }
                })
                .orElse(ResponseEntity.status(401).body("User not found"));
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByUsernameOrMobileNumberOrMail(request.getUsername(),
                request.getMobileNumber(), request.getMail());

        if (user.isPresent()) {
            return ResponseEntity.status(400).body("User Name Already Exist");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setMail(request.getMail());
        newUser.setMobileNumber(request.getMobileNumber());
        newUser.setPassword(request.getPassword());
        userRepository.save(newUser);
        return ResponseEntity.status(200).body("User Added SuccessFully!!!");

    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> updateUserPassword(@RequestBody ResetPassowrd request) {
        Optional<User> user = userRepository.findByUsernameOrMobileNumberOrMail(request.getUserName(),
                request.getMobileNumber(), request.getMail());

        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("User Not Found!!!");
        }

        if (!user.get().getPassword().equals(request.getOldPassword())) {
            return ResponseEntity.status(400).body("Old Password is Wrong!!!");

        }

        User updatedUser = new User();
        updatedUser.setId(user.get().getId());
        updatedUser.setMail(user.get().getMail());
        updatedUser.setMobileNumber(user.get().getMobileNumber());
        updatedUser.setUsername(user.get().getUsername());
        updatedUser.setPassword(request.getNewPassword());
        userRepository.save(updatedUser);

        return ResponseEntity.status(200).body("Password updated SuccessFully!!!");

    }

    @PostMapping("/reset-password-mail") // submiit otp with new password
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {

        // Check if user exists
        Optional<User> userOptional = userRepository.findByMail(request.getEmail());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        Optional<OtpVerification> otp = otpRepository.findTopByEmailOrderByIdDesc(request.getEmail());

        if (!otp.isPresent()) {
            return ResponseEntity.status(404).body("OTP not found. Please request a new OTP.");
        }

        // Check if OTP is expired (older than 10 minutes)
        if (Duration.between(otp.get().getCreatedAt(), LocalDateTime.now()).toMinutes() > 10) {
            return ResponseEntity.status(400).body("OTP expired. Please request a new one.");
        }

        // Update password (you should ideally encode the password using
        // BCryptPasswordEncoder)
        User user = userOptional.get();
        user.setPassword(request.getNewPassword()); // replace with encoder.encode(...) if using encoder
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/send-otp") // forgot password
    public ResponseEntity<String> sendOtp(@RequestBody EmailRequest request) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpVerification otpEntity = new OtpVerification();
        otpEntity.setEmail(request.getEmail());
        otpEntity.setOtp(otp);
        otpEntity.setVerified(false);
        otpRepository.save(otpEntity);

        // Send mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("OTP Verification");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);

        return ResponseEntity.ok("OTP sent to email.");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate session
        request.getSession().invalidate();

        // Clear SecurityContext
        // SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logged out successfully");
    }
}
