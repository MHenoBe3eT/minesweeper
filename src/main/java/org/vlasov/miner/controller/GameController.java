package org.vlasov.miner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vlasov.miner.dto.GameCreationRequest;
import org.vlasov.miner.model.Game;
import org.vlasov.miner.model.Move;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS})
@RestController
@RequestMapping("/games")
public class GameController {

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @PostMapping("/new")
    public ResponseEntity<?> createGame(@RequestBody GameCreationRequest request) {
        try {
            validateRequest(request);
            Game game = new Game(request.getWidth(), request.getHeight(), request.getMinesCount());
            games.put(game.getGame_id(), game);
            return new ResponseEntity<>(game, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return buildErrorResponse(e.getReason(), (HttpStatus) e.getStatusCode());
        }
    }

    private void validateRequest(GameCreationRequest request) {
        if (request.getWidth() <= 0 || request.getWidth() > 30 || request.getHeight() <= 0 || request.getHeight() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не корректный размер доски");
        }
        if (request.getMinesCount() >= request.getWidth() * request.getHeight() || request.getMinesCount() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не корректное количество мин");
        }
    }

    @PostMapping("/turn")
    public ResponseEntity<?> makeMove(@RequestBody Move move) {
        Game game = games.get(move.getGame_id());
        if (game == null) {
            return buildErrorResponse("Игра не найдена", HttpStatus.NOT_FOUND);
        }
        if (game.isCompleted()) {
            return buildErrorResponse("Игра завершена", HttpStatus.CONFLICT);
        }
        try {
            game.processMove(move);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", message);
        return new ResponseEntity<>(responseBody, status);
    }
}
