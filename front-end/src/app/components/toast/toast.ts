import { Component, inject } from '@angular/core';
import { ToastService } from '../../shared/service/toast/toast';

@Component({
  selector: 'toast-container',
  standalone: true,
   templateUrl: './toast.html'
})
export class ToastContainerComponent {
  toastService = inject(ToastService);
}