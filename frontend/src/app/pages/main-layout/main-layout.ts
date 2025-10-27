import { Component, WritableSignal, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { UpperCasePipe } from '@angular/common';
import { ActivatedRoute, NavigationEnd, Router, RouterLink, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { MenuStateService } from '../../services/menu-state-service';
import { CategoryNavbar } from '../../components/category-navbar/category-navbar';
import { OrderNavbar } from '../../components/order-navbar/order-navbar';
import { CartService } from '../../services/cart-service';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, MatIcon, CategoryNavbar, RouterLink, OrderNavbar],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss',
  host: {
    class: 'block h-full',
  },
})
export class MainLayout {
  viewState: WritableSignal<string> = signal('selecting');
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    public menuStateService: MenuStateService,
    public cartService: CartService,
    public authService: AuthService,
  ) {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      let route = this.activatedRoute.firstChild;
      while (route?.firstChild) {
        route = route.firstChild;
      }

      const view = route?.snapshot.data['view'] || 'selecting';
      this.viewState.set(view);
    });
  }

  resetSelection() {
    return this.router.navigate(['/']);
  }
  isLoggedIn() {
    return this.authService.isLoggedIn();
  }
  logout() {
    return this.authService.logout();
  }
}
