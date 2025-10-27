import {Component, Inject} from '@angular/core';
import {MenuItem} from '../../global.types';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {CurrencyPipe} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {CartService} from '../../services/cart-service';


@Component({
  selector: 'app-item-details-dialog',
  imports: [
    CurrencyPipe,
    MatDialogActions,
    MatDialogContent,
    MatIcon,
    MatDialogTitle
  ],
  templateUrl: './item-details-dialog.html',
  styleUrl: './item-details-dialog.scss'
})
export class ItemDetailsDialog {

  constructor(
    public dialogRef: MatDialogRef<ItemDetailsDialog>,
    public cartService: CartService,
    @Inject(MAT_DIALOG_DATA) public item: MenuItem
  ) {}
  onClose(): void {
    this.dialogRef.close();
  }
  addToCart(): void {
    this.cartService.addToCart(this.item)

    const dialogResult = {
      addedToCart: true,
      item: this.item
    };

    this.dialogRef.close(dialogResult);
  }

}
