package pumrapee.repairmanagementapi.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {
    private String id;
    private String name;
    private String description;
    private String status;
    private String createdBy;
}
