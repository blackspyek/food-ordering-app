import { Component, input, InputSignal } from "@angular/core";
import { OrderedItemRecord } from "../../global.types";
import { CartService } from "../../services/cart-service";
import { Router } from "@angular/router";

@Component({
	selector: "li[app-cart-item]",
	standalone: true,
	imports: [],
	templateUrl: "./cart-item.html",
	styleUrl: "./cart-item.scss",
	host: {
		class: "w-full",
	},
})
export class CartItem {
	cartItem: InputSignal<OrderedItemRecord> =
		input.required<OrderedItemRecord>();

	constructor(
		public cartService: CartService,
		public router: Router,
	) {}

	deleteFromCart() {
		this.cartService.deleteFromCart(this.cartItem().item.id);
		if (this.cartService.cart().length === 0) {
			this.router.navigate(["/menu"]);
		}
	}
	updateQuantity(newQuantity: string | undefined) {
		const parsedQuantity = Number(newQuantity);
		if (!Number.isNaN(parsedQuantity) && parsedQuantity >= 0) {
			this.cartService.updateItemQuantity(
				this.cartItem().item.id,
				parsedQuantity,
			);
			if (this.cartService.cart().length === 0) {
				this.router.navigate(["/menu"]);
			}
		}
	}
}
