import { Component } from "@angular/core";
import { CartService } from "../../services/cart-service";
import { CartItem } from "../../components/cart-item/cart-item";
import { CurrencyPipe } from "@angular/common";
import { RouterLink } from "@angular/router";

@Component({
	selector: "app-cart",
	imports: [CartItem, CurrencyPipe, RouterLink],
	templateUrl: "./cart.html",
	styleUrl: "./cart.scss",
	host: {
		class: "h-full flex flex-col justify-between align-center",
	},
})
export class Cart {
	constructor(public cartService: CartService) {}
}
