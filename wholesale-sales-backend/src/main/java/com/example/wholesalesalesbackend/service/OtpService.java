// package com.example.wholesalesalesbackend.service;


// import java.time.LocalDateTime;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.example.wholesalesalesbackend.dto.Msg91OtpRequest;
// import com.example.wholesalesalesbackend.model.OtpVerification;
// import com.example.wholesalesalesbackend.repository.OtpRepository;

// import reactor.core.publisher.Mono;

// @Service
// public class OtpService {

//     @Autowired
//     private JavaMailSender mailSender;

//     @Autowired
//     private OtpRepository otpRepository;

//     public void sendOtp(String email) {
//         String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
//         LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

//         OtpVerification otpEntity = new OtpVerification();
//         otpEntity.setEmail(email);
//         otpEntity.setOtp(otp);
//         otpEntity.setExpiryTime(expiry);
//         otpEntity.setVerified(false);
//         otpRepository.save(otpEntity);

//         sendEmail(email, otp);
//     }

//     public boolean verifyOtp(String email, String otp) {
//         Optional<OtpVerification> recordOpt = otpRepository.findTopByEmailOrderByIdDesc(email);
//         if (!recordOpt.isPresent()) return false;

//         OtpVerification record = recordOpt.get();
//         if (record.getExpiryTime().isBefore(LocalDateTime.now())) return false;
//         if (!record.getOtp().equals(otp)) return false;

//         record.setVerified(true);
//         otpRepository.save(record);
//         return true;
//     }

//     private void sendEmail(String to, String otp) {
//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(to);
//         message.setSubject("Your OTP Code");
//         message.setText("Your OTP is: " + otp + ". It is valid for 5 minutes.");
//         mailSender.send(message);
//     }
// }
