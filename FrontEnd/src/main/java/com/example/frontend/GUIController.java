package com.example.frontend;

import com.example.frontend.dto.CurrentPercentage;
import com.example.frontend.dto.EnergyUsageHourly;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class GUIController {
    @FXML
    public Label currentCommunityDepleted;
    public Label currentGridPortion;
    public Button currentRefresh;
    public DatePicker start;
    public DatePicker end;
    public Label historicCommunityProduced;
    public Label historicCommunityUsed;
    public Label historicGridUsed;
    public Spinner<Integer> endHour;
    public Spinner<Integer> startHour;
    public Button historicalRefresh;

    public void initialize() {
        startHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        endHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 23));
        start.setValue(LocalDate.now());
        end.setValue(LocalDate.now());
    }


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

            System.out.println(response);

            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            CurrentPercentage data = om.readValue(response.body(), CurrentPercentage.class);

            currentCommunityDepleted.setText(String.format("%.2f", data.getCommunityDepleted()) + " %");
            currentGridPortion.setText(String.format("%.2f", data.getGridPortion()) + " %");

        } catch (Exception e) {
            e.printStackTrace();
            currentCommunityDepleted.setText("Fehler beim Laden!");
            currentGridPortion.setText("Fehler beim Laden!");
        }
    }

    public void getHistorical(ActionEvent actionEvent) {
        try {
            LocalDateTime startDateTime = start.getValue().atTime(startHour.getValue(), 0);
            LocalDateTime endDateTime = end.getValue().atTime(endHour.getValue(), 0);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/historic?start=" + startDateTime + "&end=" + endDateTime))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println(response);

            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<EnergyUsageHourly> datenListe = om.readValue(response.body(), new TypeReference<List<EnergyUsageHourly>>() {});

            double sumProduced = 0;
            double sumUsed = 0;
            double sumGrid = 0;

            for (EnergyUsageHourly e : datenListe) {
                sumProduced += e.getCommunityProduced();
                sumUsed += e.getCommunityUsed();
                sumGrid += e.getGridUsed();
            }

            historicCommunityProduced.setText(String.format("%.2f kWh", sumProduced));
            historicCommunityUsed.setText(String.format("%.2f kWh", sumUsed));
            historicGridUsed.setText(String.format("%.2f kWh", sumGrid));

        } catch (Exception e) {
            e.printStackTrace();
            historicCommunityProduced.setText("Fehler beim Laden!");
            historicCommunityUsed.setText("Fehler beim Laden!");
            historicGridUsed.setText("Fehler beim Laden!");
        }
    }


}