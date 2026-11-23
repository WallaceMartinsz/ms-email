package br.com.wallace.email.services;

import br.com.wallace.email.models.EmailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
@Component
@Profile("!smtp")
@RequiredArgsConstructor
public class SesEmailSender implements EmailSender {

    private final SesClient ses;

    @Override
    public void send(EmailModel email) {
        var request = SendEmailRequest.builder()
                .source(email.getEmailFrom())
                .destination(Destination.builder().toAddresses(email.getEmailTo()).build())
                .message(Message.builder()
                        .subject(Content.builder().charset("UTF-8").data(email.getSubject()).build())
                        .body(Body.builder()
                                .text(Content.builder().charset("UTF-8").data(email.getText()).build())
                                .build())
                        .build())
                .build();

        var response = ses.sendEmail(request);
        log.info("E-mail {} -> SENT via SES (messageId={})", email.getEmailId(), response.messageId());
    }
}
