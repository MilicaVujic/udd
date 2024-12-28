import { Component } from '@angular/core';
import { AuthService } from 'src/auth.service';
import { Router } from '@angular/router';
import { NgModule } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log(response.headers)
        const token = response.headers.get('authorization'); 
        if (token) {
          this.authService.saveToken(token); 
          this.router.navigate(['/home']); 
        } else {
          this.errorMessage = 'Login successful but token is missing.';
        }
      },
      error: (err) => {
        this.errorMessage = 'Invalid username or password.';
      },
    });
  }
}