package com.mej.biblioteca.service;

import com.mej.biblioteca.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:no-reply@biblioteca-mej.local}")
    private String remetente;

    public void enviarCodigoVerificacao(String destinatario, String assunto, String codigo) {
        if (!mailEnabled) {
            log.info("Envio de e-mail desabilitado. Destinatario={}, assunto={}, codigo={}", destinatario, assunto, codigo);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException("Envio de e-mail habilitado, mas o SMTP nao esta configurado.");
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText("Seu codigo de verificacao e: " + codigo + ". Ele expira em 5 minutos.");
        try {
            mailSender.send(mensagem);
        } catch (MailException exception) {
            throw new BusinessException("Nao foi possivel enviar o codigo de verificacao por e-mail.");
        }
    }
}
