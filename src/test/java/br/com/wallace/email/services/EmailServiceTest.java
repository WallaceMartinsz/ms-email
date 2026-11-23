package br.com.wallace.email.services;

import br.com.wallace.email.dtos.EmailRequest;
import br.com.wallace.email.enums.StatusEmail;
import br.com.wallace.email.messaging.EmailMessage;
import br.com.wallace.email.models.EmailModel;
import br.com.wallace.email.repositories.EmailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock EmailRepository repository;
    @Mock EmailSender sender;
    @Mock ApplicationEventPublisher events;
    @InjectMocks EmailService service;

    private EmailModel pending(long id) {
        var email = EmailModel.pendingFrom(new EmailRequest("ref", "from@x.com", "to@x.com", "Oi", "Texto"));
        email.setEmailId(id);
        return email;
    }

    @Test
    void enqueueSavesAsPendingAndPublishesEvent() {
        when(repository.save(any())).thenAnswer(inv -> {
            EmailModel e = inv.getArgument(0);
            e.setEmailId(1L);
            return e;
        });

        var result = service.enqueue(new EmailRequest("ref", "from@x.com", "to@x.com", "Oi", "Texto"));

        assertThat(result.getStatusEmail()).isEqualTo(StatusEmail.PENDING);
        verify(events).publishEvent(new EmailMessage(1L));
        verifyNoInteractions(sender);
    }

    @Test
    void processSendsAndMarksAsSent() {
        var email = pending(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(email));

        service.process(1L);

        verify(sender).send(email);
        assertThat(email.getStatusEmail()).isEqualTo(StatusEmail.SENT);
        assertThat(email.getSendDateEmail()).isNotNull();
    }

    @Test
    void processSkipsAlreadySentEmail() {
        var email = pending(1L);
        email.markAsSent();
        when(repository.findById(1L)).thenReturn(Optional.of(email));

        service.process(1L);

        verifyNoInteractions(sender);
    }

    @Test
    void processPropagatesFailureSoRetryKicksIn() {
        var email = pending(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(email));
        doThrow(new RuntimeException("SES fora")).when(sender).send(email);

        assertThatThrownBy(() -> service.process(1L)).hasMessage("SES fora");
        assertThat(email.getStatusEmail()).isEqualTo(StatusEmail.PENDING);
    }

    @Test
    void markAsErrorStoresReason() {
        var email = pending(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(email));

        service.markAsError(1L, "boom");

        assertThat(email.getStatusEmail()).isEqualTo(StatusEmail.ERROR);
        assertThat(email.getErrorMessage()).isEqualTo("boom");
    }
}
