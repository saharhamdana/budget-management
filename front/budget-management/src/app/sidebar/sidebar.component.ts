import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { KeycloakService } from '../services/keycloack/keycloak.service';

@Component({
  selector: 'app-sidebar',
  imports: [RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {
 username: string = '';

  constructor(private keycloakService: KeycloakService) {}

  ngOnInit(): void {
    this.username = this.keycloakService.getUsername();
  }

}
