package com.example.stadtlandbutzserver.gui;

import com.example.stadtlandbutzserver.Client;
import com.example.stadtlandbutzserver.TimeLabel;
import com.example.stadtlandbutzserver.game.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGUI extends Application {

    private Stage stage;
    private final ArrayList<String> categories = new ArrayList<>();

    private FlowPane flow;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setOnCloseRequest(windowEvent -> Platform.runLater(Game::exit));
        Game.setGui(this);
        selectionStage();
    }

    private void selectionStage() {
        ListView<String> categoriesList = new ListView<>();
        categoriesList.setEditable(false);
        categoriesListEmptyTest(categoriesList);

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
            additionalCategory.clear();
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
            System.out.println("(origin=GUI) categories: " + Game.getCategories());
            try {
                joinStage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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


        Group root = new Group(selection);
        stage.setScene(new Scene(root, Color.LIGHTCORAL));
        stage.setTitle("Kategorien");
        stage.setMinHeight(300);
        stage.setMinWidth(200);
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

    private void joinStage() throws IOException {
        stage.hide();

        Label title = new Label("Beitreten über:");
        Label ip = new Label("IP: Server initializing");
        Label port = new Label("Port: Server initializing");
        title.setStyle("-fx-font-size: 60; -fx-font-style: italic; -fx-padding: 10");
        ip.setStyle("-fx-font-size: 36; -fx-font-style: italic; -fx-padding: 5");
        port.setStyle("-fx-font-size: 36; -fx-font-style: italic; -fx-padding: 5");

        VBox w1 = new VBox(1,ip, port);
        VBox w2 = new VBox(15, title, w1);
        w2.setStyle("-fx-background-color: #dbefef");

        HBox topText = new HBox(w2);
        topText.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: #6ebdb5");

        flow = new FlowPane(Orientation.HORIZONTAL);
        flow.setPadding(new Insets(10));
        flow.setVgap(10);
        flow.setHgap(20);
        flow.setAlignment(Pos.TOP_CENTER);
        flow.setStyle("-fx-background-color: #4aada2");

        ScrollPane scroll = new ScrollPane();
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMaxWidth(900);
        scroll.setPrefWidth(900);
        scroll.setMaxHeight(350);
        scroll.setPrefHeight(350);
        scroll.setContent(flow);
        scroll.viewportBoundsProperty().addListener((ov, oldBounds, bounds) -> {
            flow.setPrefWidth(bounds.getWidth());
            flow.setPrefHeight(bounds.getHeight());
        });
        scroll.getStylesheets().add(Objects.requireNonNull(ServerGUI.class.getResource("/css/joinStage.css")).toString());

        VBox players = new VBox(scroll);
        players.setStyle("-fx-alignment: center; -fx-padding: 10");

        Button start = new Button("Starten");
        start.setScaleX(1.6);
        start.setScaleY(1.6);
        start.setOnAction(e -> {
            Game.getServer().letClientsConnect(false);
            roundStage();
        });

        HBox confirmButtonBox = new HBox(start);
        confirmButtonBox.setStyle("-fx-alignment: center; -fx-padding: 40");

        VBox join = new VBox(topText, players, confirmButtonBox);
        join.setStyle("-fx-background-color: #4aada2");

        Group root = new Group(join);
        stage.setScene(new Scene(root));
        stage.setTitle("Beitreten");
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        stage.show();

        //show IP and Port
        Game.startServer();

        String hostAdd = "IP-Adresse konnte nicht gefunden werden";
        try {
            hostAdd = InetAddress.getLocalHost().getHostAddress();
            System.out.println("(origin=GUI) ip: " + hostAdd);
            System.out.println("(origin=GUI) port: " + Game.getServerPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ip.setText("IP: " + hostAdd);
        port.setText("Port: " + Game.getServerPort());
    }

    public void addPlayer(Client client) {
        addPlayer(client.getPlayerName());
    }

    public void addPlayer(String str) {
        Label label = new Label(str);
        label.setTextFill(Color.WHITE);
        label.setMinSize(80, 30);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-padding: 10; -fx-background-color: #23076a; -fx-font-size: 24");
        flow.getChildren().add(label);
    }

    private void roundStage() {
        stage.hide();
        Game.incRoundNumber();

        Button end = new Button("Runde beenden");
        end.setDefaultButton(true);
        end.setScaleX(1.6);
        end.setScaleY(1.6);
        end.setOnAction(e -> {
            //TODO fire on round-time elapsed
            checkStage();
        });

        TimeLabel timeLabel = new TimeLabel(0, 0);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(timeLabel::incr);
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 1000L, 1000L);

        Label letterTitle = new Label("Buchstabe:");
        letterTitle.setTextFill(Color.WHITE);
        letterTitle.setStyle("-fx-font-size: 48");

        Label letter = new Label(Game.nextLetter());
        letter.setTextFill(Color.WHITE);
        letter.setStyle("-fx-font-size: 120; -fx-font-weight: bold;");

        VBox top = new VBox(timeLabel);
        top.setPadding(new Insets(15, 40, 40, 40));
        top.setStyle("-fx-alignment: center; -fx-background-color: #2c7973");

        VBox middle = new VBox(letterTitle, letter);
        middle.setPadding(new Insets(20));
        middle.setStyle("-fx-alignment: center");

        VBox bottom = new VBox(end);
        bottom.setPadding(new Insets(20));
        bottom.setStyle("-fx-alignment: center");

        VBox round = new VBox(5, top, middle, bottom);
        round.setPadding(new Insets(10));

        Group root = new Group(round);
        stage.setScene(new Scene(root, Color.web("#004445")));
        stage.setTitle("Runde " + Game.getRoundNumber());
        stage.setMinHeight(650);
        stage.setMinWidth(435);
        stage.sizeToScene();
        stage.show();
    }

    private void checkStage() {
        stage.hide();

        Button confirm = new Button();
        confirm.setOnAction(e -> {
            //TODO check if all answers from clients have been checked
            scoreStage();
        });

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        stage.show();
    }

    private void scoreStage() {
        stage.hide();

        Button confirm = new Button();
        confirm.setOnAction(e -> {
            //TODO check if last round
            roundStage();
        });

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        stage.show();
    }

    private void winnerStage() {
        stage.hide();

        Group root = new Group();
        stage.setScene(new Scene(root));
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        stage.show();
    }
}