import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalTransferir } from './modal-transferir';

describe('ModalTransferir', () => {
  let component: ModalTransferir;
  let fixture: ComponentFixture<ModalTransferir>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalTransferir],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalTransferir);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
