package com.library.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "managers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager {

    @Id
    Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    User user;

}
