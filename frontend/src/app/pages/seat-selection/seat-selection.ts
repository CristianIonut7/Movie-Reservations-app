import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-seat-selection',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './seat-selection.html',
  styleUrl: './seat-selection.css'
})
export class SeatSelection implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  showtimeId!: number;
  seats: any[] = [];
  selectedSeatIds: number[] = [];
  userId!: number;

  // Loyalty
  userPoints: number = 0;
  usePoints: boolean = false;

  getRowLabel(rowNum: number): string {
    return String.fromCharCode(64 + rowNum);
  }

  get rows() {
    const rowsMap = new Map<number, any[]>();
    this.seats.forEach(seat => {
      if (!rowsMap.has(seat.rowNumber)) {
        rowsMap.set(seat.rowNumber, []);
      }
      rowsMap.get(seat.rowNumber)?.push(seat);
    });
    return Array.from(rowsMap.values()).sort((a, b) => a[0].rowNumber - b[0].rowNumber);
  }

  ngOnInit() {
    this.showtimeId = Number(this.route.snapshot.paramMap.get('id'));

    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      this.userId = userData.userID;

      // Fetch fresh user data (points)
      this.http.get<any>(`http://localhost:8080/api/auth/user/${this.userId}`).subscribe({
        next: (u) => {
          this.userPoints = u.loyaltyPoints || 0;
        },
        error: (e) => console.error("Could not fetch user details", e)
      });
    }

    this.loadSeats();
  }

  loadSeats() {
    this.http.get<any[]>(`http://localhost:8080/api/bookings/seats/${this.showtimeId}`)
      .subscribe(data => this.seats = data);
  }

  toggleSeat(seat: any) {
    if (seat.occupied) return;

    const index = this.selectedSeatIds.indexOf(seat.seatId);
    if (index > -1) {
      this.selectedSeatIds.splice(index, 1);
    } else {
      this.selectedSeatIds.push(seat.seatId);
    }
  }

  isSelected(seatId: number): boolean {
    return this.selectedSeatIds.includes(seatId);
  }

  confirmBooking() {
    const payload = {
      userId: this.userId,
      showtimeId: this.showtimeId,
      seatIds: this.selectedSeatIds,
      usePoints: this.usePoints
    };

    console.log("Trimit rezervarea:", payload);

    this.http.post('http://localhost:8080/api/bookings/reserve', payload, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert(res);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          // Extragem mesajul de eroare din backend (ex: varsta)
          const msg = err.error || "Eroare la rezervare!";
          alert(msg);
        }
      });
  }
}