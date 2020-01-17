package de.stea1th.persist.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order extends AbstractBaseEntity{

    private LocalDateTime created;

    private Boolean completed;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orders;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
    private Person person;
}
