package lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Відповідь з помилкою")
public class ErrorResponse {
    
    @Schema(description = "Час виникнення помилки", example = "2025-12-03T10:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP статус код", example = "404")
    private int status;
    
    @Schema(description = "Назва помилки", example = "Not Found")
    private String error;
    
    @Schema(description = "Детальне повідомлення про помилку", example = "Команду з ID 999 не знайдено")
    private String message;
    
    @Schema(description = "Шлях запиту", example = "/api/teams/999")
    private String path;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
