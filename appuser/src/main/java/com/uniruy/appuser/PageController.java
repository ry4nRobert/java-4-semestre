package com.uniruy.appuser;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/login")
    public String paginaTeste(Model model) {
        model.addAttribute("mensagem", "Olá, este texto veio do backend!");
        return "login"; // vai procurar templates/index.html
    }
}
