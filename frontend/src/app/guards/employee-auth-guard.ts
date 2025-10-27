import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

export const employeeAuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const roles = authService.getRoles();

  if (!roles) {
    return router.parseUrl('/login');
  }

  const hasEmployeeAccess = roles.includes('ROLE_EMPLOYEE') || roles.includes('ROLE_MANAGER');

  if (!hasEmployeeAccess) {
    return router.parseUrl('/');
  }

  return true;
};
