import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  
  email: string = '';
  password: string = '';

  http = inject(HttpClient);
  router = inject(Router);

  onLogin() {
    const loginData = {
      email: this.email,
      password: this.password
    };

    this.http.post('http://localhost:8080/api/auth/login', loginData, { responseType: 'text' })
      .subscribe({
        next: (response: any) => {
          alert('Succes: ' + response);
        },
        error: (error: any) => {
          console.error(error);
          alert('Eroare: Login eșuat! Verifică consola.');
        }
      });
  }
}
