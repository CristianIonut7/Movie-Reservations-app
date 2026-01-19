import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrl: '../login/login.css'
})
export class Profile implements OnInit {
  http = inject(HttpClient);
  router = inject(Router);

  // Modelul de date pentru utilizator
  user: any = {
    userID: 0,
    firstName: '',
    lastName: '',
    email: '',
    age: null,
    city: '',
    phoneNumber: '',
    loyaltyPoints: 0
  };

  // Date pentru schimbarea parolei
  passwordData = {
    oldPassword: '',
    newPassword: ''
  };

  ngOnInit() {
    // Încărcăm datele salvate la login
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const basicUser = JSON.parse(savedUser);
      this.user = { ...this.user, ...basicUser };

      // Facem un refresh de la backend
      if (this.user.userID) {
        this.http.get<any>(`http://localhost:8080/api/auth/user/${this.user.userID}`).subscribe({
          next: (fullUser) => {
            this.user = fullUser;
            localStorage.setItem('currentUser', JSON.stringify(this.user));
          },
          error: (err) => console.error("Could not fetch user details", err)
        });
      }
    }
  }

  onUpdate() {
    this.http.put('http://localhost:8080/api/auth/update-profile', this.user, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert(res);
          localStorage.setItem('currentUser', JSON.stringify(this.user));
        },
        error: (err) => alert('Eroare: ' + err.error)
      });
  }

  onChangePassword() {
    if (!this.passwordData.oldPassword || !this.passwordData.newPassword) {
      alert("Te rog completează ambele câmpuri pentru parolă.");
      return;
    }

    const payload = {
      userId: this.user.userID,
      oldPassword: this.passwordData.oldPassword,
      newPassword: this.passwordData.newPassword
    };

    this.http.post('http://localhost:8080/api/auth/update-password', payload, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert(res);
          this.passwordData.oldPassword = '';
          this.passwordData.newPassword = '';
        },
        error: (err) => alert("Eroare: " + (err.error || "Nu s-a putut schimba parola."))
      });
  }

  onDeleteAccount() {
    if (confirm("ATENȚIE! Ești sigur că vrei să îți ștergi contul? Această acțiune este ireversibilă și va șterge toate rezervările tale.")) {
      this.http.delete(`http://localhost:8080/api/auth/delete-account/${this.user.userID}`, { responseType: 'text' })
        .subscribe({
          next: (res) => {
            alert(res);
            localStorage.removeItem('currentUser');
            // Folosim window.location.href pentru a reseta complet starea aplicației
            window.location.href = '/login';
          },
          error: (err) => alert("Eroare la ștergerea contului: " + err.error)
        });
    }
  }
}