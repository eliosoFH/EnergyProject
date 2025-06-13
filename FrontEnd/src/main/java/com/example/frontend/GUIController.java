package com.example.frontend;

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

import java.net.URI;

public class GUIController {
    @FXML
    public TextField tfId;
    public Label labelResult;
    public Label currentCommunityUsed;
    public Label currentGridPortion;
    public Button currentRefresh;
    public DatePicker start;
    public DatePicker end;
    public Label historicCommunityProduced;
    public Label historicCommunityUsed;
    public Label historicGridUsed;


    public void onCurrentClick(ActionEvent actionEvent) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        Platform.runLater(() -> labelResult.setText("Current: " + response));
                    });
        } catch (Exception e) {
            e.printStackTrace();
            labelResult.setText("Fehler beim Laden!");
        }
    }

    public void onHistoricalClick(ActionEvent actionEvent) {
        String id = tfId.getText(); // z. B. „2“
        if (id == null || id.isEmpty()) {
            labelResult.setText("Bitte ID eingeben");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/energy/historic/" + id))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        Platform.runLater(() -> labelResult.setText("Historic: " + response));
                    });
        } catch (Exception e) {
            e.printStackTrace();
            labelResult.setText("Fehler beim Laden!");
        }
    }
}