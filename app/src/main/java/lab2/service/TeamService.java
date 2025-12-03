package lab2.service;

import lab2.model.Team;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    List<Team> getAllTeams();
    Optional<Team> getTeamById(Long id);
    List<Team> searchTeamsByName(String name);
    Team saveTeam(Team team);
    void deleteTeam(Long id);
}
