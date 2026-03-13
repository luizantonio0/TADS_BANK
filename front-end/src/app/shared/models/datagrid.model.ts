export interface DataGridResponse<T> {
    data: T[];
    total_count: number;
}

export interface DataGridRequest<T,V> {
    page: number;
    page_size: number;
    args?: V;
}

export interface DataGridColumns {
    size: number;
    title: string;
}