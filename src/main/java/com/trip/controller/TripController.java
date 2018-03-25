package com.trip.controller;

import com.trip.config.Constants;
import com.trip.exception.TripNotFoundException;
import com.trip.messages.CustomMessage;
import com.trip.model.Trip;
import com.trip.service.GooglePlacesService;
import com.trip.service.TripService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(description = "Trip RESTful API")
@RestController
@RequestMapping(Constants.BASE_URL)
public class TripController {

    private final TripService tripService;

    private final GooglePlacesService googlePlacesService;

    @Autowired
    public TripController(TripService tripService, GooglePlacesService googlePlacesService) {
        this.tripService = tripService;
        this.googlePlacesService = googlePlacesService;
    }

    @ApiOperation(value = "List all the created trips")
    @GetMapping(value = "/all")
    public ResponseEntity<List<Trip>> findAllTrips() {
        List<Trip> trips = tripService.findAll();

        if (trips.isEmpty()) {
            return new ResponseEntity<>(trips, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    @ApiOperation(value = "Find Trip by ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<Trip>> getTripById(@PathVariable final Long id) {
        Optional<Trip> trip = tripService.findById(id);

        if (!trip.isPresent()) {
            return new ResponseEntity<>(Optional.of(
                    new CustomMessage("Trip with id: " + id + " does not exist!")),
                    HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(trip, HttpStatus.OK);
    }

    @ApiOperation(value = "Remove Trip by ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Optional<Trip>> deleteTripById(@PathVariable final Long id) {
        Optional<Trip> trip = tripService.findById(id);

        if (!trip.isPresent()) {
            return new ResponseEntity<>(Optional.of(new CustomMessage(
                    "Trip with id: "  + id + " not found!")), HttpStatus.NOT_FOUND);
        }

        tripService.delete(trip.get());
        return new ResponseEntity<>(Optional.of(new CustomMessage(
                "Deleted Trip with id: " + id + ".")), HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Update existing Trip")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Trip>> updateTripBy(@PathVariable final Long id, @RequestBody final Trip trip) {
        Optional<Trip> found = tripService.findById(id);

        if (!found.isPresent()) {
            return new ResponseEntity<>(Optional.of(new CustomMessage(
                    "Unable to update. Trip with id: "  + id + " not found!")), HttpStatus.NOT_FOUND);
        }

        Trip currentTrip = found.get();
        currentTrip.setDestination(trip.getDestination());
        currentTrip.setStartDate(trip.getStartDate());
        currentTrip.setEndDate(trip.getEndDate());
        currentTrip.setComment(trip.getComment());

        tripService.save(currentTrip);
        return new ResponseEntity<>(Optional.of(currentTrip), HttpStatus.OK);

    }

    @ApiOperation(value = "Find trips by destination")
    @GetMapping(value = "/find/{destination}")
    public ResponseEntity<List<Trip>> findByDestination(@PathVariable String destination) {
        destination = destination.toLowerCase();
        destination = destination.substring(0,1).toUpperCase() + destination.substring(1, destination.length());
        List<Trip> trips = tripService.findByDestination(destination);

        if (trips.isEmpty()) {
            return new ResponseEntity<>(trips, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    @ApiOperation(value = "Create new trip")
    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Trip>> add(@RequestBody final Trip trip) {
        boolean existence = googlePlacesService.checkExistence(trip.getDestination());

        if (!existence) {
            return new ResponseEntity<>(Optional.of(trip), HttpStatus.NOT_ACCEPTABLE);
        } else if (trip.getStartDate().isAfter(trip.getEndDate())) {
            return new ResponseEntity<>(Optional.of(trip), HttpStatus.NOT_ACCEPTABLE);
        }

        String name = trip.getDestination().toLowerCase();
        name = name.substring(0,1).toUpperCase() + name.substring(1, name.length());

        trip.setDestination(name);
        tripService.save(trip);

        return new ResponseEntity<>(tripService.findById(trip.getId()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "List all trips ordered by day count (inc) until StartDate (showing only the future trips)")
    @GetMapping(value = "/list")
    public ResponseEntity<List<Trip>> list() {
        List<Trip> trips = tripService.findAll();

        if (trips.isEmpty()) {
            return new ResponseEntity<>(trips, HttpStatus.NO_CONTENT);
        }

        trips = trips.stream()
                    .sorted(Comparator.comparing(Trip::calculateDayCount))
                    .filter(t -> t.getStartDate().isAfter(LocalDate.now()))
                    .collect(Collectors.toList());

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }
}
