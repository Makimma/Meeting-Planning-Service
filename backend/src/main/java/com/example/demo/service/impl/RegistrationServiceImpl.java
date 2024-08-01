package com.example.demo.service.impl;

import com.example.demo.exception.*;
import com.example.demo.entity.ConfirmationToken;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendConfirmationResponse;
import com.example.demo.response.SendConfirmationResponse;
import com.example.demo.service.ConfirmationTokenService;
import com.example.demo.service.EmailSenderService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.UserService;

import com.example.demo.util.ConfirmationTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

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

    private String createLink(User user) {
        String link = user.getEmail().substring(0, user.getEmail().indexOf("@"));

        int i = 1;
        while (userRepository.findByLink(link).isPresent()) {
            if (i > 1) {
                link = link.substring(0, link.length() - 1);
            }
            link += i;
            ++i;
        }

        return link;
    }

    @Override
    @Transactional
    //TODO проверить
    public RegistrationResponse createNewUser(String username, String email, String password) throws MessagingException {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        user = existingUser.orElseGet(User::new);

        if (existingUser.isPresent() && existingUser.get().isEnabled()) {
            throw new UserAlreadyExistException("User already exist");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setLink(createLink(user));
        user = userService.save(user);

        Optional<ConfirmationToken> optionalConfirmationToken = confirmationTokenService.findFirstByUserOrderByIdDesc(user);
        if (optionalConfirmationToken.isPresent() && optionalConfirmationToken.get().getExpiresAt().plusMinutes(15).isAfter(ZonedDateTime.now())) {
            throw new TokensNotExpiredException("Token already exist");
        }

        sendConfirmationCode(user.getEmail());

        return RegistrationResponse.builder().timestamp(ZonedDateTime.now()).build();
    }

    //TODO протестить
    @Override
    @Transactional
    public SendConfirmationResponse sendConfirmationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(ConfirmationTokenGenerator.generateToken())
                .user(user)
                .createdAt(ZonedDateTime.now())
                .expiresAt(ZonedDateTime.now().plusMinutes(15))
                .build();
        confirmationTokenService.save(confirmationToken);

        emailSenderService.sendEmail(user.getEmail(), "Почтится", confirmationToken.getToken());
        return SendConfirmationResponse.builder().timestamp(ZonedDateTime.now()).build();
    }

    @Override
    @Transactional
    public ResendConfirmationResponse resendConfirmationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ConfirmationToken currentConfirmationToken = confirmationTokenService.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (currentConfirmationToken.getCreatedAt().plusMinutes(1).isAfter(ZonedDateTime.now())) {
            throw new TokensNotExpiredException("Token already exist");
        } else if (currentConfirmationToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new TokenIsExpiredException("Invalid token");
        }

        //TODO Отправить на почту

        return ResendConfirmationResponse.builder().timestamp(ZonedDateTime.now()).build();
    }


    @Override
    @Transactional
    public ConfirmationUserResponse confirmToken(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ConfirmationToken currentConfirmationToken = confirmationTokenService.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (currentConfirmationToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new TokensNotExpiredException("Token is expired");
        }

        if (!token.equals(currentConfirmationToken.getToken())) {
            throw new InvalidTokenException("Invalid token");
        }

        user.setEnabled(true);
        userRepository.save(user);

        currentConfirmationToken.setConfirmedAt(ZonedDateTime.now());
        confirmationTokenService.save(currentConfirmationToken);

        return ConfirmationUserResponse.builder().timestamp(ZonedDateTime.now()).build();
    }


}
