import { Component,Inject } from '@angular/core';
import {MAT_DIALOG_DATA,MatDialogActions,MatDialogContent, MatDialogRef,MatDialogTitle} from '@angular/material/dialog';
import {Order} from '../../global.types';
import {CurrencyPipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-order-details-dialog',
  imports: [
CurrencyPipe ,
MatDialogActions ,
MatDialogTitle ,
MatDialogContent ,
RouterLink
],
  templateUrl: './order-details-dialog.html',
  styleUrl: './order-details-dialog.scss'
})
export class OrderDetailsDialog {
  constructor(
      public dialogRef: MatDialogRef<OrderDetailsDialog>,
      @Inject(MAT_DIALOG_DATA) public data: Order,
    ) {}

    onClose(): void {
      this.dialogRef.close();
    }


}
