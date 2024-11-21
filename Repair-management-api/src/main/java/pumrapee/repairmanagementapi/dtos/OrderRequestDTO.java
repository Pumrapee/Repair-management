package pumrapee.repairmanagementapi.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import pumrapee.repairmanagementapi.entities.Status;
import pumrapee.repairmanagementapi.validators.ValidEnum;

@Getter
@Setter
public class OrderRequestDTO {
    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Lob
    @Column(name = "description")
    private String description;
    @ColumnDefault("'TODO'")
    @Lob
    @ValidEnum(enumClass = Status.class, message = "Status must be either 'TODO' or 'DONE'.")
    private String status = Status.TODO.toString();
}
