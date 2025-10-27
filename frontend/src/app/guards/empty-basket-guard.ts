import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";
import { CartService } from "../services/cart-service";

export const emptyBasketGuard: CanActivateFn = (route, state) => {
	const cartService = inject(CartService);
	const router = inject(Router);
	const cartLength = cartService.cart().length;
	if (cartLength < 1) {
		return router.parseUrl("/");
	}
	return true;
};
