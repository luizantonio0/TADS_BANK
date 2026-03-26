package com.bantads.orquestrador.model;

import java.util.UUID;

public record Command<T>(UUID id, String commandType, String targetService, T dto) {

}
