package com.ac.su.community.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    @Column(name="board_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private BoardType boardType;
}
