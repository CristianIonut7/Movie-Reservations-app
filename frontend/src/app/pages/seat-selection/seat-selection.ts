import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-seat-selection',
  standalone: true,
  imports: [CommonModule],
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
    // Returnăm rândurile sortate
    return Array.from(rowsMap.values()).sort((a, b) => a[0].rowNumber - b[0].rowNumber);
  }

  ngOnInit() {
    this.showtimeId = Number(this.route.snapshot.paramMap.get('id'));

    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      const userData = JSON.parse(savedUser);

      // MODIFICAREA ESTE AICI:
      // Folosim .userID (cu ID mare) exact cum apare în consola ta
      this.userId = userData.userID;

      console.log("ID-ul utilizatorului a fost setat cu succes:", this.userId);
    }

    this.loadSeats();
  }

  loadSeats() {
    this.http.get<any[]>(`http://localhost:8080/api/bookings/seats/${this.showtimeId}`)
      .subscribe(data => this.seats = data);
  }

  toggleSeat(seat: any) {
    if (seat.occupied) return; // Nu poți selecta locuri ocupate

    const index = this.selectedSeatIds.indexOf(seat.seatId);
    if (index > -1) {
      this.selectedSeatIds.splice(index, 1); // Deselectează
    } else {
      this.selectedSeatIds.push(seat.seatId); // Selectează
    }
  }

  isSelected(seatId: number): boolean {
    return this.selectedSeatIds.includes(seatId);
  }

  confirmBooking() {
    const payload = {
      userId: this.userId, // Aici va fi valoarea 2
      showtimeId: this.showtimeId,
      seatIds: this.selectedSeatIds
    };

    console.log("Trimit rezervarea pentru user-ul:", payload.userId);

    this.http.post('http://localhost:8080/api/bookings/reserve', payload, { responseType: 'text' })
      .subscribe({
        next: (res) => {
          alert(res);
          this.router.navigate(['/home']);
        },
        error: (err) => alert("Eroare la rezervare!")
      });
  }
}