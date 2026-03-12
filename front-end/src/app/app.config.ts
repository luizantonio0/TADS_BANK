import { ApplicationConfig, provideBrowserGlobalErrorListeners, LOCALE_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideNgxMask } from 'ngx-mask';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';

import { routes } from './app.routes';

registerLocaleData(localePt);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideNgxMask(),
    { provide: LOCALE_ID, useValue: 'pt-BR' }
  ]
};