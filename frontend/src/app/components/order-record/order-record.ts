import { Component, input, InputSignal } from "@angular/core";
import { CurrencyPipe } from "@angular/common";
import { MatDialog } from "@angular/material/dialog";
import { Order } from "../../global.types";
import { OrderDetailsDialog } from "../order-details-dialog/order-details-dialog";
@Component({
	selector: "li[app-menu-order-record]",
	standalone: true,
	imports: [CurrencyPipe],
	templateUrl: "./order-record.html",
	styleUrl: "./order-record.scss",
	host: {
		"(click)": "openOrderDetails()",
	},
})
export class OrderRecord {
	order: InputSignal<Order> = input.required<Order>();

	constructor(private dialog: MatDialog) {}

	openOrderDetails() {
		const orderData = this.order();

		const dialogRef = this.dialog.open(OrderDetailsDialog, {
			width: "400px",
			data: orderData,
		});

		dialogRef.afterClosed().subscribe((result) => {
			if (result === "confirmed") {
				console.log(`Order ${orderData.orderId} confirmed.`);
			}
		});
	}
}
