package com.example.proiectssc.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MemoryEntries")
@ToString
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Memory {
    @Id
    @Column(unique = true)
    private Integer address;
    private Integer data;
}
