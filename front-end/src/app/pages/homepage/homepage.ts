import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Header } from "../../components/header/header";

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [CommonModule, Header],
  templateUrl: './homepage.html',
  styleUrl: './homepage.css',
})
export class Homepage {}
