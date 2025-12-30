import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/movies';

  // Această metodă va apela interogarea SQL de tip JOIN din backend [cite: 36, 49]
  getShowtimes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/showtimes`);
  }
}