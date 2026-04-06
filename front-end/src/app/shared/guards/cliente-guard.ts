import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const clienteGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = sessionStorage.getItem('access_token');

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
