import { Component, inject, OnInit } from "@angular/core";
import { UpperCasePipe } from "@angular/common";
import { MenuStateService } from "../../services/menu-state-service";

@Component({
	selector: "app-order-navbar",
	imports: [UpperCasePipe],
	templateUrl: "./order-navbar.html",
	styleUrl: "./order-navbar.scss",
	host: {
		class: "flex flex-row gap-2 items-center p-2",
	},
})
export class OrderNavbar implements OnInit {
	protected menuStateService = inject(MenuStateService);
	constructor() {}

	ngOnInit(): void {
		this.menuStateService.changeSelectedNav("BOARD");
	}
}
