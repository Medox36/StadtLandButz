package com.example.stadtlandbutzserver.gui;

import com.example.stadtlandbutzserver.game.Game;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class ServerGUI extends Application {

    private Stage stage;
    private final ArrayList<String> categories = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setMinWidth(200);
        this.stage.setMinHeight(300);
        selectionStage();
    }

    private void selectionStage() {
        ListView<String> categoriesList = new ListView<>();
        categoriesList.setEditable(false);
        categoriesList.getItems().add("");

        CheckBox[] checkBoxes = new CheckBox[20];
        checkBoxes[0] = new CheckBox("Stadt");
        checkBoxes[1] = new CheckBox("Land");
        checkBoxes[2] = new CheckBox("Gewässer");
        checkBoxes[3] = new CheckBox("Gebirge");
        checkBoxes[4] = new CheckBox("Name");
        checkBoxes[5] = new CheckBox("Tier");
        checkBoxes[6] = new CheckBox("Beruf");
        checkBoxes[7] = new CheckBox("Hauptstadt");
        checkBoxes[8] = new CheckBox("Promi");
        checkBoxes[9] = new CheckBox("Modemarke");
        checkBoxes[10] = new CheckBox("Automarke");
        checkBoxes[11] = new CheckBox("Sporler");
        checkBoxes[12] = new CheckBox("Sportart");
        checkBoxes[13] = new CheckBox("Hobby");
        checkBoxes[14] = new CheckBox("Nahrungsmittel");
        checkBoxes[15] = new CheckBox("Getränk");
        checkBoxes[16] = new CheckBox("Essen");
        checkBoxes[17] = new CheckBox("Sprache");
        checkBoxes[18] = new CheckBox("Autor");
        checkBoxes[19] = new CheckBox("Schauspieler");
        for (CheckBox box : checkBoxes) {
            box.setOnAction(e -> {
                if (box.isSelected()) {
                    categoriesList.getItems().add(box.getText());
                } else {
                    categoriesList.getItems().remove(box.getText());
                }
                categoriesListEmptyTest(categoriesList);
            });
        }

        categoriesList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                String currentItemSelected = categoriesList.getSelectionModel().getSelectedItem();
                categoriesList.getItems().remove(currentItemSelected);
                for (CheckBox box : checkBoxes) {
                    if (Objects.equals(box.getText(), currentItemSelected)) {
                        box.setSelected(false);
                    }
                }
                categoriesListEmptyTest(categoriesList);
            }
        });

        TextField additionalCategory = new TextField();
        additionalCategory.setPromptText("neue Kategorie");

        Button add = new Button("Hinzufügen");
        add.setOnAction(e -> {
            containsAdd(categoriesList, additionalCategory.getText(), checkBoxes);
            additionalCategory.setText("");
        });

        VBox checkBoxesL = new VBox(15);
        VBox checkBoxesR = new VBox(15);

        for (int i = 0; i < 11; i++) {
            checkBoxesL.getChildren().add(checkBoxes[i]);
        }
        for (int i = 11; i < 20; i++) {
            checkBoxesR.getChildren().add(checkBoxes[i]);
        }

        HBox checkBoxAll = new HBox(10, checkBoxesL, checkBoxesR);
        VBox additional = new VBox(5, additionalCategory, add);
        VBox checkBoxBox = new VBox(15,checkBoxAll, additional);
        HBox category = new HBox(5,categoriesList, checkBoxBox);
        category.setLayoutX(10);
        category.setLayoutY(10);

        Label topText = new Label("Kategorien auswählen");
        topText.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-font-style: italic");
        HBox topTextBox = new HBox(topText);

        Button startButton = new Button("Bestätigen");
        startButton.setDefaultButton(true);
        startButton.setOnAction(e -> {
            categories.addAll(categoriesList.getItems());
            Game.setCategories(categories);
            System.out.println(Game.getCategories());
            joinStage();
        });
        startButton.setScaleX(1.5);
        startButton.setScaleY(1.5);

        HBox confirmButtonBox = new HBox(startButton);

        VBox selection = new VBox(topTextBox ,category, confirmButtonBox);
        selection.setPadding(new Insets(20,20,0,20));

        // last polish
        confirmButtonBox.setStyle("-fx-alignment: center; -fx-padding: 40");
        topTextBox.setStyle("-fx-alignment: center; -fx-padding: 20");
        checkBoxAll.setStyle("-fx-padding: 20; -fx-background-color: #399f97; -fx-border-color: #021C1D; -fx-border-width: 2px");
        categoriesList.setStyle("-fx-border-color: #021C1D; -fx-border-width: 2px");
        categoriesList.getSelectionModel().clearSelection();


        Group root = new Group(selection);
        stage.setScene(new Scene(root, Color.LIGHTCORAL));
        stage.setTitle("Kategorien");
        stage.show();
    }

    private void categoriesListEmptyTest(ListView<String> categoriesList) {
        if (categoriesList.getItems().isEmpty()) {
            categoriesList.getItems().add("");
        } else {
            categoriesList.getItems().remove("");
        }
        categoriesList.getSelectionModel().clearSelection();
    }

    private void containsAdd(ListView<String> categoriesList, String additionalCategory, CheckBox[] checkBoxes) {
        if (!additionalCategory.equals("")) {
            if (!categoriesList.getItems().contains(additionalCategory)) {
                for (CheckBox box : checkBoxes) {
                    if (additionalCategory.equals(box.getText()) && !box.isSelected()) {
                        categoriesList.getItems().add(additionalCategory);
                        box.setSelected(true);
                        categoriesListEmptyTest(categoriesList);
                        return;
                    }
                }
                categoriesList.getItems().add(additionalCategory);
                categoriesListEmptyTest(categoriesList);
            }
        }
    }

    private void joinStage() {

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void roundStage() {

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void checkStage() {

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void scoreStage() {

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void winnerStage() {

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.show();
    }
}