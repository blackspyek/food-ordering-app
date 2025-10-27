import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {MenuStateService} from '../services/menu-state-service';

export const deliveryMethodGuard: CanActivateFn = (route, state) => {
  const menuStateService = inject(MenuStateService);
  const router = inject(Router);
  const deliveryMethod = menuStateService.deliveryMethod();
  if (deliveryMethod === '') {
    return router.parseUrl('/');
  }
  return true;
};
