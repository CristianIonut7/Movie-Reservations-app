import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrl: '../login/login.css' // Putem refolosi stilul de login pentru consistență
})
export class Profile implements OnInit {
  http = inject(HttpClient);
  
  // Modelul de date pentru utilizator
  user: any = {
    firstName: '',
    lastName: '',
    email: '',
    age: null,
    city: '',
    phoneNumber: ''
  };

  ngOnInit() {
    // Încărcăm datele salvate la login
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      this.user = JSON.parse(savedUser);
      // Opțional: Poți face un apel GET la backend aici pentru a lua datele cele mai proaspete
    }
  }

  onUpdate() {
    this.http.put('http://localhost:8080/api/auth/update-profile', this.user, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert(res);
          // Actualizăm și memoria locală
          localStorage.setItem('currentUser', JSON.stringify(this.user));
        },
        error: (err) => alert('Eroare: ' + err.error)
      });
  }
}