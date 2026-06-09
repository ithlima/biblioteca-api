package com.mej.biblioteca.service;

import com.mej.biblioteca.exception.EmailEnvioException;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${app.mail.from:onboarding@resend.dev}")
    private String remetente;

    private Resend resend;

    @PostConstruct
    public void init() {
        if (StringUtils.hasText(resendApiKey)) {
            this.resend = new Resend(resendApiKey);
        }
    }

    public void enviarCodigoVerificacao(String destinatario, String assunto, String codigo) {
        if (!mailEnabled) {
            log.info("Envio de e-mail desabilitado. Código gerado para destinatário={}, assunto={}.",
                    mascararEmail(destinatario), assunto);
            return;
        }

        if (this.resend == null) {
            throw new EmailEnvioException("Envio de e-mail habilitado, mas a API Key do Resend não está configurada.");
        }

        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from(remetente)
                .to(destinatario)
                .subject(assunto)
                .html("<p>Seu código de verificação é: <strong>" + codigo + "</strong>. Ele expira em 5 minutos.</p>")
                .build();
        log.info("""


                =======================================================
                 CÓDIGO DE VERIFICAÇÃO (MOCK)
                 Destinatário: {}
                 Código: {}
                =======================================================
                """, destinatario, codigo);

        try {
            resend.emails().send(sendEmailRequest);
            log.info("Código de verificação enviado para destinatário={}.", mascararEmail(destinatario));
        } catch (Exception exception) {
            log.warn(
                    "Falha ao enviar e-mail real pelo Resend para {} (Normal em ambiente local sem domínio). Use o código impresso acima para continuar.",
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
