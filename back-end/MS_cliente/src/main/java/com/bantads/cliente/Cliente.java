package com.bantads.cliente;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
        