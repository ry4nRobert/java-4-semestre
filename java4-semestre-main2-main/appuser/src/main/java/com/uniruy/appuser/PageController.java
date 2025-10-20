package com.uniruy.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.HashSet;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

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
        model.addAttribute("especialidades", especialidadeRepository.findAll());
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
    public String paginaDashboard(Model model, HttpSession session) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        
        if (usuarioId == null) {
            return "redirect:/login"; 
        }

        Optional<Registro> usuarioOptional = registroRepository.findById(usuarioId);

        if (!usuarioOptional.isPresent()) {
            session.invalidate(); 
            return "redirect:/login";
        }
        
        Registro medicoLogado = usuarioOptional.get();
        model.addAttribute("medico", medicoLogado); 
        
        // CONTAR PACIENTES DO MÉDICO
        Long totalPacientes = pacienteRepository.countByMedicoId(usuarioId);
        model.addAttribute("totalPacientes", totalPacientes);
        
        return "home"; 
    }

    @PostMapping("/registro")
    public String registrarUsuario(Registro usuario,
                                 @RequestParam(name = "especialidadesIds", required = false) List<Long> especialidadesIds, 
                                 Model model) {

        if (registroRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("erro", "Este e-mail já está cadastrado.");
            model.addAttribute("especialidades", especialidadeRepository.findAll()); 
            model.addAttribute("usuario", usuario); 
            return "registro";
        }

        String codigo;
        do {
            codigo = gerarCodigoDe8Digitos();
        } while (registroRepository.existsByCodigoLogin(codigo));

        usuario.setCodigoLogin(codigo);

        if (especialidadesIds == null || especialidadesIds.isEmpty()) {
            model.addAttribute("erro", "Você deve selecionar pelo menos uma especialidade.");
            model.addAttribute("especialidades", especialidadeRepository.findAll()); 
            model.addAttribute("usuario", usuario); 
            return "registro"; 
        }

        if (especialidadesIds != null && especialidadesIds.size() > 2) {
            model.addAttribute("erro", "Você só pode selecionar no máximo 2 especialidades.");
            model.addAttribute("especialidades", especialidadeRepository.findAll()); 
            return "registro"; 
        }

        if (especialidadesIds != null && !especialidadesIds.isEmpty()) {
            List<Especialidade> especialidadesSelecionadas = especialidadeRepository.findAllById(especialidadesIds);
            
            usuario.setEspecialidades(new HashSet<>(especialidadesSelecionadas));
        }

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
                                 @RequestParam("senha") String senha,
                                 HttpSession session) { 
        Optional<Registro> usuarioOptional = registroRepository.findByCodigoLogin(codigoLogin);

        if (usuarioOptional.isPresent()) {
            Registro usuario = usuarioOptional.get();
            if (usuario.getSenha() != null && usuario.getSenha().equals(senha)) {
                System.out.println("Login bem-sucedido para o usuário: " + usuario.getNome());
                
                session.setAttribute("usuarioLogadoId", usuario.getId()); 
                
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

    @GetMapping("/api/especialidades")
    @ResponseBody 
    public List<Especialidade> getEspecialidades() {
        return especialidadeRepository.findAll(); 
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

    @GetMapping("/pacientes")
    public String paginaPacientes(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Registro> usuarioOptional = registroRepository.findById(usuarioId);
        if (!usuarioOptional.isPresent()) {
            session.invalidate();
            return "redirect:/login";
        }

        Registro medicoLogado = usuarioOptional.get();
        model.addAttribute("medico", medicoLogado);

        // Carregar pacientes do médico
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(usuarioId);
        model.addAttribute("pacientes", pacientes);

        return "pacientes";
    }

    @PostMapping("/pacientes")
    public String salvarPaciente(
        @RequestParam String nome,
        @RequestParam(required = false) Integer idade,
        @RequestParam(required = false) String cpf,
        @RequestParam(required = false) String alergias,
        @RequestParam(required = false) String historicoCirurgias,
        @RequestParam(required = false) String observacoes,
        HttpSession session) {
            
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Registro> usuarioOptional = registroRepository.findById(usuarioId);
        if (!usuarioOptional.isPresent()) {
            session.invalidate();
            return "redirect:/login";
        }

        Registro medico = usuarioOptional.get();

        Paciente paciente = new Paciente();
        paciente.setNome(nome);
        paciente.setIdade(idade);
        paciente.setCpf(cpf);
        paciente.setAlergias(alergias);
        paciente.setHistoricoCirurgias(historicoCirurgias);
        paciente.setObservacoes(observacoes);
        paciente.setMedico(medico);

        pacienteRepository.save(paciente);

        return "redirect:/pacientes?success=true";
    }

    @PostMapping("/pacientes/{id}/excluir")
    public String excluirPaciente(@PathVariable Long id, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Paciente> pacienteOptional = pacienteRepository.findById(id);
        if (pacienteOptional.isPresent()) {
            Paciente paciente = pacienteOptional.get();
            // Verificar se o paciente pertence ao médico logado
            if (paciente.getMedico().getId().equals(usuarioId)) {
                pacienteRepository.delete(paciente);
            }
        }

        return "redirect:/pacientes?delete=true";
    }

    @GetMapping("/configuracoes")
    public String paginaConfiguracoes(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Optional<Registro> usuarioOptional = registroRepository.findById(usuarioId);
        if (!usuarioOptional.isPresent()) {
            session.invalidate();
            return "redirect:/login";
        }

        Registro medicoLogado = usuarioOptional.get();
        model.addAttribute("medico", medicoLogado);

        return "configuracoes";
    }

    @PostMapping("/perfil/foto")
    public String salvarFotoPerfil(@RequestParam("foto") MultipartFile foto, 
                                   HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioLogadoId");
        if (usuarioId == null) {
            return "redirect:/login"; 
        }
        
        Optional<Registro> usuarioOptional = registroRepository.findById(usuarioId);
        if (!usuarioOptional.isPresent()) {
            return "redirect:/login";
        }
        Registro medico = usuarioOptional.get();

        Path pastaUpload = Paths.get("./uploads");

        if (!Files.exists(pastaUpload)) {
            try {
                Files.createDirectories(pastaUpload);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("erroFoto", "Erro ao criar pasta de upload.");
                model.addAttribute("medico", medico);
                return "home"; 
            }
        }

        String nomeArquivoOriginal = foto.getOriginalFilename();

        if (nomeArquivoOriginal == null || nomeArquivoOriginal.isBlank() || !nomeArquivoOriginal.contains(".")) {
            model.addAttribute("erroFoto", "Erro: O arquivo enviado é inválido ou não tem nome/extensão.");
            model.addAttribute("medico", medico);
            return "home";
        }

        String extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf("."));
        String nomeArquivoNovo = "medico-" + medico.getId() + extensao;

        try (InputStream inputStream = foto.getInputStream()) {
            Path caminhoArquivo = pastaUpload.resolve(nomeArquivoNovo);
            Files.copy(inputStream, caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);
            
            medico.setFotoPerfil(nomeArquivoNovo);
            registroRepository.save(medico);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("erroFoto", "Erro ao salvar a foto.");
            model.addAttribute("medico", medico);
            return "home";
        }

        return "redirect:/home";
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