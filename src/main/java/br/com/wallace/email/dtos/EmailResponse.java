package br.com.wallace.email.dtos;


import br.com.wallace.email.models.EmailModel;

public record EmailResponse(Long emailId, String ownerRef, String emailFrom, String emailTo, String subject, String text) {

    public EmailResponse(EmailModel email){
        this(email.getEmailId(), email.getOwnerRef(), email.getEmailFrom(), email.getEmailTo(), email.getSubject(), email.getText());
    }

}
