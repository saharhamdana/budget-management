import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeycloakService } from './services/keycloack/keycloak.service';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `<router-outlet />`,
})
export class AppComponent implements OnInit {
  username = '';
  title = 'budget-management';

  constructor(private keycloak: KeycloakService) {}

  ngOnInit(): void {
    this.username = this.keycloak.getUsername();
    console.log('Token parsed:', this.keycloak.keycloak.tokenParsed); // Pour debug
  }

  logout() {
    this.keycloak.logout();
  }
}