package lab2.repository.impl;

import lab2.model.Game;
import lab2.model.Team;
import lab2.repository.GameRepository;
import lab2.repository.TeamRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StubGameRepository implements GameRepository {

    private final List<Game> games = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final TeamRepository teamRepository;

    public StubGameRepository(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        initTestData();
    }

    private void initTestData() {
        List<Team> teams = teamRepository.findAll();
        if (teams.size() >= 4) {
            games.add(new Game(idGenerator.getAndIncrement(), 
                    LocalDateTime.now().plusDays(1), teams.get(0), teams.get(1)));
            games.add(new Game(idGenerator.getAndIncrement(), 
                    LocalDateTime.now().plusDays(2), teams.get(2), teams.get(3)));
            games.add(new Game(idGenerator.getAndIncrement(), 
                    LocalDateTime.now().plusDays(3), teams.get(0), teams.get(2)));
            games.add(new Game(idGenerator.getAndIncrement(), 
                    LocalDateTime.now().minusDays(1), teams.get(1), teams.get(3)));
        }
    }

    @Override
    public List<Game> findAll() {
        return new ArrayList<>(games);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Game> findByTeamName(String teamName) {
        String lowerName = teamName.toLowerCase();
        return games.stream()
                .filter(g -> g.getHomeTeam().getName().toLowerCase().contains(lowerName) ||
                            g.getAwayTeam().getName().toLowerCase().contains(lowerName))
                .toList();
    }

    @Override
    public Game save(Game game) {
        if (game.getId() == null) {
            game.setId(idGenerator.getAndIncrement());
            games.add(game);
        } else {
            games.removeIf(g -> g.getId().equals(game.getId()));
            games.add(game);
        }
        return game;
    }

    @Override
    public void deleteById(Long id) {
        games.removeIf(g -> g.getId().equals(id));
    }
}
