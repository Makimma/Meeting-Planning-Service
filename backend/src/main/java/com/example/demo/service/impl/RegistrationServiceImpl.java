package com.example.demo.service.impl;

import com.example.demo.exception.*;
import com.example.demo.entity.ConfirmationCode;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ConfirmationUserResponse;
import com.example.demo.response.RegistrationResponse;
import com.example.demo.response.ResendCodeResponse;
import com.example.demo.response.SendConfirmationResponse;
import com.example.demo.service.ConfirmationCodeService;
import com.example.demo.service.EmailSenderService;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.UserService;

import com.example.demo.util.ConfirmationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final ConfirmationCodeService confirmationCodeService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    @Autowired
    public RegistrationServiceImpl(ConfirmationCodeService confirmationCodeService,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   UserService userService, EmailSenderService emailSenderService) {
        this.confirmationCodeService = confirmationCodeService;
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
    public RegistrationResponse createNewUser(String username, String email, String password) {
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

        Optional<ConfirmationCode> optionalConfirmationToken = confirmationCodeService.findFirstByUserOrderByIdDesc(user);
        if (optionalConfirmationToken.isPresent() && optionalConfirmationToken.get().getExpiresAt().plusMinutes(15).isAfter(ZonedDateTime.now())) {
            throw new CodeNotExpiredException("Token already exist");
        }

        emailSenderService.sendEmail(email, "Почтится", generateCode(user));

        return RegistrationResponse.builder().timestamp(ZonedDateTime.now()).build();
    }

    @Transactional
    protected String generateCode(User user) {
        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(ConfirmationCodeGenerator.generateToken())
                .user(user)
                .createdAt(ZonedDateTime.now())
                .expiresAt(ZonedDateTime.now().plusMinutes(15))
                .build();
        return confirmationCodeService.save(confirmationCode).getCode();
    }

    @Override
    @Transactional
    public SendConfirmationResponse sendCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User not found");
        }

        ConfirmationCode currentConfirmationCode = confirmationCodeService.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new CodeNotFoundException("Token not found"));

        if (currentConfirmationCode.getConfirmedAt() == null && currentConfirmationCode.getExpiresAt().isAfter(ZonedDateTime.now())) {
            throw new CodeExpiredException("Cannot send confirmation code");
        }

        emailSenderService.sendEmail(email, "Почтится", generateCode(user));
        return SendConfirmationResponse.builder().timestamp(ZonedDateTime.now()).build();
    }

    @Override
    @Transactional
    public ResendCodeResponse resendCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ConfirmationCode currentConfirmationCode = confirmationCodeService.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new CodeNotFoundException("Token not found"));

        if (currentConfirmationCode.getConfirmedAt() != null) {
            throw new CodeNotExpiredException("Token not found");
        }

        if (currentConfirmationCode.getCreatedAt().plusMinutes(1).isAfter(ZonedDateTime.now())) {
            throw new CodeNotExpiredException("Token already exist");
        } else if (currentConfirmationCode.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new CodeExpiredException("Invalid token");
        }

        //TODO Заменить subject письма и само тело почты
        emailSenderService.sendEmail(email, "Почтится", generateCode(user));
        return ResendCodeResponse.builder().timestamp(ZonedDateTime.now()).build();
    }

    @Override
    @Transactional
    public ConfirmationUserResponse confirmCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ConfirmationCode currentConfirmationCode = confirmationCodeService.findFirstByUserOrderByIdDesc(user)
                .orElseThrow(() -> new CodeNotFoundException("Token not found"));

        if (currentConfirmationCode.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new CodeNotExpiredException("Token is expired");
        }

        if (!code.equals(currentConfirmationCode.getCode())) {
            throw new InvalidTokenException("Invalid token");
        }

        user.setEnabled(true);
        userRepository.save(user);

        currentConfirmationCode.setConfirmedAt(ZonedDateTime.now());
        confirmationCodeService.save(currentConfirmationCode);

        return ConfirmationUserResponse.builder().timestamp(ZonedDateTime.now()).build();
    }
}
