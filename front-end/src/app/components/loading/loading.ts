import { Component, inject } from '@angular/core';
import { LoadingService } from '../../shared/service/loading.service';

@Component({
  selector: 'app-spinner',
  standalone: true,
  template: `
    @if (loadingService.isLoading()) {
      <div class="fixed inset-0 bg-black/20 backdrop-blur-sm z-[9999] flex items-center justify-center">
        <div class="spinner"></div>
      </div>
    }
  `,
  styles: [`
    .spinner {
      width: 50px; height: 50px; border: 5px solid #f3f3f3;
      border-top: 5px solid #3498db; border-radius: 50%;
      animation: spin 1s linear infinite;
    }
    @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
  `]
})
export class SpinnerComponent {
  protected loadingService = inject(LoadingService);
}