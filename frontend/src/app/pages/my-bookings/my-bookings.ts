import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-bookings.html',
  styleUrl: './my-bookings.css'
})
export class MyBookingsComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  bookings: any[] = [];
  userId!: number;

  ngOnInit() {
    // Recuperăm user-ul din localStorage așa cum am stabilit anterior
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      this.userId = userData.userID; // Folosim userID cu ID mare conform log-ului tău
      this.loadMyBookings();
    } else {
      // Dacă nu e logat, îl trimitem la login
      this.router.navigate(['/login']);
    }
  }

  loadMyBookings() {
    this.http.get<any[]>(`http://localhost:8080/api/bookings/user/${this.userId}`)
      .subscribe({
        next: (data) => {
          this.bookings = data;
          console.log("Rezervări încărcate:", this.bookings);
        },
        error: (err) => console.error("Eroare la încărcarea rezervărilor", err)
      });
  }

  confirmCancel(bookingId: number) {
    if (confirm("Ești sigur că vrei să anulezi această rezervare? Această acțiune va elibera locurile în sală.")) {
      // Implementăm operația DELETE cerută în proiect [cite: 35]
      this.http.delete(`http://localhost:8080/api/bookings/${bookingId}`, { responseType: 'text' })
        .subscribe({
          next: (res) => {
            alert(res);
            this.loadMyBookings(); // Reîmprospătăm lista după ștergere
          },
          error: (err) => alert("Eroare la anularea rezervării!")
        });
    }
  }
}