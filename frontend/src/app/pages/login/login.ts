import { Component, signal } from "@angular/core";
import {
	FormBuilder,
	type FormGroup,
	ReactiveFormsModule,
	Validators,
} from "@angular/forms";
import { Router, RouterLink } from "@angular/router";
import { AuthService } from "../../services/auth-service";

@Component({
	selector: "app-login",
	imports: [ReactiveFormsModule, RouterLink],
	templateUrl: "./login.html",
	styleUrl: "./login.scss",
	host: {
		class: "flex h-full w-full items-center justify-center",
	},
})
export class Login {
	fb = new FormBuilder();
	loginForm: FormGroup;
	showPassword = signal(false);
	isLoading = signal(false);
	errorMessage = signal("");

	constructor(
		private authService: AuthService,
		private router: Router,
	) {
		this.loginForm = this.fb.group({
			email: ["", [Validators.required, Validators.email]],
			password: ["", [Validators.required, Validators.minLength(6)]],
		});
	}
	isFieldInvalid(field: string): boolean {
		const control = this.loginForm.get(field);
		return !!(control?.invalid && (control.dirty || control.touched));
	}

	togglePassword() {
		this.showPassword.update((value) => !value);
	}

	onSubmit() {
		if (this.loginForm.valid) {
			this.isLoading.set(true);

			this.authService.login(this.loginForm.value).subscribe({
				next: () => {
					this.isLoading.set(false);
					void this.router.navigate(["/orders"]);
				},
				error: (error) => {
					this.isLoading.set(false);
					this.errorMessage.set(
						error.error.message || "Błąd podczas logowania.",
					);
				},
			});
		} else {
			this.loginForm.markAllAsTouched();
		}
	}
}
