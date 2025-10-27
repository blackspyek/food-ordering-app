import {
	Component,
	inject,
	type OnInit,
	signal,
	type WritableSignal,
} from "@angular/core";
import { BoxTitle } from "../box-title/box-title";
import { OrderRecord } from "../order-record/order-record";
import type { Order } from "../../global.types";
import { OrderService } from "../../services/order-service";

@Component({
	selector: "app-orders-list",
	imports: [BoxTitle, OrderRecord],
	templateUrl: "./orders-list.html",
	styleUrl: "./orders-list.scss",
})
export class OrdersList implements OnInit {
	private orderService = inject(OrderService);
	orders: WritableSignal<Order[]> = signal<Order[]>([]);
	isLoading = signal(true);
	ngOnInit() {
		this.fetchMyOrders();
	}
	private fetchMyOrders(): void {
		this.orderService.getMyOrders().subscribe({
			next: (data) => {
				this.orders.set(data);
				this.isLoading.set(false);
				console.log("Pobrane zamówienia:", data);
			},
			error: (err) => {
				this.isLoading.set(false);
				console.error("Błąd podczas pobierania zamówień:", err);
				this.orders.set([]);
			},
		});
	}
}
