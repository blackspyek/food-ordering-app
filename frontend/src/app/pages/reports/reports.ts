import { Component, inject, signal, computed, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ReportService, ReportData } from '../../services/report-service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, MatIcon, MatButtonToggleModule, MatProgressSpinnerModule],
  templateUrl: './reports.html',
  styleUrl: './reports.scss',
})
export class Reports implements OnInit, AfterViewInit {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

  private reportService = inject(ReportService);

  reportType = signal<'daily' | 'weekly'>('daily');
  isLoading = signal(true);
  error = signal<string | null>(null);
  reportData = signal<ReportData | null>(null);

  // Computed values for display
  totalOrders = computed(() => this.reportData()?.totalOrders ?? 0);
  totalAmount = computed(() => this.reportData()?.totalAmount ?? 0);
  averageAmount = computed(() => this.reportData()?.averageAmount ?? 0);
  salesByCategory = computed(() => this.reportData()?.salesByCategory ?? {});

  categoryEntries = computed(() => {
    const sales = this.salesByCategory();
    return Object.entries(sales).map(([category, count]) => ({
      category: this.translateCategory(category),
      count: count as number,
      percentage: this.calculatePercentage(count as number),
    }));
  });

  totalItems = computed(() => {
    const sales = this.salesByCategory();
    return Object.values(sales).reduce((sum, val) => sum + (val as number), 0);
  });

  reportTitle = computed(() => (this.reportType() === 'daily' ? 'Raport dzienny' : 'Raport tygodniowy'));
  reportSubtitle = computed(() =>
    this.reportType() === 'daily'
      ? 'Statystyki sprzedaży z dzisiaj'
      : 'Statystyki sprzedaży z ostatnich 7 dni',
  );

  // Chart colors
  private chartColors = [
    '#3b82f6', // blue
    '#10b981', // green
    '#f59e0b', // amber
    '#ef4444', // red
    '#8b5cf6', // violet
    '#ec4899', // pink
    '#06b6d4', // cyan
    '#84cc16', // lime
  ];

  ngOnInit(): void {
    this.loadReport();
  }

  ngAfterViewInit(): void {
    // Chart will be drawn after data is loaded
  }

  onReportTypeChange(type: 'daily' | 'weekly'): void {
    this.reportType.set(type);
    this.loadReport();
  }

  loadReport(): void {
    this.isLoading.set(true);
    this.error.set(null);

    const request$ = this.reportType() === 'daily' ? this.reportService.getDailyReport() : this.reportService.getWeeklyReport();

    request$.subscribe({
      next: (data) => {
        this.reportData.set(data);
        this.isLoading.set(false);
        setTimeout(() => this.drawChart(), 0);
      },
      error: (err) => {
        console.error('Błąd ładowania raportu:', err);
        this.error.set('Nie udało się załadować raportu. Spróbuj ponownie.');
        this.isLoading.set(false);
      },
    });
  }

  downloadReport(): void {
    this.reportService.downloadReport(this.reportType());
  }

  private translateCategory(category: string): string {
    const translations: { [key: string]: string } = {
      PIZZA: 'Pizza',
      BURGER: 'Burgery',
      DRINK: 'Napoje',
      DESSERT: 'Desery',
      SALAD: 'Sałatki',
      PASTA: 'Makarony',
      SOUP: 'Zupy',
      SANDWICH: 'Kanapki',
      APPETIZER: 'Przystawki',
      MAIN_COURSE: 'Dania główne',
      SIDE_DISH: 'Dodatki',
    };
    return translations[category] || category;
  }

  private calculatePercentage(count: number): number {
    const total = this.totalItems();
    return total > 0 ? Math.round((count / total) * 100) : 0;
  }

  private drawChart(): void {
    if (!this.chartCanvas) return;

    const canvas = this.chartCanvas.nativeElement;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const entries = this.categoryEntries();
    if (entries.length === 0) return;

    // Clear canvas
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    ctx.scale(dpr, dpr);
    ctx.clearRect(0, 0, rect.width, rect.height);

    // Draw donut chart
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;
    const radius = Math.min(centerX, centerY) - 20;
    const innerRadius = radius * 0.6;

    let startAngle = -Math.PI / 2;
    const total = this.totalItems();

    entries.forEach((entry, index) => {
      const sliceAngle = (entry.count / total) * 2 * Math.PI;
      const endAngle = startAngle + sliceAngle;

      ctx.beginPath();
      ctx.arc(centerX, centerY, radius, startAngle, endAngle);
      ctx.arc(centerX, centerY, innerRadius, endAngle, startAngle, true);
      ctx.closePath();

      ctx.fillStyle = this.chartColors[index % this.chartColors.length];
      ctx.fill();

      startAngle = endAngle;
    });

    // Draw center text
    ctx.fillStyle = '#1f2937';
    ctx.font = 'bold 24px Inter, sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(total.toString(), centerX, centerY - 10);

    ctx.fillStyle = '#6b7280';
    ctx.font = '12px Inter, sans-serif';
    ctx.fillText('produktów', centerX, centerY + 15);
  }

  getCategoryColor(index: number): string {
    return this.chartColors[index % this.chartColors.length];
  }
}
