package br.com.wallace.email.exceptions;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(Long id) {
        super("E-mail " + id + " não encontrado");
    }
}
