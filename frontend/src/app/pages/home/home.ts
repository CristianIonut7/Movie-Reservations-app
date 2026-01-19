import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MovieService } from '../../services/movie.service';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  movieService = inject(MovieService);
  movies: any[] = [];

  searchQuery: string = '';
  selectedGenre: string = '';

  ngOnInit(): void {
    this.loadMovies();
  }

  loadMovies() {
    this.movieService.searchMovies(this.searchQuery, this.selectedGenre).subscribe({
      next: (data) => {
        this.movies = data;
      },
      error: (err) => console.error('Eroare la search:', err)
    });
  }

  onSearch() {
    this.loadMovies();
  }
}