package com.trip.repository;

import com.trip.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TripRepository extends JpaRepository<Trip, Long> {


    List<Trip> findByDestination(String destination);
}
