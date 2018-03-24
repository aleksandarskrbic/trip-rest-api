package com.trip.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.trip.config.Endpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import sun.misc.ASCIICaseInsensitiveComparator;

public class TripControllerTest {

    @Test
    public void listAllTrips() {
        Response resp = RestAssured.get(Endpoints.LIST_ALL_TRIPS);
        Assert.assertEquals(resp.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(resp.asString());

        // In initional database there is 6 Trips
        Assert.assertEquals(6, array.length());
    }

    @Test
    public void findByDestination() {
        Response resp = RestAssured.get(Endpoints.FIND_BY_DESTNATION + "maldives");
        Assert.assertEquals(resp.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(resp.asString());

        // In initial database there is 2 Trips with Maldives destination
        Assert.assertEquals(2, array.length());

        // Iterating through response to check if all destinations are equal to Maldives
        for (int i = 0; i < array.length(); ++i) {
            JSONObject tripJSON = array.getJSONObject(i);
            Assert.assertEquals("Maldives", tripJSON.getString("destination"));
        }
    }

    @Test
    public void add() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("destination", "Belgrade");
        requestParams.put("startDate", "04-09-2018");
        requestParams.put("endDate", "03-10-2018");
        requestParams.put("commnet", "No comment");

        RestAssured.baseURI = Endpoints.ADD_TRIP;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("/add");
        JSONObject tripJSON = new JSONObject(response.asString());

        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        Assert.assertEquals("Belgrade", tripJSON.getString("destination"));
    }


    @Test
    public void list() {
        Response resp = RestAssured.get(Endpoints.LIST_SORTED);
        Assert.assertEquals(resp.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(resp.asString());

        // In initial database there is 4 trips with futur Trips
        Assert.assertEquals(4, array.length());
    }
}
