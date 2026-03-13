import { Component, Input, OnInit, OnChanges, SimpleChanges, ContentChild, TemplateRef, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataGridColumns, DataGridRequest, DataGridResponse } from '../../shared/models/datagrid.model';

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
  @Input({required: true}) supplier?: (req: DataGridRequest<T,V>) => Promise<DataGridResponse<T>>;
  @Input() rawData?: T[];
  @Input() hideHeader = false;
  @Input() watchArgs = false;
  @Input() skip = 0;
  @Input() args?: V;
  @Input() hidePagination = false;

  @ContentChild('desktopRow') desktopRowTmpl?: TemplateRef<any>;

  currentPageData = signal<any>(null);
  currentPageIndex = signal(1);
  totalRecords = signal(0);

  totalPages = computed(() => Math.ceil(this.totalRecords() / this.desktopRows));
  indexRange = computed(() => [
    ((this.currentPageIndex() - 1) * this.desktopRows) + 1,
    Math.min(this.currentPageIndex() * this.desktopRows, this.totalRecords())
  ]);

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
      const resp = await this.supplier({
        page: this.currentPageIndex(),
        page_size: this.desktopRows,
        args: this.args,
      });
      this.handleResp(resp);
    } catch (err: any) {
      // Aqui entraria seu useToast()
      console.error(err.message);
    }
  }

  handleResp(resp: any) {
    this.currentPageData.set(resp);
    this.totalRecords.set(resp.total_count);
  }

  handleLocalData() {
    if (this.rawData) {
      this.currentPageData.set({ data: this.rawData });
      this.totalRecords.set(this.rawData.length);
    }
  }

  previous() {
    if (this.currentPageIndex() === 1) return;
    this.currentPageIndex.update(v => v - 1);
    this.fetch(false);
  }

  next() {
    if (this.currentPageIndex() >= this.totalPages()) return;
    this.currentPageIndex.update(v => v + 1);
    this.fetch(false);
  }

  getDataForTable(index: number) {
    const data = this.currentPageData()?.data || [];
    const rowsPerTable = Math.ceil(data.length / this.repeatTimes);
    const start = (index - 1) * rowsPerTable;
    return data.slice(start, start + rowsPerTable);
  }
}