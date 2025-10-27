import { Component } from "@angular/core";
import { DeliveryMethod } from "../../global.types";
import { Router } from "@angular/router";
import { MenuStateService } from "../../services/menu-state-service";

@Component({
	selector: "app-delivery-selection",
	imports: [],
	templateUrl: "./delivery-selection.html",
	styleUrl: "./delivery-selection.scss",
	host: {
		class: "h-full w-full flex flex-col items-center",
	},
})
export class DeliverySelection {
	constructor(
		private router: Router,
		private menuStateService: MenuStateService,
	) {}

	selectDeliveryMethod(method: DeliveryMethod) {
		this.menuStateService.deliveryMethod.set(method);
		return this.router.navigate(["/menu"]);
	}
}
