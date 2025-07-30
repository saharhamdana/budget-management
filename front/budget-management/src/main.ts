import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { KeycloakService } from './app/services/keycloack/keycloak.service';

const keycloakService = new KeycloakService();

keycloakService.init().then(() => {
  // ✅ Redirection vers /home si on est sur la racine après login
  if (window.location.pathname === '/' || window.location.pathname === '/index.html') {
    window.history.replaceState({}, '', '/home');
  }

  bootstrapApplication(AppComponent, {
    providers: [
      ...appConfig.providers!,
      { provide: KeycloakService, useValue: keycloakService }
    ]
  });

  
});
