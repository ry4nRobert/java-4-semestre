package com.uniruy.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarCodigoDeLogin(String emailDestino, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("contato.medlinksistemamedico@gmail.com"); 
        mensagem.setTo(emailDestino);
        mensagem.setSubject("MedLink - Seu Código de Acesso");
        mensagem.setText("Olá!\n\nObrigado por se registrar no MedLink.\n\nSeu código de acesso é: " + codigo + "\n\nUse este código para fazer login no sistema.\n\nAtenciosamente,\nEquipe MedLink");
        
        mailSender.send(mensagem);
    }

    @Async
    public void enviarCodigoRedefinicaoSenha(String emailDestino, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("contato.medlinksistemamedico@gmail.com"); 
        mensagem.setTo(emailDestino);
        mensagem.setSubject("MedLink - Código de Verificação");
        mensagem.setText("Olá!\n\nVocê solicitou a redefinição de senha.\n\nSeu código de verificação é: " + codigo + "\n\nEste código é válido por 30 minutos.\n\nSe você não solicitou esta redefinição, ignore este e-mail.\n\nAtenciosamente,\nEquipe MedLink");
        
        mailSender.send(mensagem);
    }

    
    @Async
    public void enviarNovaSenha(String emailDestino, String novaSenha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("contato.medlinksistemamedico@gmail.com"); 
        mensagem.setTo(emailDestino);
        mensagem.setSubject("MedLink - Sua Nova Senha Temporária");
        mensagem.setText("Olá!\n\nVocê solicitou uma nova senha.\n\nSua nova senha temporária é: " + novaSenha + "\n\nPor favor, use-a para fazer login e altere sua senha imediatamente.\n\nAtenciosamente,\nEquipe MedLink");
        
        mailSender.send(mensagem);
    }
}