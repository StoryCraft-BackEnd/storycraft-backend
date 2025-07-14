package com.storycraft.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSesService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @Value("${aws.ses.from-name}")
    private String fromName;

    public void sendResetCodeEmail(String toEmail, String code) {
        try {
            String subject = "StoryCraft 비밀번호 재설정 인증 코드";
            String htmlBody = createResetCodeEmailHtml(code);
            String textBody = createResetCodeEmailText(code);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder().toAddresses(toEmail).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).charset("UTF-8").build())
                            .body(Body.builder()
                                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                                    .text(Content.builder().data(textBody).charset("UTF-8").build())
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("이메일 발송 성공: {} -> {}, MessageId: {}", fromEmail, toEmail, response.messageId());

        } catch (SesException e) {
            log.error("이메일 발송 실패: {} -> {}, Error: {}", fromEmail, toEmail, e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다: " + e.getMessage());
        }
    }

    private String createResetCodeEmailHtml(String code) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>StoryCraft 비밀번호 재설정</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .code { font-size: 24px; font-weight: bold; color: #4CAF50; text-align: center; padding: 20px; background-color: white; border: 2px solid #4CAF50; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>StoryCraft</h1>
                        </div>
                        <div class="content">
                            <h2>비밀번호 재설정 인증 코드</h2>
                            <p>안녕하세요! StoryCraft 비밀번호 재설정을 요청하셨습니다.</p>
                            <p>아래의 인증 코드를 입력해주세요:</p>
                            <div class="code">%s</div>
                            <p><strong>주의사항:</strong></p>
                            <ul>
                                <li>이 인증 코드는 10분 후에 만료됩니다.</li>
                                <li>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</li>
                                <li>인증 코드는 절대 다른 사람과 공유하지 마세요.</li>
                            </ul>
                        </div>
                        <div class="footer">
                            <p>이 이메일은 StoryCraft 시스템에서 자동으로 발송되었습니다.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(code);
    }

    private String createResetCodeEmailText(String code) {
        return """
                StoryCraft 비밀번호 재설정 인증 코드
                
                안녕하세요! StoryCraft 비밀번호 재설정을 요청하셨습니다.
                
                인증 코드: %s
                
                주의사항:
                - 이 인증 코드는 10분 후에 만료됩니다.
                - 본인이 요청하지 않은 경우 이 이메일을 무시하세요.
                - 인증 코드는 절대 다른 사람과 공유하지 마세요.
                
                이 이메일은 StoryCraft 시스템에서 자동으로 발송되었습니다.
                """.formatted(code);
    }
} 