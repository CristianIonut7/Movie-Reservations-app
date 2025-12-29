import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: '../login/login.css' // Refolosim stilul de la login
})
export class Signup {
  http = inject(HttpClient);
  router = inject(Router);

  userData = {
    firstName: '',
    lastName: '',
    email: '',
    passwordHash: '', // Va fi hash-uită de backend
    age: null,
    city: '',
    phoneNumber: ''
  };

  onSignup() {
    this.http.post('http://localhost:8080/api/auth/signup', this.userData, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert('Cont creat!');
          this.router.navigate(['/login']);
        },
        error: (err) => alert('Eroare: ' + err.error)
      });
  }
}