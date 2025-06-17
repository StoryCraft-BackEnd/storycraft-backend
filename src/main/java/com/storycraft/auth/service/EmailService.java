package com.storycraft.auth.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public void sendResetCode(String toEmail, String code) {

        if (!userRepository.existsByEmail(toEmail)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("StoryCraft 비밀번호 재설정 인증 코드");
        message.setText("안녕하세요, 인증 코드는 " + code + " 입니다. 10분 내에 입력하세요.");
        mailSender.send(message);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
