package com.trip.service;

import com.trip.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {

    List<Trip> findAll();

    Optional<Trip> findById(Long id);

    void delete(Trip trip);

    void save(Trip trip);

    List<Trip> findByDestination(String destination);

    List<Trip> list();
}
