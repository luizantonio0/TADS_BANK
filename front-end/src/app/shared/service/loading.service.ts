import { Injectable, signal } from '@angular/core';
import { Observable, defer, finalize } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private loadingSignal = signal<boolean>(false);
  readonly isLoading = this.loadingSignal.asReadonly();

  async withLoading<T>(operation: () => Promise<T>): Promise<T> {
    try {
      this.loadingSignal.set(true);
      return await operation();
    } finally {
      this.loadingSignal.set(false);
    }
  }

  withLoadingObservable<T>(source$: Observable<T>): Observable<T> {
    return defer(() => {
      this.loadingSignal.set(true);
      return source$;
    }).pipe(
      finalize(() => this.loadingSignal.set(false))
    );
  }
}