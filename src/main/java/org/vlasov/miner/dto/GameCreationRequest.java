package org.vlasov.miner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameCreationRequest {
    private int width;
    private int height;
    @JsonProperty("mines_count")
    private int minesCount;
}

