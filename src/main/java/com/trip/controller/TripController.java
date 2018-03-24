package com.trip.controller;

import com.trip.config.Constants;
import com.trip.model.Trip;
import com.trip.repository.TripRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trip")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @ApiOperation(value = "List all the created trips")
    @GetMapping(value = "/all")
    public ResponseEntity<List<Trip>> findAllTrips() {
        List<Trip> trips = tripRepository.findAll();

        if (trips.isEmpty()) {
            return new ResponseEntity<>(trips, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    @ApiOperation(value = "Find trips by destination")
    @GetMapping(value = "/find/{destination}")
    public ResponseEntity<List<Trip>> findByDestination(@PathVariable String destination) {
        destination = destination.toLowerCase();
        destination = destination.substring(0,1).toUpperCase() + destination.substring(1, destination.length());
        List<Trip> trips = tripRepository.findByDestination(destination);
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    @ApiOperation(value = "Create new trip")
    @PostMapping(value = "/add")
    public ResponseEntity<Optional<Trip>> add(@RequestBody final Trip trip) {
        GooglePlaces client = new GooglePlaces(Constants.API_KEY);
        List<Place> places = client.getPlacesByQuery(trip.getDestination(), GooglePlaces.MAXIMUM_RESULTS);

        if (places.isEmpty()) {
            return new ResponseEntity<>(Optional.of(trip), HttpStatus.NOT_ACCEPTABLE);
        } else if (trip.getStartDate().isAfter(trip.getEndDate())) {
            return new ResponseEntity<>(Optional.of(trip), HttpStatus.NOT_ACCEPTABLE);
        }

        String name = trip.getDestination().toLowerCase();
        name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());

        trip.setDestination(name);
        tripRepository.save(trip);

        return new ResponseEntity<>(tripRepository.findById(trip.getId()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "List all trips ordered by day count (inc) until StartDate (showing only the future trips)")
    @GetMapping(value = "/list")
    public ResponseEntity<List<Trip>> list() {
        List<Trip> trips = tripRepository.findAll();

        if (trips.isEmpty()) {
            return new ResponseEntity<>(trips, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        trips = trips.stream()
                    .sorted(Comparator.comparing(Trip::calculateDayCount))
                    .filter(t -> t.getStartDate().isAfter(LocalDate.now()))
                    .collect(Collectors.toList());

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }
}