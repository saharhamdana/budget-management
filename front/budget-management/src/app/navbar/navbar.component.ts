import { Component, OnInit } from '@angular/core';
import { KeycloakService } from '../services/keycloack/keycloak.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  
  username: string = '';
  email: string = '';
  firstname: string = '';
  lastname: string = '';

  constructor(private keycloakService: KeycloakService) {}

  ngOnInit(): void {
    this.username = this.keycloakService.getUsername();
    this.email = this.keycloakService.getEmail();
    this.firstname = this.keycloakService.getFirstName();
    this.lastname = this.keycloakService.getLastName();
  }
  logout(): void {
  this.keycloakService.logout();
}

}
