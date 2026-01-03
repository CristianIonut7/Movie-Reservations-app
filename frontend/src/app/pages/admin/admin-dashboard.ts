import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {
  private http = inject(HttpClient);
  
  topMovies: any[] = [];
  vipClients: any[] = [];
  statsLoaded = false;

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    // Apelăm rutele pentru cele 4 interogări complexe
    this.http.get<any[]>('http://localhost:8080/api/admin/top-movies').subscribe(data => this.topMovies = data);
    this.http.get<any[]>('http://localhost:8080/api/admin/vip-clients').subscribe(data => this.vipClients = data);
    this.statsLoaded = true;
  }

  promoteUser(email: string) {
    this.http.post(`http://localhost:8080/api/admin/promote`, { email }, { responseType: 'text' })
      .subscribe(() => {
        alert("Utilizator promovat!");
        this.loadStats();
      });
  }
}