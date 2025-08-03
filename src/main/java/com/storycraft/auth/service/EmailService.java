package com.storycraft.auth.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AwsSesService awsSesService;
    private final UserRepository userRepository;

    public void sendResetCode(String toEmail, String code) {
        if (!userRepository.existsByEmail(toEmail)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }

        try {
            awsSesService.sendResetCodeEmail(toEmail, code);
            log.info("비밀번호 재설정 인증코드 발송 완료: {}", toEmail);
        } catch (Exception e) {
            log.error("비밀번호 재설정 인증코드 발송 실패: {}, Error: {}", toEmail, e.getMessage());
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
