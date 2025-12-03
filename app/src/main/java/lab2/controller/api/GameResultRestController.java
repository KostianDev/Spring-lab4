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
import lab2.dto.GameResultDTO;
import lab2.dto.PagedResponse;
import lab2.exception.BadRequestException;
import lab2.exception.ResourceNotFoundException;
import lab2.model.Game;
import lab2.model.GameResult;
import lab2.service.GameResultService;
import lab2.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@Tag(name = "Game Results", description = "API для управління результатами ігор")
public class GameResultRestController {

    private final GameResultService gameResultService;
    private final GameService gameService;

    public GameResultRestController(GameResultService gameResultService, GameService gameService) {
        this.gameResultService = gameResultService;
        this.gameService = gameService;
    }

    @Operation(
            summary = "Отримати всі результати ігор",
            description = "Повертає список всіх результатів ігор з підтримкою пагінації та фільтрації за ID гри"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успішно отримано список результатів",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<GameResultDTO>> getAllResults(
            @Parameter(description = "Фільтр за ID гри")
            @RequestParam(required = false) Long gameId,
            @Parameter(description = "Номер сторінки (починаючи з 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        List<GameResult> allResults;
        if (gameId != null) {
            allResults = gameResultService.getResultByGameId(gameId)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            allResults = gameResultService.getAllResults();
        }

        // Пагінація
        int totalElements = allResults.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<GameResultDTO> pagedResults = allResults.subList(fromIndex, toIndex).stream()
                .map(this::convertToDTO)
                .toList();

        PagedResponse<GameResultDTO> response = new PagedResponse<>(pagedResults, page, size, totalElements);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Отримати результат за ID",
            description = "Повертає детальну інформацію про результат гри за його унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Результат не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<GameResultDTO> getResultById(
            @Parameter(description = "Унікальний ідентифікатор результату", required = true)
            @PathVariable Long id) {
        GameResult result = gameResultService.getResultById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Результат", "ID", id));
        return ResponseEntity.ok(convertToDTO(result));
    }

    @Operation(
            summary = "Створити новий результат гри",
            description = "Створює новий результат гри в системі та повертає створений об'єкт з присвоєним ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Результат успішно створено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Результат для цієї гри вже існує",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<GameResultDTO> createResult(
            @Parameter(description = "Дані нового результату", required = true)
            @RequestBody GameResultDTO resultDTO) {

        validateResultDTO(resultDTO);

        Game game = gameService.getGameById(resultDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", resultDTO.getGameId()));

        // Перевірка, чи не існує вже результат для цієї гри
        if (gameResultService.getResultByGameId(resultDTO.getGameId()).isPresent()) {
            throw new BadRequestException("Результат для гри з ID " + resultDTO.getGameId() + " вже існує");
        }

        GameResult result = new GameResult();
        result.setGame(game);
        result.setHomeScore(resultDTO.getHomeScore());
        result.setAwayScore(resultDTO.getAwayScore());

        GameResult savedResult = gameResultService.saveResult(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedResult));
    }

    @Operation(
            summary = "Оновити результат повністю",
            description = "Повністю замінює дані результату новими значеннями (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Результат або гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<GameResultDTO> updateResult(
            @Parameter(description = "Унікальний ідентифікатор результату", required = true)
            @PathVariable Long id,
            @Parameter(description = "Нові дані результату", required = true)
            @RequestBody GameResultDTO resultDTO) {

        GameResult existingResult = gameResultService.getResultById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Результат", "ID", id));

        validateResultDTO(resultDTO);

        Game game = gameService.getGameById(resultDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", resultDTO.getGameId()));

        existingResult.setGame(game);
        existingResult.setHomeScore(resultDTO.getHomeScore());
        existingResult.setAwayScore(resultDTO.getAwayScore());

        GameResult updatedResult = gameResultService.saveResult(existingResult);
        return ResponseEntity.ok(convertToDTO(updatedResult));
    }

    @Operation(
            summary = "Часткове оновлення результату (JSON Merge Patch - RFC 7386)",
            description = "Частково оновлює дані результату. Передаються лише ті поля, які потрібно змінити. " +
                    "Відповідає стандарту RFC 7386 (JSON Merge Patch)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат успішно частково оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Результат або гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GameResultDTO> patchResultMergePatch(
            @Parameter(description = "Унікальний ідентифікатор результату", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Merge Patch документ з полями для оновлення", required = true)
            @RequestBody Map<String, Object> patch) {

        GameResult existingResult = gameResultService.getResultById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Результат", "ID", id));

        // Застосування JSON Merge Patch (RFC 7386)
        if (patch.containsKey("gameId")) {
            Object gameIdValue = patch.get("gameId");
            if (gameIdValue != null) {
                Long gameId = Long.valueOf(gameIdValue.toString());
                Game game = gameService.getGameById(gameId)
                        .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", gameId));
                existingResult.setGame(game);
            }
        }

        if (patch.containsKey("homeScore")) {
            Object homeScoreValue = patch.get("homeScore");
            if (homeScoreValue != null) {
                int homeScore = Integer.parseInt(homeScoreValue.toString());
                if (homeScore < 0) {
                    throw new BadRequestException("Рахунок команди господарів не може бути від'ємним");
                }
                existingResult.setHomeScore(homeScore);
            }
        }

        if (patch.containsKey("awayScore")) {
            Object awayScoreValue = patch.get("awayScore");
            if (awayScoreValue != null) {
                int awayScore = Integer.parseInt(awayScoreValue.toString());
                if (awayScore < 0) {
                    throw new BadRequestException("Рахунок команди гостей не може бути від'ємним");
                }
                existingResult.setAwayScore(awayScore);
            }
        }

        GameResult updatedResult = gameResultService.saveResult(existingResult);
        return ResponseEntity.ok(convertToDTO(updatedResult));
    }

    @Operation(
            summary = "Часткове оновлення результату (JSON Patch - RFC 6902)",
            description = "Застосовує набір операцій JSON Patch до результату. " +
                    "Підтримувані операції: add, remove, replace, copy, move, test. " +
                    "Відповідає стандарту RFC 6902"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResultDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректний JSON Patch документ",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Результат або гру не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<GameResultDTO> patchResultJsonPatch(
            @Parameter(description = "Унікальний ідентифікатор результату", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Patch документ (масив операцій)", required = true)
            @RequestBody List<Map<String, Object>> patchOperations) {

        GameResult existingResult = gameResultService.getResultById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Результат", "ID", id));

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
                    applyPatchOperation(existingResult, path, value, op);
                    break;
                case "test":
                    testPatchOperation(existingResult, path, value);
                    break;
                case "remove":
                    throw new BadRequestException("Операція 'remove' не підтримується для обов'язкових полів");
                default:
                    throw new BadRequestException("Непідтримувана операція: " + op);
            }
        }

        GameResult updatedResult = gameResultService.saveResult(existingResult);
        return ResponseEntity.ok(convertToDTO(updatedResult));
    }

    @Operation(
            summary = "Видалити результат",
            description = "Видаляє результат гри з системи за його унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Результат успішно видалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Результат не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(
            @Parameter(description = "Унікальний ідентифікатор результату", required = true)
            @PathVariable Long id) {

        gameResultService.getResultById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Результат", "ID", id));

        gameResultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    private void validateResultDTO(GameResultDTO resultDTO) {
        if (resultDTO.getGameId() == null) {
            throw new BadRequestException("ID гри є обов'язковим");
        }
        if (resultDTO.getHomeScore() == null) {
            throw new BadRequestException("Рахунок команди господарів є обов'язковим");
        }
        if (resultDTO.getAwayScore() == null) {
            throw new BadRequestException("Рахунок команди гостей є обов'язковим");
        }
        if (resultDTO.getHomeScore() < 0) {
            throw new BadRequestException("Рахунок команди господарів не може бути від'ємним");
        }
        if (resultDTO.getAwayScore() < 0) {
            throw new BadRequestException("Рахунок команди гостей не може бути від'ємним");
        }
    }

    private void applyPatchOperation(GameResult result, String path, Object value, String op) {
        if (value == null) {
            throw new BadRequestException("Значення 'value' є обов'язковим для операції " + op);
        }

        switch (path) {
            case "/gameId":
                Long gameId = Long.valueOf(value.toString());
                Game game = gameService.getGameById(gameId)
                        .orElseThrow(() -> new ResourceNotFoundException("Гра", "ID", gameId));
                result.setGame(game);
                break;
            case "/homeScore":
                int homeScore = Integer.parseInt(value.toString());
                if (homeScore < 0) {
                    throw new BadRequestException("Рахунок команди господарів не може бути від'ємним");
                }
                result.setHomeScore(homeScore);
                break;
            case "/awayScore":
                int awayScore = Integer.parseInt(value.toString());
                if (awayScore < 0) {
                    throw new BadRequestException("Рахунок команди гостей не може бути від'ємним");
                }
                result.setAwayScore(awayScore);
                break;
            default:
                throw new BadRequestException("Непідтримуваний шлях: " + path);
        }
    }

    private void testPatchOperation(GameResult result, String path, Object value) {
        switch (path) {
            case "/gameId":
                if (!result.getGame().getId().equals(Long.valueOf(value.toString()))) {
                    throw new BadRequestException("Test operation failed: значення gameId не співпадає");
                }
                break;
            case "/homeScore":
                if (!result.getHomeScore().equals(Integer.parseInt(value.toString()))) {
                    throw new BadRequestException("Test operation failed: значення homeScore не співпадає");
                }
                break;
            case "/awayScore":
                if (!result.getAwayScore().equals(Integer.parseInt(value.toString()))) {
                    throw new BadRequestException("Test operation failed: значення awayScore не співпадає");
                }
                break;
            default:
                throw new BadRequestException("Непідтримуваний шлях для test операції: " + path);
        }
    }

    private GameResultDTO convertToDTO(GameResult result) {
        GameResultDTO dto = new GameResultDTO();
        dto.setId(result.getId());
        dto.setGameId(result.getGame().getId());
        dto.setHomeScore(result.getHomeScore());
        dto.setAwayScore(result.getAwayScore());
        dto.setScoreString(result.getScoreString());

        // Включаємо інформацію про гру
        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(result.getGame().getId());
        gameDTO.setDateTime(result.getGame().getDateTime());
        gameDTO.setHomeTeamId(result.getGame().getHomeTeam().getId());
        gameDTO.setAwayTeamId(result.getGame().getAwayTeam().getId());
        gameDTO.setHomeTeamName(result.getGame().getHomeTeam().getName());
        gameDTO.setAwayTeamName(result.getGame().getAwayTeam().getName());
        dto.setGame(gameDTO);

        return dto;
    }
}
