package lab2.service;

import lab2.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {
    List<Game> getAllGames();
    Optional<Game> getGameById(Long id);
    List<Game> searchGamesByTeamName(String teamName);
    Game saveGame(Game game);
    void deleteGame(Long id);
}
