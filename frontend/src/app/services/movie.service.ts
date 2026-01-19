import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/movies';

  getShowtimes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/showtimes`);
  }

  searchMovies(query: string, genre: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?query=${query}&genre=${genre}`);
  }

  getMovieById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getShowtimesByMovie(movieId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/showtimes/movie/${movieId}`);
  }
}