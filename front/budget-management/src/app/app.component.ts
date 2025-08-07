import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeycloakService } from './services/keycloack/keycloak.service';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from "./sidebar/sidebar.component";
import { NavbarComponent } from "./navbar/navbar.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, NavbarComponent],
  template: ` <app-navbar></app-navbar>
    <div class="d-flex">
      <app-sidebar></app-sidebar>
      <main class="flex-grow-1 p-3">
        <router-outlet></router-outlet>
      </main>
    </div>`,
})
export class AppComponent implements OnInit {
  username = '';
  title = 'budget-management';

  constructor(private keycloak: KeycloakService) {}
  
  ngOnInit(): void {
    this.username = this.keycloak.getUsername();
    console.log('Token:', this.keycloak.getToken());
    localStorage.setItem('token', this.keycloak.getToken());
    console.log('Token parsed:', this.keycloak.keycloak.tokenParsed); // Pour debug
  }

  logout() {
    this.keycloak.logout();
  }
}