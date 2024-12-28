import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { AuthService } from 'src/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    // Proverite da li je URL zahteva login ili register
    if (req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register')) {
      // Ako jeste, preskočite dodavanje Authorization zaglavlja
      return next.handle(req);
    }

    // U suprotnom, dodajte Authorization zaglavlje
    const token = this.authService.getToken();
    if (token) {
      const cloned = req.clone({
        setHeaders: {
          Authorization: `${token}`,
        },
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
