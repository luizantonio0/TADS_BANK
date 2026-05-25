package com.bantads.conta.datasource;

public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    public static void setContext(DataSourceType dataSourceType) {
        CONTEXT.set(dataSourceType);
    }

    public static DataSourceType getContext() {
        return CONTEXT.get();
    }

    public static void clearContext() {
        CONTEXT.remove();
    }
}