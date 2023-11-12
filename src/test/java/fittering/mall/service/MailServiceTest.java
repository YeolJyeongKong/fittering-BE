package fittering.mall.service;

import fittering.mall.service.dto.CustomMailMessage;
import fittering.mall.service.dto.MailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    @DisplayName("발송할 메일 형태를 작성할 수 있다.")
    @Test
    void createMail() {
        //given
        String tempPassword = "tempPassword";
        String to = "iteratively@naver.com";

        //when
        MailDto result = mailService.createMail(tempPassword, to);

        //then
        assertThat(result).
                extracting("to", "title", "message")
                .containsExactlyInAnyOrder(
                        "iteratively@naver.com",
                        "[Fittering] 임시 비밀번호 안내 이메일입니다.",
                        "안녕하세요. Fittering 임시 비밀번호 안내 메일입니다. " + "\n"
                                + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요." + "\n"
                                + "tempPassword"
                );

    }

    @DisplayName("메일 전송 함수를 호출한다.")
    @Test
    void sendMail() {
        //given
        String tempPassword = "tempPassowrd";
        String to = "iteratively@naver.com";

        MailDto sendMailForm = mailService.createMail(tempPassword, to);
        doNothing().when(mailSender).send(any(CustomMailMessage.class));

        //when
        mailService.sendMail(sendMailForm);

        //then
        verify(mailSender, times(1)).send(any(CustomMailMessage.class));
    }
}