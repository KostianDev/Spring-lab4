package lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object для гри")
public class GameDTO {
    
    @Schema(description = "Унікальний ідентифікатор гри", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Дата та час проведення гри", example = "2025-12-15T18:00:00")
    private LocalDateTime dateTime;
    
    @Schema(description = "ID команди господарів", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long homeTeamId;
    
    @Schema(description = "ID команди гостей", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long awayTeamId;
    
    @Schema(description = "Назва команди господарів", accessMode = Schema.AccessMode.READ_ONLY)
    private String homeTeamName;
    
    @Schema(description = "Назва команди гостей", accessMode = Schema.AccessMode.READ_ONLY)
    private String awayTeamName;

    public GameDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(Long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public Long getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(Long awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }
}
