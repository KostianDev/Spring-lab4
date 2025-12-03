package lab2.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object для команди")
public class TeamDTO {
    
    @Schema(description = "Унікальний ідентифікатор команди", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Назва команди", example = "Динамо Київ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public TeamDTO() {
    }

    public TeamDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
