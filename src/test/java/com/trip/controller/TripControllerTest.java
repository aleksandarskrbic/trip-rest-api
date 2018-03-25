package com.trip.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.trip.config.Endpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import sun.misc.ASCIICaseInsensitiveComparator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class TripControllerTest {

    @Test
    public void test1_listAllTrips() {
        Response response = RestAssured.get(Endpoints.LIST_ALL_TRIPS);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(response.asString());

        // In initional database there is 6 Trips
        Assert.assertEquals(6, array.length());
    }

    @Test
    public void test2_getTripById() {
        Response response = RestAssured.get(Endpoints.BASE_URL + "/1");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());

        JSONObject trip = new JSONObject(response.asString());
        Assert.assertEquals(1, trip.getInt("id"));
        Assert.assertEquals("Maldives", trip.getString("destination"));
    }

    @Test
    public void test3_findByDestination() {
        Response response = RestAssured.get(Endpoints.FIND_BY_DESTNATION + "/maldives");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(response.asString());

        // In initial database there is 2 Trips with Maldives destination
        Assert.assertEquals(2, array.length());

        // Iterating through response to check if all destinations are equal to Maldives
        for (int i = 0; i < array.length(); ++i) {
            JSONObject tripJSON = array.getJSONObject(i);
            Assert.assertEquals("Maldives", tripJSON.getString("destination"));
        }
    }

    @Test
    public void test4_list() {
        Response response = RestAssured.get(Endpoints.LIST_SORTED);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());

        JSONArray array = new JSONArray(response.asString());

        // In initial database there is 4 trips with futur Trips
        System.out.println(array);
        Assert.assertEquals(4, array.length());
    }

    @Test
    public void test5_add() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("destination", "Belgrade");
        requestParams.put("startDate", "04-09-2018");
        requestParams.put("endDate", "03-10-2018");
        requestParams.put("comment", "No comment");

        RestAssured.baseURI = Endpoints.BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.post("/add");
        JSONObject tripJSON = new JSONObject(response.asString());

        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        Assert.assertEquals("Belgrade", tripJSON.getString("destination"));
    }

    @Test
    public void test6_delete() {
        RequestSpecification request = RestAssured.given();
        Response response = request.delete(Endpoints.BASE_URL + "/1");

        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCode());
    }

    @Test
    public void test7_update() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("destination", "Belgrade");
        requestParams.put("startDate", "04-09-2018");
        requestParams.put("endDate", "03-10-2018");
        requestParams.put("commnent", "No comment");

        RestAssured.baseURI = Endpoints.BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestParams.toString());

        Response response = request.put("/4");
        JSONObject tripJSON = new JSONObject(response.asString());

        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals("Belgrade", tripJSON.getString("destination"));
    }
}
