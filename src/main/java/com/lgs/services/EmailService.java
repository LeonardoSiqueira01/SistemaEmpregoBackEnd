package com.lgs.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.lgs.entities.Client;
import com.lgs.entities.Professional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendActivationEmail(String recipientEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Ativação de Conta");
        
        // Criar um link com o código de verificação
        String activationLink = "http://localhost:8080/api/verify?code=" + verificationCode;
        
        // O corpo do e-mail
        message.setText("Digite o código para ativar sua conta: " + verificationCode + "\n\n"
                + "Ou se preferir, clique no link abaixo para ativar sua conta:\n" + activationLink);
        message.setFrom("Lgs.System01@gmail.com");

        javaMailSender.send(message);
    }
    
    public void enviarNotificacaoParaProfissional(Professional profissional, com.lgs.entities.Service service) throws MessagingException {
        String assunto = "O cliente " + service.getClient().getName() + " lhe ofereceu uma oportunidade!";
        
        // Construindo os links de aceitação e recusa com base nos endpoints do ProfessionalController
        String aceitarLink = "http://127.0.0.1:5500/Login/index.html" ;
        String recusarLink = "http://127.0.0.1:5500/Login/index.html" ;

        // Corpo do e-mail com links de aceitação e recusa e CSS
        String mensagem = "<html><head>" +
        	    "<meta charset=\"UTF-8\">" +
        	    "<style>" +
        	    "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; color: #333; }" +
        	    ".container { background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto; }" +
        	    "h1 { color: #4CAF50; font-size: 24px; margin-bottom: 20px; }" +
        	    "p { font-size: 16px; line-height: 1.5; }" +
        	    "a { text-decoration: none; color: #fff; background-color: #007bff; padding: 10px 20px; border-radius: 5px; margin-top: 10px; display: inline-block; }" +
        	    "a:hover { background-color: #0056b3; }" +
        	    ".footer { font-size: 12px; color: #888; text-align: center; margin-top: 30px; }" +
        	    "</style>" +
        	    "</head><body>" +
        	    "<div class='container'>" +
        	    "<h1>O Cliente " + service.getClient().getName() + " lhe ofereceu uma oportunidade de serviço!</h1>" +
        	    "<p>O Cliente " + service.getClient().getName() + " lhe ofereceu uma oportunidade de serviço: <strong>" + service.getName() + "</strong>.</p>" +
        	    "<p><a href='" + aceitarLink + "'>Aceitar Serviço</a></p>" +
        	    "<p><a href='" + recusarLink + "'>Recusar Serviço</a></p>" +
        	    "<div class='footer'>" +
        	    "<p>Se você tiver dúvidas, entre em contato conosco.</p>" +
        	    "</div>" +
        	    "</div>" +
        	    "</body></html>";


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(profissional.getEmail());
        helper.setSubject(assunto);
        helper.setText(mensagem, true); // true indica que a mensagem está em HTML

        javaMailSender.send(mimeMessage);
    }
    
    
    public void enviarNotificacaoParaCliente(Client cliente, String nomeServico, String nomeProfissional, boolean aceito) throws MessagingException {
        // Definindo o assunto do e-mail
        String assunto = "Seu serviço " + nomeServico + " foi " + (aceito ? "aceito" : "recusado") + " pelo profissional";

        // Corpo do e-mail com HTML e CSS
        String mensagem = "<html><head>" +
                "<meta charset=\"UTF-8\">" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; color: #333; }" +
                ".container { background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); max-width: 600px; margin: auto; }" +
                "h1 { color: #4CAF50; font-size: 24px; margin-bottom: 20px; }" +
                "p { font-size: 16px; line-height: 1.5; }" +
                "strong { font-weight: bold; }" +
                ".footer { font-size: 12px; color: #888; text-align: center; margin-top: 30px; }" +
                "</style>" +
                "</head><body>" +
                "<div class='container'>" +
                "<h1>Notificação sobre o Serviço: " + nomeServico + "</h1>" +
                "<p>O profissional <strong>" + nomeProfissional + "</strong> " + (aceito ? "aceitou" : "recusou") + " o serviço: <strong>" + nomeServico + "</strong>.</p>" +
                "<div class='footer'>" +
                "<p>Se você tiver dúvidas, entre em contato conosco.</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";

        try {
            // Criando o e-mail em formato MIME
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Configurando os destinatários e o assunto
            helper.setTo(cliente.getEmail());
            helper.setSubject(assunto);
            helper.setText(mensagem, true); // true indica que a mensagem está em HTML

            // Enviando o e-mail
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Captura erros e fornece informações úteis
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }


    
}
