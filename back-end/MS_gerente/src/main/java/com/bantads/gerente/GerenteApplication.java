package com.bantads.gerente;

import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.enums.GerenteTipo;
import com.bantads.gerente.service.GerenteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GerenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenteApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(GerenteService gerenteService) {
		return args -> {
			gerenteService.novoGerente(new CriaGerenteDTO(
					"Victor",
					"fsdfsdf@gmail.com",
					"29004446087",
					"1242",
					GerenteTipo.GERENTE
			));
		};
	}

}
