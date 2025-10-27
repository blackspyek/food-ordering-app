import { Injectable, signal, WritableSignal } from "@angular/core";
import { Category, categoryNames, type DeliveryMethod } from "../global.types";

@Injectable({
	providedIn: "root",
})
export class MenuStateService {
	public chosenNav: WritableSignal<string> = signal(Category.RAMEN);
	public deliveryMethod: WritableSignal<DeliveryMethod | ""> = signal("");
	protected readonly categoryNames = categoryNames;

	constructor() {}

	changeSelectedNav(newNav: string) {
		this.chosenNav.set(newNav);
	}
	getCategories() {
		return this.categoryNames;
	}
}
