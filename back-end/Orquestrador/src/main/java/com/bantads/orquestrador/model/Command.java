package com.bantads.orquestrador.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("Command")
public record Command<T>(UUID id, String commandType, String targetService, T dto) {

}
