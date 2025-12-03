package lab2.repository.impl;

import lab2.model.Team;
import lab2.repository.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StubTeamRepository implements TeamRepository {

    private final List<Team> teams = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public StubTeamRepository() {
        teams.add(new Team(idGenerator.getAndIncrement(), "Динамо Київ"));
        teams.add(new Team(idGenerator.getAndIncrement(), "Шахтар Донецьк"));
        teams.add(new Team(idGenerator.getAndIncrement(), "Зоря Луганськ"));
        teams.add(new Team(idGenerator.getAndIncrement(), "Дніпро-1"));
        teams.add(new Team(idGenerator.getAndIncrement(), "Ворскла Полтава"));
    }

    @Override
    public List<Team> findAll() {
        return new ArrayList<>(teams);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return teams.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Team> findByNameContaining(String name) {
        return teams.stream()
                .filter(t -> t.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(idGenerator.getAndIncrement());
            teams.add(team);
        } else {
            teams.removeIf(t -> t.getId().equals(team.getId()));
            teams.add(team);
        }
        return team;
    }

    @Override
    public void deleteById(Long id) {
        teams.removeIf(t -> t.getId().equals(id));
    }
}
