package lab2.repository;

import lab2.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameRepository {
    List<Game> findAll();
    Optional<Game> findById(Long id);
    List<Game> findByTeamName(String teamName);
    Game save(Game game);
    void deleteById(Long id);
}
