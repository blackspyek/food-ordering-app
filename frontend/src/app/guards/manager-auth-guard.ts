import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

export const managerAuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const roles = authService.getRoles();

  if (!roles) {
    return router.parseUrl('/login');
  }

  const hasManagerAccess = roles.includes('ROLE_MANAGER');

  if (!hasManagerAccess) {
    return router.parseUrl('/');
  }

  return true;
};
