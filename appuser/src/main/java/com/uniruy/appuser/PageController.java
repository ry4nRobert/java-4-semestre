package com.uniruy.appuser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/login")
    public String paginaLogin(Model model) {
        return "login"; // vai procurar templates/index.html
    }
  
  @GetMapping("/registro")
    public String paginaRegistro(Model model) {
        return "registro"; // vai procurar templates/registro.html
    }

  @GetMapping("/esqueceu-senha")
    public String paginaEsqueceuSenha(Model model) {
        return "esqueceu-senha"; // vai procurar templates/index.html
    }
}
