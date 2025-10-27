import { Component, computed, inject } from "@angular/core";
import { MenuList } from "../../components/menu-list/menu-list";
import { Category } from "../../global.types";
import { MenuStateService } from "../../services/menu-state-service";

@Component({
	selector: "app-menu-listing",
	standalone: true,
	imports: [MenuList],
	templateUrl: "./menu-listing.html",
	styleUrl: "./menu-listing.scss",
	host: {
		class: "flex flex-col h-full",
	},
})
export class MenuListing {
	private menuStateService = inject(MenuStateService);

	chosenCategory = computed(() => {
		console.log("MenuListing: chosenCategory computed called");
		console.log(this.menuStateService.chosenNav());
		const category = this.menuStateService.chosenNav();
		return category ? category : Category.RAMEN;
	});
}
