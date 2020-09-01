package de.stea1th.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
@ToString()
public class Order extends AbstractBaseEntity {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime created = LocalDateTime.now();

    @Column(name="completed")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completed;

    @Column(columnDefinition = "person_id")
    private Integer personId;

    @Column(columnDefinition = "pdf")
    private String pdf;
}
