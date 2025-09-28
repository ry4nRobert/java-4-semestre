package com.uniruy.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Controller
public class PageController {

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String paginaLogin(Model model) {
        model.addAttribute("usuario", new Registro());
        return "login";
    }
    
    @GetMapping("/registro")
    public String paginaRegistro(Model model) {
        model.addAttribute("usuario", new Registro());
        return "registro";
    }
    
    @GetMapping("/esqueceu-senha")
    public String paginaEsqueceuSenha() {
        return "esqueceu-senha";
    }

    @GetMapping("/codigo-verificacao")
    public String paginaCodigoVerificacao(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "codigo-verificacao";
    }

    @GetMapping("/nova-senha")
    public String paginaNovaSenha(@RequestParam("token") String token, Model model) {
        Optional<Registro> usuarioOptional = registroRepository.findByResetToken(token);
        
        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            if (usuario.getResetTokenExpiryDate() != null && 
                usuario.getResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
                model.addAttribute("token", token);
                return "nova-senha";
            }
        }
        
        return "redirect:/esqueceu-senha?error=token-invalido";
    }

    @GetMapping("/home")
    public String paginaDashboard(Model model) {
        model.addAttribute("mensagem", "Bem-vindo ao MedLink!");
        return "home";
    }
    

    @PostMapping("/registro")
    public String registrarUsuario(Registro usuario) {
        String codigo;
        do {
            codigo = gerarCodigoDe8Digitos();
        } while (registroRepository.existsByCodigoLogin(codigo));

        usuario.setCodigoLogin(codigo);
        registroRepository.save(usuario);

        try {
            emailService.enviarCodigoDeLogin(usuario.getEmail(), usuario.getCodigoLogin());
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação: " + e.getMessage());
        }

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String processarLogin(@RequestParam("codigoLogin") String codigoLogin,
                                 @RequestParam("senha") String senha) {
        Optional<Registro> usuarioOptional = registroRepository.findByCodigoLogin(codigoLogin);

        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            if (usuario.getSenha() != null && usuario.getSenha().equals(senha)) {
                System.out.println("Login bem-sucedido para o usuário: " + usuario.getNome());
                return "redirect:/home";
            } else {
                System.out.println("Senha incorreta para o usuário: " + codigoLogin);
                return "redirect:/login?error=senha";
            }
        } else {
            System.out.println("Tentativa de login falhou com o código: " + codigoLogin);
            return "redirect:/login?error=codigo";
        }
    }

    @PostMapping("/esqueceu-senha")
    public String processarEsqueceuSenha(@RequestParam("email") String email, Model model) {
        Optional<Registro> usuarioOptional = registroRepository.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            
            String resetToken = gerarTokenSeguro();
            usuario.setResetToken(resetToken);
            usuario.setResetTokenExpiryDate(LocalDateTime.now().plusMinutes(30));
            registroRepository.save(usuario);

            try {
                emailService.enviarCodigoRedefinicaoSenha(usuario.getEmail(), resetToken);
                return "redirect:/codigo-verificacao?email=" + email;
            } catch (Exception e) {
                System.err.println("Erro ao enviar e-mail de redefinição: " + e.getMessage());
                return "redirect:/esqueceu-senha?error=email";
            }
        } else {
            return "redirect:/esqueceu-senha?error=email-nao-encontrado";
        }
    }

    
    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam("email") String email, 
                                 @RequestParam("codigo") String codigo) {
        
        Optional<Registro> usuarioOptional = registroRepository.findByEmail(email);
        
        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            
            
            if (usuario.getResetToken() != null && codigo.equals(usuario.getResetToken())) {
                
                if (usuario.getResetTokenExpiryDate() != null && 
                    usuario.getResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
                    return "redirect:/nova-senha?token=" + codigo;
                } else {
                    return "redirect:/codigo-verificacao?error=token-expirado&email=" + email;
                }
            } else {
                return "redirect:/codigo-verificacao?error=codigo-invalido&email=" + email;
            }
        }
        
        return "redirect:/codigo-verificacao?error=email-nao-encontrado&email=" + email;
    }

    
    @PostMapping("/definir-nova-senha")
    public String definirNovaSenha(@RequestParam("token") String token,
                                  @RequestParam("novaSenha") String novaSenha,
                                  @RequestParam("confirmarSenha") String confirmarSenha) {
        
        if (!novaSenha.equals(confirmarSenha)) {
            return "redirect:/nova-senha?error=senhas-nao-coincidem&token=" + token;
        }
        
        Optional<Registro> usuarioOptional = registroRepository.findByResetToken(token);
        
        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            
           
            if (usuario.getResetTokenExpiryDate() != null && 
                usuario.getResetTokenExpiryDate().isAfter(LocalDateTime.now())) {
                usuario.setSenha(novaSenha);
                usuario.setResetToken(null);
                usuario.setResetTokenExpiryDate(null);
                registroRepository.save(usuario);
                
                return "redirect:/login?reset=success";
            } else {
                return "redirect:/esqueceu-senha?error=token-expirado";
            }
        }
        
        return "redirect:/esqueceu-senha?error=token-invalido";
    }
    
    private String gerarTokenSeguro() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String gerarCodigoDe8Digitos() {
        Random random = new SecureRandom();
        int numero = 10000000 + random.nextInt(90000000);
        return String.valueOf(numero);
    }
}