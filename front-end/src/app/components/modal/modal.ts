import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'modal',
  imports: [],
  templateUrl: './modal.html'
})
export class Modal {
  
  @Input({required: true}) control!: boolean;
  @Input({required: true}) title!: string;
  @Input({required: true}) icon!: string;
  @Input() submitText?: string;
  @Input() submitCallback?: () => void;
  @Input() submitDisabled?: boolean;

  @Output() close = new EventEmitter();

  onClose() {
    this.close.emit();
  }
}
