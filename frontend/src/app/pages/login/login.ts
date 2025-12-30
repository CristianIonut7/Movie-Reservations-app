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

  // În login.ts, modifică onLogin:
  onLogin() {
    const loginData = { email: this.email, password: this.password };

    // Scoatem { responseType: 'text' } pentru că acum primim JSON
    this.http.post('http://localhost:8080/api/auth/login', loginData)
      .subscribe({
        next: (response: any) => {
          // SALVĂM DATELE AICI
          localStorage.setItem('currentUser', JSON.stringify(response));

          alert('Bine ai venit, ' + response.firstName);
          this.router.navigate(['/home']);

          // Forțăm un refresh mic sau anunțăm navbar-ul (vezi pasul 3)
          window.location.reload();
        },
        error: (error) => alert('Login eșuat!')
      });
  }
}
