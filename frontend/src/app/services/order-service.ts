import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ApiResponse, CreateOrderDto, Order, OrderResponse, OrderStatus } from '../global.types';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http: HttpClient = inject(HttpClient);
  private apiUrl: string = environment.apiUrl + 'orders';

  createOrder(orderData: CreateOrderDto): Observable<OrderResponse> {
    console.log(orderData);
    return this.http.post<OrderResponse>(`${this.apiUrl}`, orderData);
  }
  getOrderById(orderId: string | null): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.apiUrl}/${orderId}`);
  }
  getMyOrders(): Observable<Order[]> {
    return this.http
      .get<ApiResponse<Order[]>>(`${this.apiUrl}/my-orders`)
      .pipe(map((response) => response.data));
  }
  getOrdersByStatus(status: OrderStatus): Observable<Order[]> {
    return this.http
      .get<ApiResponse<Order[]>>(`${this.apiUrl}/status/${status}`)
      .pipe(map((response) => response.data));
  }
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<Order> {
    return this.http
      .put<ApiResponse<Order>>(`${this.apiUrl}/${orderId}/status`, {
        orderStatus: status,
      })
      .pipe(map((response) => response.data));
  }
}
