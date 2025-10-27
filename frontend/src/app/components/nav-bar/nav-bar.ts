import { Component, inject, OnInit } from "@angular/core";
import { MenuBtn } from "../menu-btn/menu-btn";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth-service";

@Component({
	selector: "app-nav-bar",
	imports: [MenuBtn, RouterLink],
	templateUrl: "./nav-bar.html",
	styleUrl: "./nav-bar.scss",
})
export class NavBar implements OnInit {
	authService = inject(AuthService);

	ngOnInit(): void {
		console.log("test");
		console.log("Rola użytkownika w NavBar:", this.authService.getRoles());
	}
}
