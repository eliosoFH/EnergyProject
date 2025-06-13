package com.example.frontend;

import com.example.frontend.dto.Energy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.application.Platform;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class GUIController {
    @FXML
    public Label currentCommunityUsed;
    public Label currentGridPortion;
    public Button currentRefresh;
    public DatePicker start;
    public DatePicker end;
    public Label historicCommunityProduced;
    public Label historicCommunityUsed;
    public Label historicGridUsed;


    public void getCurrent(ActionEvent actionEvent) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            HttpResponse<String> response;
            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            Energy daten = om.readValue(response.body(), Energy.class);

            currentCommunityUsed.setText(String.valueOf(daten.getCommunityUsed()));
            currentGridPortion.setText((String.valueOf(daten.getGridUsed())));

        } catch (Exception e) {
            e.printStackTrace();
            currentCommunityUsed.setText("Fehler beim Laden!");
            currentGridPortion.setText("Fehler beim Laden!");
        }
    }

    public void getHistorical(ActionEvent actionEvent) {
    }


}