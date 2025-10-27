import { Component, signal } from "@angular/core";
import {
	FormBuilder,
	FormGroup,
	ReactiveFormsModule,
	Validators,
} from "@angular/forms";
import { CartService } from "../../services/cart-service";
import { OrderService } from "../../services/order-service";
import { Router } from "@angular/router";
import { CreateOrderDto } from "../../global.types";
import { MenuStateService } from "../../services/menu-state-service";
import { finalize } from "rxjs";
import { AuthService } from "../../services/auth-service";

@Component({
	selector: "app-order-finalization",
	standalone: true,
	imports: [ReactiveFormsModule],
	templateUrl: "./order-finalization.html",
	styleUrl: "./order-finalization.scss",
	host: {
		class: "w-full h-full",
	},
})
export class OrderFinalization {
	contactForm: FormGroup;

	isLoading = signal(false);

	constructor(
		private formBuilder: FormBuilder,
		public cartService: CartService,
		private menuStateService: MenuStateService,
		private orderService: OrderService,
		private router: Router,
		private authService: AuthService,
	) {
		this.contactForm = this.formBuilder.group({
			name: [
				this.getLoggedUserName(),
				[Validators.required, Validators.minLength(2)],
			],
			email: [
				this.getLoggedUserEmail(),
				[Validators.required, Validators.email],
			],
		});
	}

	get f() {
		return this.contactForm.controls;
	}
	getItems() {
		return this.cartService.cart();
	}
	getLoggedUserName() {
		return this.authService.getUserName();
	}
	getLoggedUserEmail() {
		return this.authService.getUserEmail();
	}
	onSubmit() {
		if (this.contactForm.invalid) {
			this.contactForm.markAllAsTouched();
			return;
		}

		this.isLoading.set(true);

		const newOrder: CreateOrderDto = {
			email: this.contactForm.value.email,
			name: this.contactForm.value.name,
			orderType: this.menuStateService.deliveryMethod(),
			userId: this.authService.getUserId(),
			orderItems: this.cartService.cart().map((item) => ({
				menuItemId: item.item.id,
				quantity: item.quantity,
			})),
		};

		this.orderService
			.createOrder(newOrder)
			.pipe(finalize(() => this.isLoading.set(false)))
			.subscribe({
				next: (response) => {
					console.log("Order created successfully:", response.data.orderId);
					this.cartService.clearCart();
					this.router.navigate(["order-details", response.data.orderId]);
				},
				error: (error) => {
					console.error("Error creating order:", error);
				},
			});
	}
}
