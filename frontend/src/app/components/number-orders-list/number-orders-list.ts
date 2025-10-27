import { Component, computed, inject, input, InputSignal } from "@angular/core";
import { BoxTitle } from "../box-title/box-title";
import { OrderSocketService } from "../../services/order-socket-service";

@Component({
	selector: "app-number-orders-list",
	imports: [BoxTitle],
	templateUrl: "./number-orders-list.html",
	styleUrl: "./number-orders-list.scss",
	host: {
		class: "h-full flex flex-col items-center gap-4 p-4",
	},
})
export class NumberOrdersList {
	private socketService = inject(OrderSocketService);

	title: InputSignal<string> = input.required<string>();
	state: InputSignal<"primary" | "secondary"> = input<"primary" | "secondary">(
		"secondary",
	);
	items = computed(() => {
		return this.state() === "primary"
			? this.socketService.boardState().liveOrderBoardReadyCodes
			: this.socketService.boardState().liveOrderBoardCodes;
	});
}
