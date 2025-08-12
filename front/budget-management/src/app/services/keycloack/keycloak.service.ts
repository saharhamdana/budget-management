import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  keycloak!: Keycloak;
 

  init(): Promise<boolean> {
    this.keycloak = new Keycloak({
      url: 'http://localhost:9090',
      realm: 'BudgifyApplication',
      clientId: 'my-app-client',
    });

    return this.keycloak.init({
      onLoad: 'login-required',
      checkLoginIframe: false,
    });
  }

  getToken(): string {
    return this.keycloak.token!;
  }

  
  logout(): void {
    this.keycloak.logout();
  }

  getUsername(): string {
    return this.keycloak.tokenParsed?.['preferred_username'] || '';
  }

  getEmail(): string {
    return this.keycloak.tokenParsed?.['email'] || '';
  }

  getFirstName(): string {
    return this.keycloak.tokenParsed?.['given_name'] || '';
  }

  getLastName(): string {
    return this.keycloak.tokenParsed?.['family_name'] || '';
  }

}