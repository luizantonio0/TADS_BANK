package com.bantads.cliente.orchestration;

public record Snapshot<T> (Class<T> clazz, T previous, T now) { }
