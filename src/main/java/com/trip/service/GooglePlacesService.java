package com.trip.service;

import com.trip.config.Constants;
import org.springframework.stereotype.Service;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import java.util.List;

@Service
public class GooglePlacesService {

    private GooglePlaces googlePlaces;

    public GooglePlacesService() {
        googlePlaces = new GooglePlaces(Constants.API_KEY);
    }

    public GooglePlaces getGooglePlaces() {
        return googlePlaces;
    }

    public boolean checkExistence(String destination) {
        List<Place> places = googlePlaces.getPlacesByQuery(destination, GooglePlaces.MAXIMUM_RESULTS);

        if (places.isEmpty()) {
            return false;
        }

        return true;
    }

}
