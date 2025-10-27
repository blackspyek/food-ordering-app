import { Component, inject, OnInit } from "@angular/core";
import { UpperCasePipe } from "@angular/common";
import { MenuStateService } from "../../services/menu-state-service";
import { CategoryNamePipe } from "../../pipes/category-name-pipe";

@Component({
	selector: "app-category-navbar",
	imports: [UpperCasePipe, CategoryNamePipe],
	templateUrl: "./category-navbar.html",
	styleUrl: "./category-navbar.scss",
	host: {
		class: "flex flex-row gap-2 items-center p-2",
	},
})
export class CategoryNavbar implements OnInit {
	protected menuStateService = inject(MenuStateService);

	ngOnInit() {
		this.menuStateService.changeSelectedNav("RAMEN");
	}
}
