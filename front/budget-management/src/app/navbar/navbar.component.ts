import { Component, OnInit } from '@angular/core';
import { KeycloakService } from '../services/keycloack/keycloak.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  username: string = '';

  constructor(private keycloakService: KeycloakService) {}

  ngOnInit(): void {
    this.username = this.keycloakService.getUsername();
  }
  logout(): void {
  this.keycloakService.logout();
}

}
