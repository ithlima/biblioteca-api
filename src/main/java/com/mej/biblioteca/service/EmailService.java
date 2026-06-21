package com.mej.biblioteca.service;

import com.mej.biblioteca.exception.domain.EmailEnvioException;
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

    public void enviarNotificacaoEmprestimo(String destinatario, String assunto, String corpoMensagem) {
        if (!mailEnabled) {
            log.info("Envio de e-mail desabilitado. Notificacao gerada para destinatário={}, assunto={}.",
                    mascararEmail(destinatario), assunto);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new EmailEnvioException("Envio de e-mail habilitado, mas o SMTP não está configurado.");
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText(corpoMensagem);

        log.info("""


                =======================================================
                 NOTIFICAÇÃO (MOCK/SMTP)
                 Destinatário: {}
                 Assunto: {}
                =======================================================
                """, destinatario, assunto);

        try {
            mailSender.send(mensagem);
            log.info("Notificação enviada para destinatário={}.", mascararEmail(destinatario));
        } catch (MailException exception) {
            log.warn("Falha ao enviar notificação real pelo SMTP para {}.", mascararEmail(destinatario));
        }
    }

    public void enviarCodigoVerificacao(String destinatario, String assunto, String codigo) {
        if (!mailEnabled) {
            log.info("Envio de e-mail desabilitado. Código gerado para destinatário={}, assunto={}.",
                    mascararEmail(destinatario), assunto);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new EmailEnvioException("Envio de e-mail habilitado, mas o SMTP não está configurado.");
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText("Seu código de verificação é: " + codigo + ". Ele expira em 5 minutos.");

        log.info("""


                =======================================================
                 CÓDIGO DE VERIFICAÇÃO (MOCK/SMTP)
                 Destinatário: {}
                 Código: {}
                =======================================================
                """, destinatario, codigo);

        try {
            mailSender.send(mensagem);
            log.info("Código de verificação enviado para destinatário={}.", mascararEmail(destinatario));
        } catch (MailException exception) {
            log.warn("Falha ao enviar e-mail real pelo SMTP para {}. Use o código impresso acima para continuar.",
                    mascararEmail(destinatario));
        }
    }

    private String mascararEmail(String email) {
        int separador = email.indexOf('@');
        if (separador <= 1) {
            return "***" + email.substring(Math.max(separador, 0));
        }
        return email.charAt(0) + "***" + email.substring(separador);
    }
}
