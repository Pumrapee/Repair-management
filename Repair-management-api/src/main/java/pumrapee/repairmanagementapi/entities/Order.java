package pumrapee.repairmanagementapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pumrapee.repairmanagementapi.validators.ValidEnum;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @ColumnDefault("'TODO'")
    @Lob
    @ValidEnum(enumClass = Status.class)
    @Column(name = "status", nullable = false)
    private String status = Status.TODO.toString();

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX", timezone="UTC")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX", timezone="UTC")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;


    public String getCreatedBy() {
        return createdBy.getUsername();
    }

}