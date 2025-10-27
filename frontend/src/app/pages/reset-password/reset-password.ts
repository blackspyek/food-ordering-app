import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, type FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-reset-password',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
  host: {
    class: 'flex h-full w-full items-center justify-center',
  },
})
export class ResetPassword implements OnInit {
  fb = new FormBuilder();
  resetForm: FormGroup;
  showPassword = signal(false);
  showConfirmPassword = signal(false);
  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');
  token = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
  ) {
    this.resetForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.errorMessage.set('Invalid or missing reset token.');
      }
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.resetForm.get(field);
    return !!(control?.invalid && (control.dirty || control.touched));
  }

  passwordsMatch(): boolean {
    return this.resetForm.value.newPassword === this.resetForm.value.confirmPassword;
  }

  togglePassword() {
    this.showPassword.update((value) => !value);
  }

  toggleConfirmPassword() {
    this.showConfirmPassword.update((value) => !value);
  }

  onSubmit() {
    if (!this.passwordsMatch()) {
      this.errorMessage.set('Passwords do not match.');
      return;
    }

    if (this.resetForm.valid && this.token) {
      this.isLoading.set(true);
      this.errorMessage.set('');
      this.successMessage.set('');

      this.authService.resetPassword(this.token, this.resetForm.value.newPassword).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.successMessage.set('Password has been reset successfully! Redirecting to login...');
          setTimeout(() => {
            void this.router.navigate(['/login']);
          }, 2000);
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set(error.error?.message || 'Invalid or expired reset token.');
        },
      });
    }
  }
}
