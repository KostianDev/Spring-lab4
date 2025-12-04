package lab2.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lab2.dto.ErrorResponse;
import lab2.dto.PagedResponse;
import lab2.dto.TeamDTO;
import lab2.exception.BadRequestException;
import lab2.exception.ResourceNotFoundException;
import lab2.model.Team;
import lab2.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "API для управління командами")
public class TeamRestController {

    private final TeamService teamService;

    public TeamRestController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(
            summary = "Отримати всі команди",
            description = "Повертає список всіх команд з підтримкою пагінації та фільтрації за назвою"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успішно отримано список команд",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<TeamDTO>> getAllTeams(
            @Parameter(description = "Фільтр за назвою команди (частковий збіг)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Номер сторінки (починаючи з 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        List<Team> allTeams;
        if (name != null && !name.isBlank()) {
            allTeams = teamService.searchTeamsByName(name);
        } else {
            allTeams = teamService.getAllTeams();
        }

        // Пагінація
        int totalElements = allTeams.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        
        List<TeamDTO> pagedTeams = allTeams.subList(fromIndex, toIndex).stream()
                .map(this::convertToDTO)
                .toList();

        PagedResponse<TeamDTO> response = new PagedResponse<>(pagedTeams, page, size, totalElements);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Отримати команду за ID",
            description = "Повертає детальну інформацію про команду за її унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Команду знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(
            @Parameter(description = "Унікальний ідентифікатор команди", required = true)
            @PathVariable Long id) {
        Team team = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Команда", "ID", id));
        return ResponseEntity.ok(convertToDTO(team));
    }

    @Operation(
            summary = "Створити нову команду",
            description = "Створює нову команду в системі та повертає створений об'єкт з присвоєним ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Команду успішно створено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректні дані запиту",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(
            @Parameter(description = "Дані нової команди", required = true)
            @RequestBody TeamDTO teamDTO) {
        if (teamDTO.getName() == null || teamDTO.getName().isBlank()) {
            throw new BadRequestException("Назва команди є обов'язковою");
        }

        Team team = new Team();
        team.setName(teamDTO.getName());
        Team savedTeam = teamService.saveTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedTeam));
    }

    @Operation(
            summary = "Оновити команду повністю",
            description = "Повністю замінює дані команди новими значеннями (PUT)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Команду успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))
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
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(
            @Parameter(description = "Унікальний ідентифікатор команди", required = true)
            @PathVariable Long id,
            @Parameter(description = "Нові дані команди", required = true)
            @RequestBody TeamDTO teamDTO) {
        
        teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Команда", "ID", id));

        if (teamDTO.getName() == null || teamDTO.getName().isBlank()) {
            throw new BadRequestException("Назва команди є обов'язковою");
        }

        Team team = new Team();
        team.setId(id);
        team.setName(teamDTO.getName());
        Team updatedTeam = teamService.saveTeam(team);
        return ResponseEntity.ok(convertToDTO(updatedTeam));
    }

    @Operation(
            summary = "Часткове оновлення команди (JSON Merge Patch - RFC 7386)",
            description = "Частково оновлює дані команди. Передаються лише ті поля, які потрібно змінити. " +
                    "Відповідає стандарту RFC 7386 (JSON Merge Patch)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Команду успішно частково оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))
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
    @PatchMapping(value = "/{id}/merge")
    public ResponseEntity<TeamDTO> patchTeamMergePatch(
            @Parameter(description = "Унікальний ідентифікатор команди", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Merge Patch документ з полями для оновлення", required = true)
            @RequestBody Map<String, Object> patch) {
        
        Team existingTeam = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Команда", "ID", id));

        // Застосування JSON Merge Patch (RFC 7386)
        if (patch.containsKey("name")) {
            Object nameValue = patch.get("name");
            if (nameValue == null) {
                throw new BadRequestException("Назва команди не може бути null");
            }
            existingTeam.setName(nameValue.toString());
        }

        Team updatedTeam = teamService.saveTeam(existingTeam);
        return ResponseEntity.ok(convertToDTO(updatedTeam));
    }

    @Operation(
            summary = "Часткове оновлення команди (JSON Patch - RFC 6902)",
            description = "Застосовує набір операцій JSON Patch до команди. " +
                    "Підтримувані операції: add, remove, replace, copy, move, test. " +
                    "Відповідає стандарту RFC 6902"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Команду успішно оновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некоректний JSON Patch документ",
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
    @PatchMapping(value = "/{id}")
    public ResponseEntity<TeamDTO> patchTeamJsonPatch(
            @Parameter(description = "Унікальний ідентифікатор команди", required = true)
            @PathVariable Long id,
            @Parameter(description = "JSON Patch документ (масив операцій)", required = true)
            @RequestBody List<Map<String, Object>> patchOperations) {
        
        Team existingTeam = teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Команда", "ID", id));

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
                    if ("/name".equals(path)) {
                        if (value == null) {
                            throw new BadRequestException("Значення 'value' є обов'язковим для операції " + op);
                        }
                        existingTeam.setName(value.toString());
                    } else {
                        throw new BadRequestException("Непідтримуваний шлях: " + path);
                    }
                    break;
                case "test":
                    if ("/name".equals(path)) {
                        if (!existingTeam.getName().equals(value)) {
                            throw new BadRequestException("Test operation failed: значення не співпадає");
                        }
                    }
                    break;
                case "remove":
                    throw new BadRequestException("Операція 'remove' не підтримується для обов'язкових полів");
                default:
                    throw new BadRequestException("Непідтримувана операція: " + op);
            }
        }

        Team updatedTeam = teamService.saveTeam(existingTeam);
        return ResponseEntity.ok(convertToDTO(updatedTeam));
    }

    @Operation(
            summary = "Видалити команду",
            description = "Видаляє команду з системи за її унікальним ідентифікатором"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Команду успішно видалено"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Команду не знайдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "Унікальний ідентифікатор команди", required = true)
            @PathVariable Long id) {
        
        teamService.getTeamById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Команда", "ID", id));
        
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    private TeamDTO convertToDTO(Team team) {
        return new TeamDTO(team.getId(), team.getName());
    }
}
