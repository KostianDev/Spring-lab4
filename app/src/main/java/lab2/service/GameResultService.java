package lab2.service;

import lab2.model.GameResult;
import java.util.List;
import java.util.Optional;

public interface GameResultService {
    List<GameResult> getAllResults();
    Optional<GameResult> getResultById(Long id);
    Optional<GameResult> getResultByGameId(Long gameId);
    GameResult saveResult(GameResult result);
    void deleteResult(Long id);
}
