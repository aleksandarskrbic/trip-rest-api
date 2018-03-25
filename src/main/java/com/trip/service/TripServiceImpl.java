package com.trip.service;

import com.trip.model.Trip;
import com.trip.repository.TripService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class TripServiceImpl implements com.trip.service.TripService {

    private final TripService tripService;

    @Autowired
    public TripServiceImpl(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public List<Trip> findAll() {
        return tripService.findAll();
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return tripService.findById(id);
    }

    @Override
    public void delete(Trip trip) {
        tripService.delete(trip);
    }

    @Override
    public void save(Trip trip) {
        tripService.saveAndFlush(trip);
    }
}
