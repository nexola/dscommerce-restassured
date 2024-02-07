package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private Long existingOrderId, nonExistingOrderId, dependentOrderId;
    private String clientUsername, clientPassword, adminUsername, adminPassword, clientToken, adminToken, invalidToken;

    private Map<String, Object> postOrderInstance;

    @BeforeEach
    public void setUp() throws Exception {
        baseURI = "http://localhost:8080";;
        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto"; // invalid token
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExistsAndAdminLogged() {
        existingOrderId = 1L;

        given().header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("client.name", equalTo("Maria Brown"))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {
        nonExistingOrderId = 10L;

        given().header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() {
        nonExistingOrderId = 10L;

        given().header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExistsAndClientLoggedAndOwnTheOrder() {
        existingOrderId = 1L;

        given().header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("client.name", equalTo("Maria Brown"))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenNoUserLogged() {
        existingOrderId = 1L;

        given().header("Authorization", "Bearer " + invalidToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenClientLoggedAndDoNotOwnOrder() {
        existingOrderId = 2L;

        given().header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(403);
    }
}
