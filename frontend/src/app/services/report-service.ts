import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReportData {
  totalOrders: number;
  totalAmount: number | null;
  averageAmount: number | null;
  salesByCategory: { [key: string]: number };
}

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private http = inject(HttpClient);
  private API_URL = environment.apiUrl + 'reports';

  getDailyReport(): Observable<ReportData> {
    return this.http.get<ReportData>(`${this.API_URL}/daily/json`);
  }

  getWeeklyReport(): Observable<ReportData> {
    return this.http.get<ReportData>(`${this.API_URL}/weekly/json`);
  }

  downloadReport(type: 'daily' | 'weekly'): void {
    const endpoint = type === 'daily' ? 'daily/json' : 'weekly/json';
    const filename = type === 'daily' ? 'raport_dzienny.json' : 'raport_tygodniowy.json';

    this.http.get(`${this.API_URL}/${endpoint}`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Błąd pobierania raportu:', err);
      },
    });
  }
}
