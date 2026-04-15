package main.java.MS_Gerente.bantads.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_gerente")
public class Gerente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
        