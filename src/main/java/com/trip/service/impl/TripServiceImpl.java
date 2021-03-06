package com.trip.service.impl;

import com.trip.model.Trip;
import com.trip.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements com.trip.service.TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    @Override
    public void delete(Trip trip) {
        tripRepository.delete(trip);
    }

    @Override
    public void save(Trip trip) {
        tripRepository.saveAndFlush(trip);
    }

    @Override
    public List<Trip> findByDestination(String destination) {
        return tripRepository.findByDestination(destination);
    }

    @Override
    public List<Trip> list() {
        List<Trip> trips = tripRepository.findAll();

        return trips.stream()
                .sorted(Comparator.comparing(Trip::calculateDayCount))
                .filter(t -> t.getStartDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

}
