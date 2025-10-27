import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuBtn } from './menu-btn';

describe('MenuBtn', () => {
  let component: MenuBtn;
  let fixture: ComponentFixture<MenuBtn>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuBtn]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MenuBtn);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
