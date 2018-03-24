package com.trip.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @Column(nullable = true)
    private String comment;

    private long dayCount;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getDayCount() {
        return dayCount;
    }

    public void setDayCount(long dayCount) {
        this.dayCount = dayCount;
    }
}
