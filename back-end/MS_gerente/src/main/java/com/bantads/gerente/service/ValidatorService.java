package com.bantads.gerente.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidatorService {
    public Optional<String> cpfValidator(String _cpf){
        var cpf = _cpf.replace(".", "")
                .replace("-", "")
                .trim();

        if (!cpf.matches("[0-9]+")) return Optional.empty();
        if (cpf.length() != 11) return Optional.empty();


        try {
            char dig10, dig11;
            int soma, i, r, num, peso;

            soma = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (cpf.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (soma % 11);
            if ((r == 10) || (r == 11)) dig10 = '0';
            else dig10 = (char) (r + 48);

            soma = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (cpf.charAt(i) - 48);
                soma = soma + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (soma % 11);
            if ((r == 10) || (r == 11)) dig11 = '0';
            else dig11 = (char) (r + 48);

            var digitosCalculadosConferem = (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

            if (digitosCalculadosConferem) return Optional.of(cpf);

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> emailValidator(String _email){
        var email = _email.trim();

        if (email.matches("^.+@.+\\..+$")) return Optional.of(email);
        return Optional.empty();
    }

}
