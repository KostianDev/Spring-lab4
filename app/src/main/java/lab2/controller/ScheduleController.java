package lab2.controller;

import lab2.model.Game;
import lab2.model.GameResult;
import lab2.model.Team;
import lab2.service.GameResultService;
import lab2.service.GameService;
import lab2.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final GameService gameService;
    private final TeamService teamService;
    private final GameResultService gameResultService;

    public ScheduleController(GameService gameService, TeamService teamService, 
                              GameResultService gameResultService) {
        this.gameService = gameService;
        this.teamService = teamService;
        this.gameResultService = gameResultService;
    }

    @GetMapping
    public String viewSchedule(@RequestParam(required = false) String search, Model model) {
        List<Game> games;
        if (search != null && !search.trim().isEmpty()) {
            games = gameService.searchGamesByTeamName(search);
            model.addAttribute("search", search);
        } else {
            games = gameService.getAllGames();
        }
        model.addAttribute("games", games);
        model.addAttribute("results", gameResultService.getAllResults());
        return "schedule/list";
    }

    @GetMapping("/new")
    public String newGameForm(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "schedule/form";
    }

    @PostMapping("/new")
    public String createGame(@RequestParam Long homeTeamId,
                            @RequestParam Long awayTeamId,
                            @RequestParam String dateTime) {
        Team homeTeam = teamService.getTeamById(homeTeamId).orElseThrow();
        Team awayTeam = teamService.getTeamById(awayTeamId).orElseThrow();
        
        Game game = new Game();
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);
        game.setDateTime(LocalDateTime.parse(dateTime));
        
        gameService.saveGame(game);
        return "redirect:/schedule";
    }

    @GetMapping("/edit/{id}")
    public String editGameForm(@PathVariable Long id, Model model) {
        Optional<Game> game = gameService.getGameById(id);
        if (game.isPresent()) {
            model.addAttribute("game", game.get());
            model.addAttribute("teams", teamService.getAllTeams());
            return "schedule/edit";
        }
        return "redirect:/schedule";
    }

    @PostMapping("/edit/{id}")
    public String updateGame(@PathVariable Long id,
                            @RequestParam Long homeTeamId,
                            @RequestParam Long awayTeamId,
                            @RequestParam String dateTime) {
        Team homeTeam = teamService.getTeamById(homeTeamId).orElseThrow();
        Team awayTeam = teamService.getTeamById(awayTeamId).orElseThrow();
        
        Game game = new Game();
        game.setId(id);
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);
        game.setDateTime(LocalDateTime.parse(dateTime));
        
        gameService.saveGame(game);
        return "redirect:/schedule";
    }

    @PostMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/schedule";
    }

    @GetMapping("/result/{gameId}")
    public String resultForm(@PathVariable Long gameId, Model model) {
        Optional<Game> game = gameService.getGameById(gameId);
        if (game.isPresent()) {
            model.addAttribute("game", game.get());
            Optional<GameResult> existingResult = gameResultService.getResultByGameId(gameId);
            existingResult.ifPresent(result -> model.addAttribute("result", result));
            return "schedule/result";
        }
        return "redirect:/schedule";
    }

    @PostMapping("/result/{gameId}")
    public String saveResult(@PathVariable Long gameId,
                            @RequestParam Integer homeScore,
                            @RequestParam Integer awayScore) {
        Game game = gameService.getGameById(gameId).orElseThrow();
        
        Optional<GameResult> existingResult = gameResultService.getResultByGameId(gameId);
        GameResult result;
        if (existingResult.isPresent()) {
            result = existingResult.get();
            result.setHomeScore(homeScore);
            result.setAwayScore(awayScore);
        } else {
            result = new GameResult();
            result.setGame(game);
            result.setHomeScore(homeScore);
            result.setAwayScore(awayScore);
        }
        
        gameResultService.saveResult(result);
        return "redirect:/schedule";
    }
}
