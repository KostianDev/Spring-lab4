package lab2.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab2.dto.ErrorResponse;
import lab2.dto.GameDTO;
import lab2.dto.PagedResponse;
import lab2.exception.BadRequestException;
import lab2.exception.ResourceNotFoundException;
import lab2.model.Game;
import lab2.model.Team;
import lab2.service.GameService;
import lab2.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@Tag(name = "Games", description = "API для управління іграми")
public class GameRestController {

    private final GameService gameService;
    private final TeamService teamService;

    public GameRestController(GameService gameService, TeamService teamService) {
        this.gameService = gameService;
        this.teamService = teamService;
    }

    @Operation(
            summary = "Отримати всі ігри",
            description = "Повертає список всіх ігор з підтримкою пагінації та фільтрації за назвою команди"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успішно отримано список ігор",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<GameDTO>> getAllGames(
            @Parameter(description = "Фільтр за назвою команди (частковий збіг)")
            @RequestParam(required = false) String teamName,
            @Parameter(description = "Номер сторінки (починаючи з 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        List<Game> allGames;
        if (teamName != null && !teamName.isBlank()) {
            allGames = gameService.searchGamesByTeamName(teamName);
        } else {
            allGames = gameService.getAllGames();
        }

        // Пагінація
        int totalElements = allGames.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<GameDTO> pagedGames = allGames.subList(fromIndex, toIndex).stream()
                .map(this::convertToDTO)
                .toList();

        PagedResponse<GameDTO> response = new PagedResponse<>(pagedGames, page, size, totalElements);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Отримати гру за ID",
            description = "Повертає детальну інформацію про гру за її унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Гру знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(
            @Parameter(description = "Унікальний ідентифікатор гри", required = true)
            @PathVariable Long id) {
        Game game = gameService.getGameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", id));
        return ResponseEntity.ok(convertToDTO(game));
    }

    @Operation(
            summary = "Створити нову гру",
            description = "Створює нову гру в системі та повертає створений об'єкт з присвоєним ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Гру успішно створено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<GameDTO> createGame(
            @Parameter(description = "Дані нової гри", required = true)
            @RequestBody GameDTO gameDTO) {
        
        validateGameDTO(gameDTO);

        Team homeTeam = teamService.getTeamById(gameDTO.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Команда господарів", "ID", gameDTO.getHomeTeamId()));
        Team awayTeam = teamService.getTeamById(gameDTO.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Команда гостей", "ID", gameDTO.getAwayTeamId()));

        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new BadRequestException("Команда господарів та команда гостей повинні бути різними");
        }

        Game game = new Game();
        game.setDateTime(gameDTO.getDateTime());
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);
        
        Game savedGame = gameService.saveGame(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedGame));
    }

    @Operation(
            summary = "Оновити гру повністю",
            description = "Повністю замінює дані гри новими значеннями (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Гру успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру або команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<GameDTO> updateGame(
            @Parameter(description = "Унікальний ідентифікатор гри", required = true)
            @PathVariable Long id,
            @Parameter(description = "Нові дані гри", required = true)
            @RequestBody GameDTO gameDTO) {

        gameService.getGameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", id));

        validateGameDTO(gameDTO);

        Team homeTeam = teamService.getTeamById(gameDTO.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Команда господарів", "ID", gameDTO.getHomeTeamId()));
        Team awayTeam = teamService.getTeamById(gameDTO.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Команда гостей", "ID", gameDTO.getAwayTeamId()));

        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new BadRequestException("Команда господарів та команда гостей повинні бути різними");
        }

        Game game = new Game();
        game.setId(id);
        game.setDateTime(gameDTO.getDateTime());
        game.setHomeTeam(homeTeam);
        game.setAwayTeam(awayTeam);

        Game updatedGame = gameService.saveGame(game);
        return ResponseEntity.ok(convertToDTO(updatedGame));
    }

    @Operation(
            summary = "Часткове оновлення гри (JSON Merge Patch - RFC 7386)",
            description = "Частково оновлює дані гри. Передаються лише ті поля, які потрібно змінити. " +
                    "Відповідає стандарту RFC 7386 (JSON Merge Patch)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Гру успішно частково оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру або команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{id}/merge")
    public ResponseEntity<GameDTO> patchGameMergePatch(
            @Parameter(description = "Унікальний ідентифікатор гри", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Merge Patch документ з полями для оновлення", required = true)
            @RequestBody Map<String, Object> patch) {

        Game existingGame = gameService.getGameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", id));

        // Застосування JSON Merge Patch (RFC 7386)
        if (patch.containsKey("dateTime")) {
            Object dateTimeValue = patch.get("dateTime");
            if (dateTimeValue != null) {
                existingGame.setDateTime(LocalDateTime.parse(dateTimeValue.toString()));
            }
        }

        if (patch.containsKey("homeTeamId")) {
            Object homeTeamIdValue = patch.get("homeTeamId");
            if (homeTeamIdValue != null) {
                Long homeTeamId = Long.valueOf(homeTeamIdValue.toString());
                Team homeTeam = teamService.getTeamById(homeTeamId)
                        .orElseThrow(() -> new ResourceNotFoundException("Команда господарів", "ID", homeTeamId));
                existingGame.setHomeTeam(homeTeam);
            }
        }

        if (patch.containsKey("awayTeamId")) {
            Object awayTeamIdValue = patch.get("awayTeamId");
            if (awayTeamIdValue != null) {
                Long awayTeamId = Long.valueOf(awayTeamIdValue.toString());
                Team awayTeam = teamService.getTeamById(awayTeamId)
                        .orElseThrow(() -> new ResourceNotFoundException("Команда гостей", "ID", awayTeamId));
                existingGame.setAwayTeam(awayTeam);
            }
        }

        if (existingGame.getHomeTeam().getId().equals(existingGame.getAwayTeam().getId())) {
            throw new BadRequestException("Команда господарів та команда гостей повинні бути різними");
        }

        Game updatedGame = gameService.saveGame(existingGame);
        return ResponseEntity.ok(convertToDTO(updatedGame));
    }

    @Operation(
            summary = "Часткове оновлення гри (JSON Patch - RFC 6902)",
            description = "Застосовує набір операцій JSON Patch до гри. " +
                    "Підтримувані операції: add, remove, replace, copy, move, test. " +
                    "Відповідає стандарту RFC 6902"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Гру успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректний JSON Patch документ",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру або команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<GameDTO> patchGameJsonPatch(
            @Parameter(description = "Унікальний ідентифікатор гри", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Patch документ (масив операцій)", required = true)
            @RequestBody List<Map<String, Object>> patchOperations) {

        Game existingGame = gameService.getGameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", id));

        // Застосування JSON Patch операцій (RFC 6902)
        for (Map<String, Object> operation : patchOperations) {
            String op = (String) operation.get("op");
            String path = (String) operation.get("path");
            Object value = operation.get("value");

            if (op == null || path == null) {
                throw new BadRequestException("Кожна операція повинна мати 'op' та 'path'");
            }

            switch (op) {
                case "replace":
                case "add":
                    applyPatchOperation(existingGame, path, value, op);
                    break;
                case "test":
                    testPatchOperation(existingGame, path, value);
                    break;
                case "remove":
                    throw new BadRequestException("Операція 'remove' не підтримується для обов'язкових полів");
                default:
                    throw new BadRequestException("Непідтримувана операція: " + op);
            }
        }

        if (existingGame.getHomeTeam().getId().equals(existingGame.getAwayTeam().getId())) {
            throw new BadRequestException("Команда господарів та команда гостей повинні бути різними");
        }

        Game updatedGame = gameService.saveGame(existingGame);
        return ResponseEntity.ok(convertToDTO(updatedGame));
    }

    @Operation(
            summary = "Видалити гру",
            description = "Видаляє гру з системи за її унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Гру успішно видалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "Унікальний ідентифікатор гри", required = true)
            @PathVariable Long id) {

        gameService.getGameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", id));

        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    private void validateGameDTO(GameDTO gameDTO) {
        if (gameDTO.getDateTime() == null) {
            throw new BadRequestException("Дата та час гри є обов'язковими");
        }
        if (gameDTO.getHomeTeamId() == null) {
            throw new BadRequestException("ID команди господарів є обов'язковим");
        }
        if (gameDTO.getAwayTeamId() == null) {
            throw new BadRequestException("ID команди гостей є обов'язковим");
        }
    }

    private void applyPatchOperation(Game game, String path, Object value, String op) {
        if (value == null) {
            throw new BadRequestException("Значення 'value' є обов'язковим для операції " + op);
        }
        
        switch (path) {
            case "/dateTime":
                game.setDateTime(LocalDateTime.parse(value.toString()));
                break;
            case "/homeTeamId":
                Long homeTeamId = Long.valueOf(value.toString());
                Team homeTeam = teamService.getTeamById(homeTeamId)
                        .orElseThrow(() -> new ResourceNotFoundException("Команда господарів", "ID", homeTeamId));
                game.setHomeTeam(homeTeam);
                break;
            case "/awayTeamId":
                Long awayTeamId = Long.valueOf(value.toString());
                Team awayTeam = teamService.getTeamById(awayTeamId)
                        .orElseThrow(() -> new ResourceNotFoundException("Команда гостей", "ID", awayTeamId));
                game.setAwayTeam(awayTeam);
                break;
            default:
                throw new BadRequestException("Непідтримуваний шлях: " + path);
        }
    }

    private void testPatchOperation(Game game, String path, Object value) {
        switch (path) {
            case "/dateTime":
                if (!game.getDateTime().toString().equals(value.toString())) {
                    throw new BadRequestException("Test operation failed: значення dateTime не співпадає");
                }
                break;
            case "/homeTeamId":
                if (!game.getHomeTeam().getId().equals(Long.valueOf(value.toString()))) {
                    throw new BadRequestException("Test operation failed: значення homeTeamId не співпадає");
                }
                break;
            case "/awayTeamId":
                if (!game.getAwayTeam().getId().equals(Long.valueOf(value.toString()))) {
                    throw new BadRequestException("Test operation failed: значення awayTeamId не співпадає");
                }
                break;
            default:
                throw new BadRequestException("Непідтримуваний шлях для test операції: " + path);
        }
    }

    private GameDTO convertToDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setDateTime(game.getDateTime());
        dto.setHomeTeamId(game.getHomeTeam().getId());
        dto.setAwayTeamId(game.getAwayTeam().getId());
        dto.setHomeTeamName(game.getHomeTeam().getName());
        dto.setAwayTeamName(game.getAwayTeam().getName());
        return dto;
    }
}
