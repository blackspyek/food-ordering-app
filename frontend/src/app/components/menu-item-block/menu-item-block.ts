import { Component, input, InputSignal } from "@angular/core";
import { CurrencyPipe } from "@angular/common";
import { MatDialog } from "@angular/material/dialog";
import { ItemDetailsDialog } from "../item-details-dialog/item-details-dialog";
import { MenuItem } from "../../global.types";
import { MatIcon } from "@angular/material/icon";

@Component({
	selector: "li[app-menu-item-block]",
	standalone: true,
	imports: [CurrencyPipe, MatIcon],
	templateUrl: "./menu-item-block.html",
	styleUrl: "./menu-item-block.scss",
	host: {
		class:
			"threeColumns cursor-pointer hover:bg-gray-50 shadow-lg rounded-lg relative pt-6 pb-4 pr-4 pl-4",
		"(click)": "openItemDetails()",
	},
})
export class MenuItemBlock {
	id: InputSignal<number> = input.required<number>();
	photoUrl = input<string | undefined>();
	name: InputSignal<string> = input.required<string>();
	price: InputSignal<number> = input.required<number>();
	description: InputSignal<string | undefined> = input<string>();
	bestSeller: InputSignal<boolean | undefined> = input<boolean>();
	constructor(private dialog: MatDialog) {}

	openItemDetails() {
		const dialogRef = this.dialog.open(ItemDetailsDialog, {
			width: "400px",
			data: {
				id: this.id(),
				photoUrl: this.photoUrl(),
				name: this.name(),
				price: this.price(),
				description: this.description(),
			} as MenuItem,
		});

		dialogRef
			.afterClosed()
			.subscribe((result: { addedToBasket: any; item: any }) => {
				if (result?.addedToBasket) {
					console.log("Dialog zamknięty, dodano do koszyka:", result.item);
				}
			});
	}
}
