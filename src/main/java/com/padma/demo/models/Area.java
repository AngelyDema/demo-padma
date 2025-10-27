package com.padma.demo.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "areas")
@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // constructor vacío
@AllArgsConstructor // constructor con todos los campos
public class Area {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="area_id", nullable = false)
    private Long areaId;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "areas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habit> habits = new ArrayList<>(); // Agregación: un área puede contener muchos hábitos

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;  //Muchas áreas pueden pertenecer a un usuario

}