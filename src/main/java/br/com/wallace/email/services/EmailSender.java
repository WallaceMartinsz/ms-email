package br.com.wallace.email.services;

import br.com.wallace.email.models.EmailModel;

public interface EmailSender {
    void send(EmailModel email);
}
