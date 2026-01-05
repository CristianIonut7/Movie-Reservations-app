import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovieService } from '../../services/movie.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  movieService = inject(MovieService);
  showtimes: any[] = [];

  ngOnInit(): void {
    this.movieService.getShowtimes().subscribe({
      next: (data) => {
        console.log('DATE PRIMITE DE LA SERVER:', data); // VEZI ASTA ÎN CONSOLĂ (F12)
        this.showtimes = data;
        console.log('Program încărcat cu succes!');
      },
      error: (err) => {
        console.error('Eroare la preluarea programului:', err);
      }
    });
  }
}