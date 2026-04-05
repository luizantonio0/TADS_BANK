import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'telefone',
  standalone: true
})
export class TelefonePipe implements PipeTransform {

  transform(valor?: string | number): string {
    if (!valor) return '';

    const fone = valor.toString().replace(/\D/g, '');

    if (fone.length === 11) {
      return fone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
    } else if (fone.length === 10) {
      return fone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }

    return valor.toString();
  }
}