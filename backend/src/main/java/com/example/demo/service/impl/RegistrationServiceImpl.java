package com.example.demo.service.impl;

import com.example.demo.dto.UserRegistrationDTO;
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
    public ResponseEntity<?> createNewUser(UserRegistrationDTO userRegistrationDTO) throws MessagingException {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        /**
         * It allows numeric values from 0 to 9.
         * Both uppercase and lowercase letters from a to z are allowed.
         * Allowed are underscore “_”, hyphen “-“, and dot “.”
         * Dot isn’t allowed at the start and end of the local part.
         * Consecutive dots aren’t allowed.
         * For the local part, a maximum of 64 characters are allowed.
         */
        if (!Pattern.matches(emailRegex, userRegistrationDTO.getEmail())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Некорректный адрес электронной почты"),
                    HttpStatus.BAD_REQUEST);
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,32}$";
        /**
         * (?=.*[a-z]): makes sure that there is at least one small letter
         * (?=.*[A-Z]): needs at least one capital letter
         * (?=.*\\d): requires at least one digit
         * (?=.*[@#$%^&+=]): provides a guarantee of at least one special symbol
         * .{8,20}: imposes the minimum length of 8 characters and the maximum length of 20 characters
         */
        if (!Pattern.matches(passwordRegex, userRegistrationDTO.getPassword())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Некорректный пароль"),
                    HttpStatus.BAD_REQUEST);
        }
        //TODO: Сделать доп проверку что пользователь не имеет enabled = true в БД.
        // Если время истекло, то можно(надо спросить gpt)
        // Если имеет то отредачить пароль(это не надо делать, тк ниже в уже есть код)
        // Придумал: Надо при регитсрации смотреть на expiresAt и вообще наличие такого пользователя в системе
        // Если expiresAt истек, то можно продлить его
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDTO.getEmail());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(),
                            "Пользователь с таким адресом электронной почты уже существует"),
                    HttpStatus.BAD_REQUEST);
        }
        User user = new User(
                userRegistrationDTO.getEmail(),
                userRegistrationDTO.getPassword(),
                userRegistrationDTO.getUsername()
        );
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));

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

        String confirmationLink = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        emailSenderService.sendEmail(user.getEmail(), "Подтверждение регистрации", confirmationLink);
        return new ResponseEntity<>(
                new AppError(HttpStatus.OK.value(),
                        "Отправлена ссылка для подтверждения"),
                HttpStatus.OK);
    }


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
        return new ResponseEntity<>(
                new AppError(HttpStatus.OK.value(),
                        "confirmed"),
                HttpStatus.OK);
    }
}
