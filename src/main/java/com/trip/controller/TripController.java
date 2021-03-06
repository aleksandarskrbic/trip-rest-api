package com.trip.controller;

import com.trip.config.Constants;
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
import se.walkercrou.places.exception.NoResultsFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    public ResponseEntity<Optional<Trip>> updateTripBy(@PathVariable final Long id,
                                                       @Valid @RequestBody final Trip trip) {
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
    @GetMapping(value = {"/find/{destination}", "/find/{destOne}/{destTwo}"})
    public ResponseEntity<List<Trip>> findByDestination(@PathVariable Optional<String> destination,
                                                        @PathVariable Optional<String> destOne,
                                                        @PathVariable Optional<String> destTwo) {
        String search = "";

        if (destination.isPresent()) {
            search = destinationFormatter(destination.get());
        } else if(destOne.isPresent() && destTwo.isPresent()) {
            search = destOne.get() + " " + destTwo.get();
            search = destinationFormatter(search);
        }

        List<Trip> trips = tripService.findByDestination(search);

        if (trips.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    @ApiOperation(value = "Create new trip")
    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Trip>> add(@Valid @RequestBody final Trip trip) {
        boolean existence;

        try {
            existence = googlePlacesService.checkExistence(trip.getDestination());
        } catch (NoResultsFoundException ex) {
            return new ResponseEntity<>(Optional.of(new CustomMessage(
                    "Destination \'" + trip.getDestination() + "'/ does not exist!")),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (trip.getStartDate().isAfter(trip.getEndDate())) {
            return new ResponseEntity<>(Optional.of(new CustomMessage(
                    "Not valid dates!"))
                    , HttpStatus.NOT_ACCEPTABLE);
        }

        String name = destinationFormatter(trip.getDestination());

        trip.setDestination(name);
        tripService.save(trip);

        return new ResponseEntity<>(tripService.findById(trip.getId()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "List all trips ordered by day count (inc) until StartDate (showing only the future trips)")
    @GetMapping(value = "/list")
    public ResponseEntity<List<Trip>> list() {
        List<Trip> trips = tripService.list();

        if (trips.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(trips, HttpStatus.OK);
    }

    private String destinationFormatter(String destination) {
        destination = destination.trim();

        if (destination.contains("_")) {
            String[] split = destination.split("_");
            split[0] = split[0].toLowerCase();
            split[0] = split[0].substring(0, 1).toUpperCase() + split[0].substring(1, split[0].length());

            split[1] = split[1].toLowerCase();
            split[1] = split[1].substring(0, 1).toUpperCase() + split[1].substring(1, split[1].length());

            destination = split[0] + " " + split[1];
        } else if (destination.contains(" ")){
            String[] split = destination.split(" ");
            split[0] = split[0].toLowerCase();
            split[0] = split[0].substring(0, 1).toUpperCase() + split[0].substring(1, split[0].length());

            split[1] = split[1].toLowerCase();
            split[1] = split[1].substring(0, 1).toUpperCase() + split[1].substring(1, split[1].length());

            destination = split[0] + " " + split[1];
        } else {
            destination = destination.toLowerCase();
            destination = destination.substring(0,1).toUpperCase() + destination.substring(1, destination.length());
        }

        return destination;
    }
}
