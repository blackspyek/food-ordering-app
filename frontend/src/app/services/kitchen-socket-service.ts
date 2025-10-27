import { inject, Injectable, OnDestroy, signal } from '@angular/core';
import { Order, OrderStatus } from '../global.types';
import { Client } from '@stomp/stompjs';
import { environment } from '../../environments/environment';
import { OrderService } from './order-service';
import { forkJoin } from 'rxjs';

export interface KitchenOrderUpdate {
  action: 'NEW' | 'STATUS_CHANGED' | 'REMOVED';
  order: KitchenOrder;
}

export interface KitchenOrder {
  orderId: string;
  boardCode: string;
  status: OrderStatus;
  orderType: string;
  name: string;
  email: string;
  totalPrice: number;
  orderTime: string;
  orderItems: KitchenOrderItem[];
}

export interface KitchenOrderItem {
  orderItemId: number;
  itemName: string;
  quantity: number;
  totalPrice: number;
}

@Injectable({
  providedIn: 'root',
})
export class KitchenSocketService implements OnDestroy {
  private orderService = inject(OrderService);

  readonly inPreparationOrders = signal<KitchenOrder[]>([]);
  readonly readyForPickupOrders = signal<KitchenOrder[]>([]);
  readonly isConnected = signal<boolean>(false);
  readonly isLoading = signal<boolean>(true);

  private client: Client;
  private audioContext: AudioContext | null = null;

  constructor() {
    this.client = new Client({
      brokerURL: environment.wsBrokerUrl,
      reconnectDelay: 5000,
      onConnect: () => {
        this.isConnected.set(true);
        this.subscribeToKitchenOrders();
        this.loadInitialOrders();
      },
      onStompError: (frame) => {
        console.error('STOMP: Broker reported error: ' + frame.headers['message']);
        this.isConnected.set(false);
      },
      onWebSocketClose: (event) => {
        console.warn('STOMP: WebSocket closed', event);
        this.isConnected.set(false);
      },
    });
    this.client.activate();
  }

  private subscribeToKitchenOrders(): void {
    this.client.subscribe('/topic/kitchenOrders', (message) => {
      if (message.body) {
        try {
          const update: KitchenOrderUpdate = JSON.parse(message.body);
          this.handleOrderUpdate(update);
        } catch (e) {
          console.error('STOMP: JSON Parse error', e);
        }
      }
    });
  }

  private loadInitialOrders(): void {
    this.isLoading.set(true);
    forkJoin({
      inPreparation: this.orderService.getOrdersByStatus('IN_PREPARATION'),
      readyForPickup: this.orderService.getOrdersByStatus('READY_FOR_PICKUP'),
    }).subscribe({
      next: ({ inPreparation, readyForPickup }) => {
        this.inPreparationOrders.set(inPreparation.map((o) => this.mapOrderToKitchenOrder(o)));
        this.readyForPickupOrders.set(readyForPickup.map((o) => this.mapOrderToKitchenOrder(o)));
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load initial orders', err);
        this.isLoading.set(false);
      },
    });
  }

  private mapOrderToKitchenOrder(order: Order): KitchenOrder {
    return {
      orderId: order.orderId,
      boardCode: String(order.boardCode),
      status: order.status,
      orderType: order.orderType,
      name: order.email || '',
      email: order.email || '',
      totalPrice: order.totalPrice,
      orderTime: order.orderTime,
      orderItems: (order.orderItems || []).map((item) => ({
        orderItemId: item.orderItemId || 0,
        itemName: item.item?.name || 'Unknown',
        quantity: item.quantity,
        totalPrice: item.totalPrice,
      })),
    };
  }

  private handleOrderUpdate(update: KitchenOrderUpdate): void {
    const { action, order } = update;

    switch (action) {
      case 'NEW':
        this.inPreparationOrders.update((orders) => [...orders, order]);
        this.playNewOrderSound();
        break;

      case 'STATUS_CHANGED':
        if (order.status === 'IN_PREPARATION') {
          // Move from ready to in-preparation
          this.readyForPickupOrders.update((orders) =>
            orders.filter((o) => o.orderId !== order.orderId),
          );
          this.inPreparationOrders.update((orders) => {
            const exists = orders.some((o) => o.orderId === order.orderId);
            if (exists) {
              return orders.map((o) => (o.orderId === order.orderId ? order : o));
            }
            return [...orders, order];
          });
        } else if (order.status === 'READY_FOR_PICKUP') {
          // Move from in-preparation to ready
          this.inPreparationOrders.update((orders) =>
            orders.filter((o) => o.orderId !== order.orderId),
          );
          this.readyForPickupOrders.update((orders) => {
            const exists = orders.some((o) => o.orderId === order.orderId);
            if (exists) {
              return orders.map((o) => (o.orderId === order.orderId ? order : o));
            }
            return [...orders, order];
          });
        }
        break;

      case 'REMOVED':
        this.inPreparationOrders.update((orders) =>
          orders.filter((o) => o.orderId !== order.orderId),
        );
        this.readyForPickupOrders.update((orders) =>
          orders.filter((o) => o.orderId !== order.orderId),
        );
        break;
    }
  }

  private playNewOrderSound(): void {
    try {
      if (!this.audioContext) {
        this.audioContext = new AudioContext();
      }

      const ctx = this.audioContext;
      const oscillator = ctx.createOscillator();
      const gainNode = ctx.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(ctx.destination);

      // Play a pleasant notification sound (two-tone bell)
      oscillator.frequency.setValueAtTime(830, ctx.currentTime); // First tone
      oscillator.frequency.setValueAtTime(1046, ctx.currentTime + 0.15); // Second tone (higher)

      gainNode.gain.setValueAtTime(0.3, ctx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.4);

      oscillator.start(ctx.currentTime);
      oscillator.stop(ctx.currentTime + 0.4);
    } catch (err) {
      console.warn('Could not play notification sound:', err);
    }
  }

  refreshOrders(): void {
    this.loadInitialOrders();
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
