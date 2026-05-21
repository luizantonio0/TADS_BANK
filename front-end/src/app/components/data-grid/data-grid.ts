import { Component, Input, OnInit, OnChanges, SimpleChanges, ContentChild, TemplateRef, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataGridColumns } from '../../shared/models/datagrid.model';

@Component({
  selector: 'data-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-grid.html'
})
export class DataGridComponent<T extends object, V> implements OnInit, OnChanges {
  @Input() repeatTimes = 1;
  @Input({required: true}) columns: DataGridColumns[] = [];
  @Input() desktopRows = 10;
  @Input({required: true}) supplier?: () => Promise<T[]>;
  @Input() rawData?: T[];
  @Input() hideHeader = false;
  @Input() watchArgs = false;
  @Input() skip = 0;
  @Input() args?: V;
  @Input() hidePagination = false;

  @ContentChild('desktopRow') desktopRowTmpl?: TemplateRef<any>;

  data = signal<any[]>([]);
  currentPageIndex = signal(1);

  ngOnInit() {
    if (this.rawData) {
      this.handleLocalData();
    } else if (this.supplier) {
      this.fetch(false);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.watchArgs && changes['args'] && !changes['args'].firstChange) {
      this.fetch(false);
    }
  }

  async fetch(silent: boolean) {
    if (!this.supplier) return;
    try {
      const resp = await this.supplier();
      this.handleResp(resp);
    } catch (err: any) {
      console.error(err.message);
    }
  }

  handleResp(resp: any) {
    this.data.set(resp);
  }

  handleLocalData() {
    if (this.rawData) {
      this.data.set(this.rawData);
    }
  }
}