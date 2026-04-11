import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function cpfValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const cpf = String(control.value ?? '').replace(/\D/g, '');

    if (!cpf) {
      return null;
    }

    if (cpf.length !== 11) {
      return { cpfInvalido: true };
    }

    if (/^(\d)\1{10}$/.test(cpf)) {
      return { cpfInvalido: true };
    }

    const calcularDigito = (base: string, fatorInicial: number): number => {
      let soma = 0;
      for (let i = 0; i < base.length; i++) {
        soma += Number(base[i]) * (fatorInicial - i);
      }

      const resto = soma % 11;
      return resto < 2 ? 0 : 11 - resto;
    };

    const base = cpf.slice(0, 9);
    const digito1 = calcularDigito(base, 10);
    const digito2 = calcularDigito(base + digito1, 11);

    const cpfCalculado = base + digito1 + digito2;

    return cpf === cpfCalculado ? null : { cpfInvalido: true };
  };
}
