package com.trip.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.print.attribute.standard.MediaSize;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRIP_ID")
    private Long id;

    @ApiModelProperty(value = "Destination name", required = true)
    @NotEmpty
    @Length(max = 80)
    @Column(name = "DESTINATION")
    private String destination;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @ApiModelProperty(value = "Start Date of Trip", required = true)
    @Column(name = "START_DATE")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @ApiModelProperty(value = "End Date of Trip", required = true)
    @Column(name = "END_DATE")
    private LocalDate endDate;

    @ApiModelProperty(value = "Trip comment")
    @Length(max = 150)
    @Column(name = "COMMENT")
    private String comment;

    public Trip() {
    }

    public Trip(Long id, String destination, LocalDate startDate, LocalDate endDate, String comment) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
    }

    public Trip(Long id, String destination, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long calculateDayCount() {
        return DAYS.between(this.getStartDate(), this.getEndDate());
    }
}
