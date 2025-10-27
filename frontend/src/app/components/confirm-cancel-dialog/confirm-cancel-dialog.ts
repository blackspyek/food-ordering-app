import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';

export interface ConfirmCancelDialogData {
  boardCode: string;
  orderId: string;
}

@Component({
  selector: 'app-confirm-cancel-dialog',
  imports: [MatDialogActions, MatDialogContent, MatDialogTitle, MatIcon],
  templateUrl: './confirm-cancel-dialog.html',
  styleUrl: './confirm-cancel-dialog.scss',
})
export class ConfirmCancelDialog {
  constructor(
    public dialogRef: MatDialogRef<ConfirmCancelDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmCancelDialogData,
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
