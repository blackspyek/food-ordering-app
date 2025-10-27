import { Component, inject, input, InputSignal, signal } from '@angular/core';
import { Category, MenuItem } from '../../global.types';
import { MenuItemBlock } from '../menu-item-block/menu-item-block';
import { MenuItemService } from '../../services/menu-item-service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { of, switchMap, tap } from 'rxjs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-menu-list',
  imports: [MenuItemBlock, MatIcon],
  templateUrl: './menu-list.html',
  styleUrl: './menu-list.scss',
  host: {
    class: 'flex flex-col h-full md:flex-row md:flex-wrap',
  },
})
export class MenuList {
  private menuItemService = inject(MenuItemService);
  chosenCategory: InputSignal<string | null> = input.required<string | null>();
  isLoading = signal(false);

  menuItems = toSignal(
    toObservable(this.chosenCategory).pipe(
      tap(() => this.isLoading.set(true)),
      switchMap((category) => {
        if (!category) {
          this.isLoading.set(false);
          return of([] as MenuItem[]);
        }
        return this.menuItemService
          .getMenuItemsByCategory(category as Category)
          .pipe(tap(() => this.isLoading.set(false)));
      }),
    ),
    { initialValue: [] as MenuItem[] },
  );
}
