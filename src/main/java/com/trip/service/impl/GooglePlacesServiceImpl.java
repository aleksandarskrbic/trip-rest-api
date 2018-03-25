package com.trip.service.impl;

import com.trip.config.Constants;
import com.trip.service.GooglePlacesService;
import org.springframework.stereotype.Service;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import java.util.List;

@Service
public class GooglePlacesServiceImpl implements GooglePlacesService {

    private GooglePlaces googlePlaces;

    public GooglePlacesServiceImpl() {
        googlePlaces = new GooglePlaces(Constants.API_KEY);
    }

    public boolean checkExistence(String destination) {
        List<Place> places = googlePlaces.getPlacesByQuery(destination, GooglePlaces.MAXIMUM_RESULTS);

        if (places.isEmpty()) {
            return false;
        }

        return true;
    }

}
