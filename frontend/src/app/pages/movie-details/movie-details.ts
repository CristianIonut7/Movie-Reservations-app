import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { MovieService } from '../../services/movie.service';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-movie-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-details.html',
  styleUrls: ['./movie-details.css']
})
export class MovieDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private movieService = inject(MovieService);
  private router = inject(Router);

  movie: any = null;
  showtimes: any[] = [];
  movieId: number = 0;

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.movieId = +params['id'];
      this.loadData();
    });
  }

  loadData() {
    this.movieService.getMovieById(this.movieId).subscribe({
      next: (data) => {
        this.movie = data;
      },
      error: (err) => console.error('Error loading movie:', err)
    });

    this.movieService.getShowtimesByMovie(this.movieId).subscribe({
      next: (data) => {
        this.showtimes = data;
      },
      error: (err) => console.error('Error loading showtimes:', err)
    });
  }
}
