import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error';
  isLeaving?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);

  remove(id: number) {
    this.toasts.update(t => t.filter(toast => toast.id !== id));
  }

  show(message: string, type: Toast['type'] = 'success') {
    const id = Date.now();
    this.toasts.update(t => [...t, { id, message, type }]);

    setTimeout(() => this.markForRemoval(id), 4000);
  }

  success(message: string) {
    this.show(message, 'success');
  }

  error(message: string) {
    this.show(message, 'error');
  }

  markForRemoval(id: number) {
    this.toasts.update(t => t.map(toast =>
      toast.id === id ? { ...toast, isLeaving: true } : toast
    ));

    setTimeout(() => this.remove(id), 300);
  }
}