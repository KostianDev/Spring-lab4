package lab2.repository;

import lab2.model.GameResult;
import java.util.List;
import java.util.Optional;

public interface GameResultRepository {
    List<GameResult> findAll();
    Optional<GameResult> findById(Long id);
    Optional<GameResult> findByGameId(Long gameId);
    GameResult save(GameResult result);
    void deleteById(Long id);
}
