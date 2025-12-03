package lab2.repository;

import lab2.model.Team;
import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    List<Team> findAll();
    Optional<Team> findById(Long id);
    List<Team> findByNameContaining(String name);
    Team save(Team team);
    void deleteById(Long id);
}
