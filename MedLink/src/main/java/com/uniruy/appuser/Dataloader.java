package com.uniruy.appuser;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class Dataloader {

    @Bean
    CommandLineRunner initDatabase(EspecialidadeRepository repository) {
        return args -> {
            // Esta linha verifica se a tabela já tem dados para não duplicar
            if (repository.count() == 0) {
                System.out.println("Populando banco de dados com especialidades...");
                
                List<String> especialidadesNomes = Arrays.asList(
                    "Acupuntura", "Alergia e imunologia", "Anestesiologia", "Angiologia",
                    "Cardiologia", "Cirurgia cardiovascular", "Cirurgia da mão", "Cirurgia de cabeça e pescoço",
                    "Cirurgia do aparelho digestivo", "Cirurgia geral", "Cirurgia oncológica", "Cirurgia pediátrica",
                    "Cirurgia plástica", "Cirurgia torácica", "Cirurgia vascular", "Clínica médica",
                    "Coloproctologia", "Dermatologia", "Endocrinologia e metabologia", "Endoscopia",
                    "Gastroenterologia", "Genética médica", "Geriatria", "Ginecologia e obstetrícia",
                    "Hematologia e hemoterapia", "Homeopatia", "Infectologia", "Mastologia",
                    "Medicina de emergência", "Medicina de família e comunidade", "Medicina do trabalho",
                    "Medicina do tráfego", "Medicina esportiva", "Medicina física e reabilitação",
                    "Medicina intensiva", "Medicina legal e perícia médica", "Medicina nuclear",
                    "Medicina preventiva e social", "Nefrologia", "Neurocirurgia", "Neurologia",
                    "Nutrologia", "Oftalmologia", "Oncologia clínica", "Ortopedia e traumatologia",
                    "Otorrinolaringologia", "Patologia", "Patologia clínica/medicina laboratorial",
                    "Pediatria", "Pneumologia", "Psiquiatria", "Radiologia e diagnóstico por imagem",
                    "Radioterapia", "Reumatologia", "Urologia"
                );

                // Para cada nome na lista, cria um novo objeto Especialidade e salva
                especialidadesNomes.forEach(nome -> repository.save(new Especialidade(nome)));
                
                System.out.println("Especialidades populadas com sucesso!");
            } else {
                System.out.println("Banco de dados de especialidades já populado.");
            }
        };
    }
}