package com.example.demo.service.impl;

import com.example.demo.dto.RegistrationRequestDTO;
import com.example.demo.entity.ConfirmationToken;
import com.example.demo.entity.User;
import com.example.demo.exception.AppError;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ConfirmationTokenService;
import com.example.demo.service.EmailSenderService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    @Autowired
    public RegistrationServiceImpl(ConfirmationTokenService confirmationTokenService,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   UserService userService, EmailSenderService emailSenderService) {
        this.confirmationTokenService = confirmationTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    @Override
    @Transactional
    public ResponseEntity<?> createNewUser(RegistrationRequestDTO registrationRequestDTO) throws MessagingException {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!Pattern.matches(emailRegex, registrationRequestDTO.getEmail())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Некорректный адрес электронной почты"),
                    HttpStatus.BAD_REQUEST);
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,32}$";
        if (!Pattern.matches(passwordRegex, registrationRequestDTO.getPassword())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Некорректный пароль"),
                    HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepository.findByEmail(registrationRequestDTO.getEmail());
        if (existingUser.isPresent()) {
            if (existingUser.get().isEnabled()) {
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(),
                                "Пользователь с таким адресом электронной почты уже существует"),
                        HttpStatus.BAD_REQUEST);
            } else {
                confirmationTokenService.deleteByUserId(existingUser.get().getId());
                userService.deleteById(existingUser.get().getId());
            }
        }

        User user = new User(
                registrationRequestDTO.getEmail(),
                registrationRequestDTO.getPassword(),
                registrationRequestDTO.getUsername()
        );
        user.setPassword(passwordEncoder.encode(registrationRequestDTO.getPassword()));

        String link = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        int i = 1;
        while (userRepository.findByLink(link).isPresent()) {
            if (i > 1) {
                link = link.substring(0, link.length() - 1);
            }
            link += i;
            ++i;
        }
        user.setLink(link);
        userService.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                user,
                token,
                new Date(),
                new Date((new Date()).getTime() + 900000)
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);


        //TODO: Change confirmation link
        String confirmationLink = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        emailSenderService.sendEmail(user.getEmail(), "Подтверждение регистрации", confirmationLink);
        return new ResponseEntity<>("Отправлена ссылка для подтверждения", HttpStatus.OK);
    }


    @Override
    @Transactional
    public ResponseEntity<?> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElse(null);
        if (confirmationToken == null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "token not found"),
                    HttpStatus.BAD_REQUEST);
        }
        if (confirmationToken.getConfirmedAt() != null) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "token already confirmed"),
                    HttpStatus.BAD_REQUEST);
        }

        Date expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.before(new Date())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "token expired"),
                    HttpStatus.BAD_REQUEST);
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());
        return new ResponseEntity<>("confirmed", HttpStatus.OK);
    }
}
