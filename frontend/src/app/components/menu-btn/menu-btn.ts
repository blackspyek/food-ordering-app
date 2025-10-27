import {Component, input} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-menu-btn',
  imports: [
    MatIcon, RouterLink
  ],
  templateUrl: './menu-btn.html',
  styleUrl: './menu-btn.scss'
})
export class MenuBtn {
  label = input.required<string>();
  href = input.required<string>();
  icon = input<string>("");
  is_highlighted = input(false);


}
