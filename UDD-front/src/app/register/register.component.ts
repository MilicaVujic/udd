import { Component } from '@angular/core';
import { AuthService } from 'src/auth.service';
import { Router } from '@angular/router';
import { NgModule } from '@angular/core';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  data = { username: '', password: '' };
  confirmPassword = '';
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    if (this.data.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.authService.register(this.data).subscribe({
      next: () => {
        this.successMessage = 'Registration successful. Please login.';
        this.router.navigate(['/login']);
      },
      error: () => {
        this.errorMessage = 'Registration failed. Try again.';
      },
    });
  }
}
