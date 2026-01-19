import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  email: string = '';
  password: string = '';

  http = inject(HttpClient);
  router = inject(Router);

  onLogin() {
    const loginData = { email: this.email, password: this.password };

    this.http.post('http://localhost:8080/api/auth/login', loginData)
      .subscribe({
        next: (response: any) => {
          localStorage.setItem('currentUser', JSON.stringify(response));

          alert('Bine ai venit, ' + response.firstName);

          window.location.href = '/home';
        },
        error: (error) => alert('Login eșuat!')
      });
  }
}
