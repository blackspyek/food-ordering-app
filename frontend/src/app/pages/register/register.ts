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
	selector: "app-register",
	imports: [ReactiveFormsModule, RouterLink],
	templateUrl: "./register.html",
	styleUrl: "./register.scss",
})
export class Register {
	fb = new FormBuilder();
	registerForm: FormGroup;

	showPassword = signal(false);
	isLoading = signal(false);
	errorMessage = signal<string | null>(null);

	constructor(
		private authService: AuthService,
		private router: Router,
	) {
		this.registerForm = this.fb.group({
			name: ["", Validators.required],
			email: ["", [Validators.required, Validators.email]],
			phone: [
				"",
				[Validators.required, Validators.pattern("^\\+?[0-9]{7,15}$")],
			],
			password: [
				"",
				[
					Validators.required,
					Validators.minLength(6),
					Validators.maxLength(60),
				],
			],
		});
	}
	isFieldInvalid(field: string): boolean {
		const control = this.registerForm.get(field);
		return !!(control?.invalid && (control.dirty || control.touched));
	}

	togglePassword() {
		this.showPassword.update((value) => !value);
	}

	onSubmit() {
		if (this.registerForm.valid) {
			this.isLoading.set(true);
			this.errorMessage.set(null);
			this.authService.signUp(this.registerForm.value).subscribe({
				next: () => {
					this.isLoading.set(false);
					void this.router.navigate(["/login"]);
				},
				error: (error) => {
					this.isLoading.set(false);
					const message = error?.error?.message || "Registration failed. Please try again.";
					this.errorMessage.set(message);
				},
			});
		} else {
			this.registerForm.markAllAsTouched();
		}
	}
}
