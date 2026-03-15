import { NgClass } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-inicial-administrador',
  imports: [NgClass],
  templateUrl: './inicial-administrador.html'
})
export class InicialAdministrador {

  selectedTab = 1;

}