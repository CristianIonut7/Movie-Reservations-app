import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnInit {
  router = inject(Router);

  isLoggedIn: boolean = false;
  userRole: string = 'guest';
  userName: string = '';

  ngOnInit() {
    this.checkLoginStatus();
  }



  checkLoginStatus() {
    const user = localStorage.getItem('currentUser');
    if (user) {
      const userData = JSON.parse(user);
      this.isLoggedIn = true;
      this.userRole = userData.userRole;
      this.userName = userData.firstName;
    } else {
      this.isLoggedIn = false;
      this.userRole = 'guest';
    }
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.isLoggedIn = false;
    this.userRole = 'guest';

    this.router.navigate(['/login']);
  }
}