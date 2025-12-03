package lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object для результату гри")
public class GameResultDTO {
    
    @Schema(description = "Унікальний ідентифікатор результату", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "ID гри", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long gameId;
    
    @Schema(description = "Рахунок команди господарів", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer homeScore;
    
    @Schema(description = "Рахунок команди гостей", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer awayScore;
    
    @Schema(description = "Рядок з рахунком у форматі 'X : Y'", example = "2 : 1", accessMode = Schema.AccessMode.READ_ONLY)
    private String scoreString;
    
    @Schema(description = "Інформація про гру", accessMode = Schema.AccessMode.READ_ONLY)
    private GameDTO game;

    public GameResultDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public String getScoreString() {
        return scoreString;
    }

    public void setScoreString(String scoreString) {
        this.scoreString = scoreString;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }
}
