package fittering.mall.service;

import fittering.mall.domain.CustomMailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import fittering.mall.domain.dto.MailDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static final String title = "[Fittering] 임시 비밀번호 안내 이메일입니다.";
    private static final String message = "안녕하세요. Fittering 임시 비밀번호 안내 메일입니다. "
            + "\n" + "회원님의 임시 비밀번호는 아래와 같습니다. 로그인 후 반드시 비밀번호를 변경해주세요." + "\n";

    @Value("${spring.mail.username}")
    private String fromAddress;

    public MailDto createMail(String tmpPassword, String to) {
        return new MailDto(fromAddress, to, title, message + tmpPassword);
    }

    public void sendMail(MailDto mailDto) {
        SimpleMailMessage mailMessage = new CustomMailMessage(mailDto.getTo(), mailDto.getTitle(),
                mailDto.getMessage(), mailDto.getFrom(), mailDto.getFrom());
        mailSender.send(mailMessage);
    }
}
