package lab2.controller;

import lab2.model.Team;
import lab2.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @org.springframework.beans.factory.annotation.Autowired
    private TeamService teamService;

    @GetMapping
    public String listTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "teams/list";
    }

    @GetMapping("/search")
    public String searchTeams(@RequestParam String name, Model model) {
        model.addAttribute("teams", teamService.searchTeamsByName(name));
        model.addAttribute("search", name);
        return "teams/list";
    }

    @GetMapping("/new")
    public String newTeamForm() {
        return "teams/form";
    }

    @PostMapping("/new")
    public String createTeam(@RequestParam String name) {
        Team team = new Team();
        team.setName(name);
        teamService.saveTeam(team);
        return "redirect:/teams";
    }

    @GetMapping("/edit/{id}")
    public String editTeamForm(@PathVariable Long id, Model model) {
        Optional<Team> team = teamService.getTeamById(id);
        if (team.isPresent()) {
            model.addAttribute("team", team.get());
            return "teams/edit";
        }
        return "redirect:/teams";
    }

    @PostMapping("/edit/{id}")
    public String updateTeam(@PathVariable Long id, @RequestParam String name) {
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        teamService.saveTeam(team);
        return "redirect:/teams";
    }

    @PostMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}
