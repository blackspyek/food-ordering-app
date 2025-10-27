import { Component, ChangeDetectorRef, inject, OnInit, OnDestroy, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CdkDragDrop,
  CdkDrag,
  CdkDropList,
  CdkDropListGroup,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { KitchenSocketService, KitchenOrder } from '../../services/kitchen-socket-service';
import { OrderService } from '../../services/order-service';
import {
  ConfirmCancelDialog,
  ConfirmCancelDialogData,
} from '../../components/confirm-cancel-dialog/confirm-cancel-dialog';

@Component({
  selector: 'app-orders-management',
  imports: [CommonModule, CdkDropListGroup, CdkDropList, CdkDrag, MatIcon],
  templateUrl: './orders-management.html',
  styleUrl: './orders-management.scss',
})
export class OrdersManagement implements OnInit, OnDestroy {
  private dialog = inject(MatDialog);
  private orderService = inject(OrderService);
  private cdr = inject(ChangeDetectorRef);
  kitchenSocketService = inject(KitchenSocketService);

  // Local mutable arrays for cdkDropList
  inPreparationOrders: KitchenOrder[] = [];
  readyForPickupOrders: KitchenOrder[] = [];
  isLoading = true;

  constructor() {
    // Sync with KitchenSocketService signals
    effect(() => {
      const inPrep = this.kitchenSocketService.inPreparationOrders();
      this.inPreparationOrders = [...inPrep];
      this.cdr.detectChanges();
    });

    effect(() => {
      const ready = this.kitchenSocketService.readyForPickupOrders();
      this.readyForPickupOrders = [...ready];
      this.cdr.detectChanges();
    });

    effect(() => {
      this.isLoading = this.kitchenSocketService.isLoading();
      this.cdr.detectChanges();
    });
  }

  ngOnInit(): void {
    // Initial data is loaded by KitchenSocketService
  }

  ngOnDestroy(): void {
    // Cleanup if needed
  }

  drop(event: CdkDragDrop<KitchenOrder[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      const order = event.previousContainer.data[event.previousIndex];
      const newStatus = event.container.id === 'ready-list' ? 'READY_FOR_PICKUP' : 'IN_PREPARATION';

      // Optimistically move the item
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );

      // Update status on backend
      this.orderService.updateOrderStatus(order.orderId, newStatus).subscribe({
        error: (err) => {
          console.error('Failed to update order status', err);
          // Revert on error
          transferArrayItem(
            event.container.data,
            event.previousContainer.data,
            event.currentIndex,
            event.previousIndex,
          );
        },
      });
    }
  }

  cancelOrder(order: KitchenOrder): void {
    const dialogRef = this.dialog.open(ConfirmCancelDialog, {
      width: '400px',
      data: {
        boardCode: order.boardCode,
        orderId: order.orderId,
      } as ConfirmCancelDialogData,
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.orderService.updateOrderStatus(order.orderId, 'CANCELLED').subscribe({
          next: () => {
            console.log('Order cancelled successfully');
          },
          error: (err) => {
            console.error('Failed to cancel order', err);
          },
        });
      }
    });
  }

  getOrderTypeLabel(orderType: string): string {
    return orderType === 'DINE_IN' ? 'Na miejscu' : 'Na wynos';
  }

  formatTime(orderTime: string): string {
    const date = new Date(orderTime);
    return date.toLocaleTimeString('pl-PL', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
