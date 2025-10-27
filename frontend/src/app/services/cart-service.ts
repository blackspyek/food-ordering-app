import { computed, Injectable, signal } from "@angular/core";
import { MenuItem, OrderedItemRecord } from "../global.types";

@Injectable({
	providedIn: "root",
})
export class CartService {
	cart = signal<OrderedItemRecord[]>([]);
	// computed signal to get total price of items in cart
	totalPrice = computed(() => {
		return this.cart().reduce((total, item) => total + item.totalPrice, 0);
	});
	totalNumberOfItems = computed(() => {
		return this.cart().reduce((total, item) => total + item.quantity, 0);
	});

	addToCart(menuItem: MenuItem) {
		const newItem = {
			item: menuItem,
			quantity: 1,
			totalPrice: menuItem.price,
		};
		const existingItemIndex = this.cart().findIndex(
			(item) => item.item.id === menuItem.id,
		);
		if (existingItemIndex !== -1) {
			const updatedCart = [...this.cart()];
			const existingItem = updatedCart[existingItemIndex];
			existingItem.quantity += 1;
			existingItem.totalPrice = existingItem.quantity * existingItem.item.price;
			this.cart.set(updatedCart);
		} else {
			this.cart.set([...this.cart(), newItem]);
		}
	}
	clearCart() {
		this.cart.set([]);
	}

	deleteFromCart(menuItemId: number) {
		const updatedCart = this.cart().filter(
			(item) => item.item.id !== menuItemId,
		);
		this.cart.set(updatedCart);
	}

	updateItemQuantity(menuItemId: number, newQuantity: number) {
		const updatedCart = this.cart()
			.map((item) => {
				if (item.item.id === menuItemId) {
					const updatedItem = { ...item };
					updatedItem.quantity = newQuantity;
					updatedItem.totalPrice = newQuantity * updatedItem.item.price;
					return updatedItem;
				}
				return item;
			})
			.filter((item) => item.quantity > 0);
		this.cart.set(updatedCart);
	}
	constructor() {}
}
