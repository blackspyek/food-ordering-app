import { Component, signal } from '@angular/core';
import { FormBuilder, type FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-forgot-password',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
  host: {
    class: 'flex h-full w-full items-center justify-center',
  },
})
export class ForgotPassword {
  fb = new FormBuilder();
  forgotForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  constructor(private authService: AuthService) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.forgotForm.get(field);
    return !!(control?.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.forgotForm.valid) {
      this.isLoading.set(true);
      this.errorMessage.set('');
      this.successMessage.set('');

      this.authService.forgotPassword(this.forgotForm.value.email).subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.successMessage.set(
            response.message ||
              'If your email exists in our system, you will receive a password reset link.',
          );
          this.forgotForm.reset();
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set(error.error?.message || 'Something went wrong. Please try again.');
        },
      });
    }
  }
}
