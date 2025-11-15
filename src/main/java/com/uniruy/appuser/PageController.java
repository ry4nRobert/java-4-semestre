package com.uniruy.appuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
//import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
//import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



@Controller
public class PageController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    //@Autowired
    //private EmailService emailService;
    
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

        
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

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

        return "redirect:/login";
    }

    
@PostMapping("/login")
public String processarLogin(@RequestParam("email") String email,
                             @RequestParam("senha") String senha,
                             HttpSession session) { 
   
    Optional<Registro> usuarioOptional = registroRepository.findByEmail(email);

    if (usuarioOptional.isPresent()) {
        Registro usuario = usuarioOptional.get();
        
        if (passwordEncoder.matches(senha, usuario.getSenha())) {

            session.setAttribute("usuarioLogadoId", usuario.getId()); 
            return "redirect:/home";

        } else {
            return "redirect:/login?error=senha";
        }

    } else {
        return "redirect:/login?error=email";
    }
}


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
    // Invalida a sessão atual
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }

    // Limpa qualquer cookie de sessão se necessário
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID")) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    return "redirect:/login?logout";
}

@PostMapping("/logout")
public String logoutPost(HttpServletRequest request) {
    request.getSession().invalidate();
    return "redirect:/login?logout";
}

    @GetMapping("/api/especialidades")
    @ResponseBody 
    public List<Especialidade> getEspecialidades() {
        return especialidadeRepository.findAll(); 
    }

    public String mostrarPaginaAjuda() {

        return "ajuda-login";
    }

    @GetMapping("/ajuda-config")
    public String mostrarPaginaAjudaConfig() {

        return "ajuda-config";
    }

    @GetMapping("/pacientes")
public String paginaPacientes(

        @RequestParam(value = "q", required = false) String query, 
        Model model, 
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
    model.addAttribute("medico", usuarioOptional.get());

    
    
    List<Paciente> pacientes;
    
    
    if (query != null && !query.isBlank()) {
        
       
        pacientes = pacienteRepository.findByMedicoIdAndNomeContainingIgnoreCaseOrMedicoIdAndCpf(
            usuarioId, query, 
            usuarioId, query  
        );
        
    } else {
        
        pacientes = pacienteRepository.findByMedicoId(usuarioId);
    }
    
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

    @Transactional
    @PostMapping("/pacientes/{id}/excluir")
    public String excluirPaciente(@PathVariable("id") Long id, HttpSession session) {
        
        Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
        if (medicoId == null) {
            return "redirect:/login";
        }
        
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (!pacienteOpt.isPresent()) {
            return "redirect:/pacientes?error=naoencontrado";
        }
        
        Paciente paciente = pacienteOpt.get();
        
        if (!paciente.getMedico().getId().equals(medicoId)) {
        
            return "redirect:/pacientes?error=permissao";
        }

        try {
            
            consultaRepository.deleteByPacienteId(id);
        
            pacienteRepository.delete(paciente);
            return "redirect:/pacientes?success=excluido";

        } catch (Exception e) {
            e.printStackTrace();
            
            return "redirect:/pacientes?error=falhaexcluir";
        }
    }

    @PostMapping("/pacientes/editar")
    public String editarPaciente(
    @RequestParam Long id,
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

    Optional<Paciente> pacienteOptional = pacienteRepository.findById(id);
    if (pacienteOptional.isPresent()) {
        Paciente paciente = pacienteOptional.get();

        // Verificar se o paciente pertence ao médico logado
        if (paciente.getMedico().getId().equals(usuarioId)) {
            paciente.setNome(nome);
            paciente.setIdade(idade);
            paciente.setCpf(cpf);
            paciente.setAlergias(alergias);
            paciente.setHistoricoCirurgias(historicoCirurgias);
            paciente.setObservacoes(observacoes);

            pacienteRepository.save(paciente);
        }
    }

    return "redirect:/pacientes?edit=true";
}


@GetMapping("/consultas")
    public String paginaConsultas(Model model, HttpSession session) {
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
        return "consultas";
    }

    @PostMapping("/api/consultas/nova")
    @ResponseBody 
    public ResponseEntity<?> salvarConsulta(@RequestBody Map<String, String> dados, HttpSession session) {
        try {
            Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
            if (medicoId == null) {
                return ResponseEntity.status(401).body("Você precisa estar logado.");
            }
            Registro medico = registroRepository.findById(medicoId).orElse(null);

            String pacienteIdStr = dados.get("pacienteId");
            String dataHoraStr = dados.get("dataHora"); 

            Long pacienteId = Long.parseLong(pacienteIdStr);
            Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
            if (!pacienteOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Paciente não encontrado com o ID: " + pacienteId);
            }

            Consulta novaConsulta = new Consulta();
            novaConsulta.setMedico(medico);
            novaConsulta.setPaciente(pacienteOpt.get());
            novaConsulta.setDataHora(LocalDateTime.parse(dataHoraStr));
            
            consultaRepository.save(novaConsulta);

            return ResponseEntity.ok("Consulta agendada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao salvar consulta: " + e.getMessage());
        }
    }

    @GetMapping("/api/consultas")
@ResponseBody 
public ResponseEntity<?> getConsultasDoMes(
                        @RequestParam("ano") int ano,
                        @RequestParam("mes") int mes, 
                        HttpSession session) {

    Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
    if (medicoId == null) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }
    Optional<Registro> medicoOpt = registroRepository.findById(medicoId);
    if (!medicoOpt.isPresent()) {
        return ResponseEntity.status(401).body("Médico não encontrado");
    }

    try {
        LocalDateTime inicioDoMes = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fimDoMes = inicioDoMes.plusMonths(1).minusNanos(1); 
        List<Consulta> consultas = consultaRepository.findByMedicoAndDataHoraBetween(
            medicoOpt.get(), inicioDoMes, fimDoMes
        );

        return ResponseEntity.ok(consultas);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao buscar consultas");
    }
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

      @GetMapping("/graficos")
    public String paginaGraficos(Model model, HttpSession session) {
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

        return "graficos";
    }

    @GetMapping("/api/graficos/pacientes-por-mes")
@ResponseBody
public ResponseEntity<?> getPacientesPorMes(HttpSession session) {
    Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
    if (medicoId == null) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }

    try {
        // Para simplificar, vamos pegar os últimos 6 meses
        LocalDateTime agora = LocalDateTime.now();
        Map<String, Long> dados = new LinkedHashMap<>();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime mes = agora.minusMonths(i);
            String mesAno = mes.getMonth().toString() + "/" + mes.getYear();
            
            // Contar pacientes criados naquele mês
            LocalDateTime inicioMes = LocalDateTime.of(mes.getYear(), mes.getMonth(), 1, 0, 0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);
            
            // Você precisará criar este método no repository
            Long count = pacienteRepository.countByMedicoIdAndDataCriacaoBetween(medicoId, inicioMes, fimMes);
            dados.put(mesAno, count);
        }
        
        return ResponseEntity.ok(dados);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao buscar dados dos pacientes");
    }
}

@GetMapping("/api/graficos/consultas-por-mes")
@ResponseBody
public ResponseEntity<?> getConsultasPorMes(HttpSession session) {
    Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
    if (medicoId == null) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }

    Optional<Registro> medicoOpt = registroRepository.findById(medicoId);
    if (!medicoOpt.isPresent()) {
        return ResponseEntity.status(401).body("Médico não encontrado");
    }

    try {
        LocalDateTime agora = LocalDateTime.now();
        Map<String, Long> dados = new LinkedHashMap<>();
        Registro medico = medicoOpt.get();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime mes = agora.minusMonths(i);
            String mesAno = mes.getMonth().toString() + "/" + mes.getYear();
            
            LocalDateTime inicioMes = LocalDateTime.of(mes.getYear(), mes.getMonth(), 1, 0, 0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);
            
            List<Consulta> consultas = consultaRepository.findByMedicoAndDataHoraBetween(medico, inicioMes, fimMes);
            dados.put(mesAno, (long) consultas.size());
        }
        
        return ResponseEntity.ok(dados);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao buscar dados das consultas");
    }
}

@GetMapping("/api/graficos/distribuicao-idade")
@ResponseBody
public ResponseEntity<?> getDistribuicaoIdade(HttpSession session) {
    Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
    if (medicoId == null) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }

    try {
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        
        Map<String, Long> distribuicao = new LinkedHashMap<>();
        distribuicao.put("0-18", pacientes.stream().filter(p -> p.getIdade() != null && p.getIdade() <= 18).count());
        distribuicao.put("19-35", pacientes.stream().filter(p -> p.getIdade() != null && p.getIdade() > 18 && p.getIdade() <= 35).count());
        distribuicao.put("36-50", pacientes.stream().filter(p -> p.getIdade() != null && p.getIdade() > 35 && p.getIdade() <= 50).count());
        distribuicao.put("51-65", pacientes.stream().filter(p -> p.getIdade() != null && p.getIdade() > 50 && p.getIdade() <= 65).count());
        distribuicao.put("65+", pacientes.stream().filter(p -> p.getIdade() != null && p.getIdade() > 65).count());
        
        return ResponseEntity.ok(distribuicao);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao buscar distribuição por idade");
    }
}

@GetMapping("/api/graficos/estatisticas-gerais")
@ResponseBody
public ResponseEntity<?> getEstatisticasGerais(HttpSession session) {
    Long medicoId = (Long) session.getAttribute("usuarioLogadoId");
    if (medicoId == null) {
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }

    try {
        Map<String, Object> estatisticas = new HashMap<>();
        
        // Total de pacientes
        Long totalPacientes = pacienteRepository.countByMedicoId(medicoId);
        estatisticas.put("totalPacientes", totalPacientes);
        
        // Consultas deste mês
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);
        Optional<Registro> medicoOpt = registroRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            List<Consulta> consultasMes = consultaRepository.findByMedicoAndDataHoraBetween(
                medicoOpt.get(), inicioMes, fimMes);
            estatisticas.put("consultasMes", consultasMes.size());
        } else {
            estatisticas.put("consultasMes", 0);
        }
        
        // Idade média
        List<Paciente> pacientes = pacienteRepository.findByMedicoId(medicoId);
        Double mediaIdade = pacientes.stream()
            .filter(p -> p.getIdade() != null)
            .mapToInt(Paciente::getIdade)
            .average()
            .orElse(0.0);
        estatisticas.put("mediaIdade", Math.round(mediaIdade));
        
        return ResponseEntity.ok(estatisticas);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Erro ao buscar estatísticas gerais");
    }
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

    if (foto.isEmpty()) {
        model.addAttribute("erroFoto", "Erro: O arquivo enviado está vazio.");
        model.addAttribute("medico", medico);
        return "home";
    }
  
    try {
        byte[] bytesDaFoto = foto.getBytes();
        
        medico.setFotoPerfil(bytesDaFoto); 
        
        registroRepository.save(medico);

    } catch (IOException e) {
        e.printStackTrace();
        model.addAttribute("erroFoto", "Erro ao processar a foto.");
        model.addAttribute("medico", medico);
        return "home";
    }
  
    return "redirect:/home";
}}