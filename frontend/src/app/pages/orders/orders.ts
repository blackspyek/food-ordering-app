import { Component, inject } from "@angular/core";
import { NumberOrdersList } from "../../components/number-orders-list/number-orders-list";
import { MenuStateService } from "../../services/menu-state-service";
import { OrdersList } from "../../components/orders-list/orders-list";
import { AuthService } from "../../services/auth-service";

@Component({
	selector: "app-orders",
	imports: [NumberOrdersList, OrdersList],
	templateUrl: "./orders.html",
	styleUrl: "./orders.scss",
	host: {
		class: "flex flex-row gap-4 p-4 h-full",
	},
})
export class Orders {
	protected authService = inject(AuthService);
	protected menuStateService = inject(MenuStateService);
}
