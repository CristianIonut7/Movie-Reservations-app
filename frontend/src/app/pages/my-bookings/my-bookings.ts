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
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      this.userId = userData.userID;
      this.loadMyBookings();
    } else {
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
      this.http.delete(`http://localhost:8080/api/bookings/${bookingId}`, { responseType: 'text' })
        .subscribe({
          next: (res) => {
            alert(res);
            this.loadMyBookings();
          },
          error: (err) => alert("Eroare la anularea rezervării!")
        });
    }
  }
}