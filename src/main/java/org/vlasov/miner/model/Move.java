package org.vlasov.miner.model;

import lombok.Data;


@Data
public class Move {
    private String game_id;
    private int row;
    private int col;
}
