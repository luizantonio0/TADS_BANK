import { Component, inject } from '@angular/core';
import { LoadingService } from '../../shared/service/loading.service';

@Component({
  selector: 'app-spinner',
  standalone: true,
  template: `
    @if (loadingService.isLoading()) {
      <div class="fixed inset-0 bg-black/20 backdrop-blur-sm z-9999 flex items-center justify-center">
        <div class="w-12.5 h-12.5 border-[5px] border-solid border-t-indigo border-gray-200 rounded-[50%] animate-spin"></div>
      </div>
    }
  `
})
export class SpinnerComponent {
  protected loadingService = inject(LoadingService);
}