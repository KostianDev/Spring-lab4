package lab2.repository.impl;

import lab2.model.Game;
import lab2.model.GameResult;
import lab2.repository.GameRepository;
import lab2.repository.GameResultRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StubGameResultRepository implements GameResultRepository {

    private final List<GameResult> results = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private GameRepository gameRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        initTestData();
    }

    private void initTestData() {
        List<Game> games = gameRepository.findAll();
        if (games.size() >= 4) {
            Game pastGame = games.get(3);
            results.add(new GameResult(idGenerator.getAndIncrement(), pastGame, 2, 1));
        }
    }

    @Override
    public List<GameResult> findAll() {
        return new ArrayList<>(results);
    }

    @Override
    public Optional<GameResult> findById(Long id) {
        return results.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<GameResult> findByGameId(Long gameId) {
        return results.stream()
                .filter(r -> r.getGame().getId().equals(gameId))
                .findFirst();
    }

    @Override
    public GameResult save(GameResult result) {
        if (result.getId() == null) {
            result.setId(idGenerator.getAndIncrement());
            results.add(result);
        } else {
            results.removeIf(r -> r.getId().equals(result.getId()));
            results.add(result);
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        results.removeIf(r -> r.getId().equals(id));
    }
}
