import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  private http = inject(HttpClient);

  activeTab: string = 'stats';

  topMovies: any[] = [];
  vipClients: any[] = [];
  moviesWithoutBookings: any[] = [];
  revenueData: any[] = [];
  genreStats: any[] = [];
  statsLoaded = false;

  movies: any[] = [];
  showtimes: any[] = [];

  isEditingMovie = false;
  isEditingShowtime = false;

  newMovie: any = {
    movieID: 0,
    title: '',
    description: '',
    genre: '',
    durationMinutes: 0,
    releaseDate: '',
    minAge: 0,
    directorFirstName: '',
    directorLastName: ''
  };

  newShowtime: any = {
    showtimeID: 0,
    movieID: 0,
    roomID: 0,
    startTime: '',
    ticketPrice: 0
  };

  rooms: any[] = [
    { RoomID: 1, RoomType: 'Normal' },
    { RoomID: 2, RoomType: 'VIP' },
    { RoomID: 3, RoomType: 'IMAX' },
    { RoomID: 4, RoomType: '7D' },
    { RoomID: 5, RoomType: '4K' }
  ];

  ngOnInit() {
    this.loadStats();
  }

  switchTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'movies') this.loadMovies();
    if (tab === 'showtimes') {
      this.loadMovies();
      this.loadShowtimes();
    }
  }

  loadStats() {
    this.http.get<any[]>('http://localhost:8080/api/admin/top-movies').subscribe(data => this.topMovies = data);
    this.http.get<any[]>('http://localhost:8080/api/admin/vip-clients').subscribe(data => this.vipClients = data);
    this.http.get<any[]>('http://localhost:8080/api/admin/movies-without-bookings').subscribe(data => this.moviesWithoutBookings = data);
    this.http.get<any[]>('http://localhost:8080/api/admin/revenue').subscribe(data => this.revenueData = data);
    this.http.get<any[]>('http://localhost:8080/api/admin/genre-stats').subscribe(data => this.genreStats = data);
    this.statsLoaded = true;
  }

  promoteUser(email: string) {
    this.http.post(`http://localhost:8080/api/admin/promote?email=${email}`, {}, { responseType: 'text' })
      .subscribe({
        next: (msg) => { alert(msg); this.loadStats(); },
        error: (err) => alert("Eroare: " + err.message)
      });
  }

  loadMovies() {
    this.http.get<any[]>('http://localhost:8080/api/movies').subscribe(data => this.movies = data);
  }

  editMovie(movie: any) {
    this.isEditingMovie = true;
    this.newMovie = { ...movie };
  }

  cancelEditMovie() {
    this.isEditingMovie = false;
    this.newMovie = { movieID: 0, title: '', description: '', genre: '', durationMinutes: 0, releaseDate: '', minAge: 0, directorFirstName: '', directorLastName: '' };
  }

  saveMovie() {
    if (this.isEditingMovie) {
      this.http.put(`http://localhost:8080/api/movies/${this.newMovie.movieID}`, this.newMovie, { responseType: 'text' }).subscribe(() => {
        alert('Film actualizat!');
        this.loadMovies();
        this.cancelEditMovie();
      });
    } else {
      this.http.post('http://localhost:8080/api/movies', this.newMovie, { responseType: 'text' }).subscribe(() => {
        alert('Film adăugat!');
        this.loadMovies();
        this.cancelEditMovie();
      });
    }
  }

  deleteMovie(id: number) {
    if (confirm('Ștergi filmul?')) {
      this.http.delete(`http://localhost:8080/api/movies/${id}`, { responseType: 'text' }).subscribe(() => this.loadMovies());
    }
  }

  loadShowtimes() {
    this.http.get<any[]>('http://localhost:8080/api/showtimes').subscribe(data => this.showtimes = data);
  }

  editShowtime(showtime: any) {
    this.isEditingShowtime = true;
    this.newShowtime = { ...showtime };
  }

  cancelEditShowtime() {
    this.isEditingShowtime = false;
    this.newShowtime = { showtimeID: 0, movieID: 0, roomID: 0, startTime: '', ticketPrice: 0 };
  }

  saveShowtime() {
    if (this.isEditingShowtime) {
      this.http.put(`http://localhost:8080/api/showtimes/${this.newShowtime.showtimeID}`, this.newShowtime, { responseType: 'text' }).subscribe(() => {
        alert('Difuzare actualizată!');
        this.loadShowtimes();
        this.cancelEditShowtime();
      });
    } else {
      this.http.post('http://localhost:8080/api/showtimes', this.newShowtime, { responseType: 'text' }).subscribe(() => {
        alert('Difuzare adăugată!');
        this.loadShowtimes();
        this.cancelEditShowtime();
      });
    }
  }

  deleteShowtime(id: number) {
    if (confirm('Ștergi difuzarea?')) {
      this.http.delete(`http://localhost:8080/api/showtimes/${id}`, { responseType: 'text' }).subscribe(() => this.loadShowtimes());
    }
  }
}