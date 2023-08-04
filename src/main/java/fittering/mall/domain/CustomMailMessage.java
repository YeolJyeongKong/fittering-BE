package fittering.mall.domain;

import lombok.Builder;
import org.springframework.mail.SimpleMailMessage;

public class CustomMailMessage extends SimpleMailMessage {
    @Builder
    public CustomMailMessage(String to, String title, String msg, String from, String replyTo) {
        super.setTo(to);
        super.setSubject(title);
        super.setText(msg);
        super.setFrom(from);
        super.setReplyTo(replyTo);
    }
}
