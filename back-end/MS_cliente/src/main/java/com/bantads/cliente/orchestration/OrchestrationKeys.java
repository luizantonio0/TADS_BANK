package com.bantads.cliente.orchestration;

public class OrchestrationKeys {

    public static final String ORCHESTRATE_QUEUE = "orchestration.orchestrate";
    public static final String RESULT_QUEUE = "orchestration.result";
    public static final String CONFIRM_QUEUE = "orchestration.confirm";
    public static final String COMMAND_QUEUE = "orchestration.ms-auth.command";

    public static final String MS_AUTH = "ms-auth";
    public static final String MS_GERENTE = "ms-gerente";
    public static final String MS_CONTA = "ms-conta";
    public static final String MS_CLIENTE = "ms-cliente";

    public static final String CREATE_CREDENTIALS_COMMAND = "CreateCredentials";
    public static final String CREATE_CONTA_COMMAND = "CreateConta";
    public static final String CREATE_CLIENTE_COMMAND = "CreateCliente";
    public static final String FIND_GERENTE_COMMAND = "FindGerente";
    public static final String GET_GERENTE_COMMAND = "GetGerente";
    public static final String APPROVE_CLIENTE_COMMAND = "ApproveGerente";
    public static final String UPDATE_LIMITE_COMMAND = "UpdateLimite";
    public static final String UPDATE_CLIENTE_COMMAND = "UpdateCliente";
    public static final String GET_CLIENTE_COMMAND = "GetCliente";


}
