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

  ngOnInit() {
    this.showtimeId = Number(this.route.snapshot.paramMap.get('id'));
    
    // Luăm ID-ul utilizatorului din localStorage (salvat la login)
    const userData = JSON.parse(localStorage.getItem('currentUser') || '{}');
    this.userId = userData.userId; // Asigură-te că obiectul User din Java include acum și câmpul userId

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
    if (this.selectedSeatIds.length === 0) {
      alert("Te rog selectează cel puțin un loc!");
      return;
    }

    const payload = {
      userId: this.userId,
      showtimeId: this.showtimeId,
      seatIds: this.selectedSeatIds
    };

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