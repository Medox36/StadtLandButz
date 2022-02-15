package com.example.stadtlandbutzserver.gui;

import com.example.stadtlandbutzserver.Client;
import com.example.stadtlandbutzserver.game.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ServerGUI extends Application {

    private Stage stage;
    private final ArrayList<String> categories = new ArrayList<>();

    //joinStage
    private FlowPane flow;

    //checkStage
    private Text checkStageCategory;
    private ListView<String> checkStagePlayerNames;

    //scoreStage
    private ListView<BorderPane> scoreboard;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Game.setGui(this);
        selectionStage();
    }

    private void selectionStage() {
        ListView<String> categoriesList = new ListView<>();
        categoriesList.setEditable(false);
        emptyListTest(categoriesList);

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
                emptyListTest(categoriesList);
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
                emptyListTest(categoriesList);
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
        selection.setStyle("-fx-background-color: #F08080FF");

        Group root = new Group(selection);
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root, Color.web("#da6060")));
        stage.setTitle("Kategorien");
        stage.setMinHeight(702);
        stage.setMinWidth(553);
        stage.show();
    }

    private void emptyListTest(ListView<String> listView) {
        if (listView.getItems().isEmpty()) {
            listView.getItems().add("");
        } else {
            listView.getItems().remove("");
        }
        listView.getSelectionModel().clearSelection();
    }

    private void containsAdd(ListView<String> categoriesList, String additionalCategory, CheckBox[] checkBoxes) {
        if (!additionalCategory.equals("")) {
            if (!categoriesList.getItems().contains(additionalCategory)) {
                for (CheckBox box : checkBoxes) {
                    if (additionalCategory.equals(box.getText()) && !box.isSelected()) {
                        categoriesList.getItems().add(additionalCategory);
                        box.setSelected(true);
                        emptyListTest(categoriesList);
                        return;
                    }
                }
                categoriesList.getItems().add(additionalCategory);
                emptyListTest(categoriesList);
            }
        }
    }

    private void joinStage() {
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
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root, Color.web("#28988b")));
        stage.setTitle("Beitreten");
        stage.setMinHeight(804);
        stage.setMinWidth(936);
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
        letter.setStyle("-fx-font-size: 120; -fx-font-weight: bold; -fx-font-family: 'Malgun Gothic'");

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
        round.setStyle("-fx-background-color: #004445");

        Group root = new Group(round);
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root, Color.web("#00292a")));
        stage.setTitle("Runde " + Game.getRoundNumber());
        stage.sizeToScene();
        stage.setMinHeight(624);
        stage.setMinWidth(436);
        stage.show();
    }

    private void checkStage() {
        stage.hide();

        Button continueButton = new Button("Spiel fortsetzen");
        continueButton.setDefaultButton(true);
        continueButton.setScaleX(1.6);
        continueButton.setScaleY(1.6);
        continueButton.setOnAction(e -> {
            //TODO check if all answers from clients have been checked
            scoreStage();
        });

        Button finishButton = new Button("Spiel abschliessen");
        finishButton.setScaleX(1.6);
        finishButton.setScaleY(1.6);
        finishButton.setOnAction(e -> {
            //TODO check if all answers from clients have been checked
            winnerStage();
        });

        Button accept = new Button("Wort akzeptieren");
        accept.setScaleX(1.6);
        accept.setScaleY(1.6);
        accept.setOnMouseEntered(mouseEvent -> accept.setStyle("-fx-background-color: #bbd9a2"));
        accept.setOnMouseExited(mouseEvent -> accept.setStyle(""));
        accept.setOnMousePressed(mouseEvent -> accept.setStyle("-fx-background-color: #91b457"));
        accept.setOnMouseReleased(mouseEvent -> accept.setStyle(accept.isHover() ? "-fx-background-color: #bbd9a2" : ""));

        Button reject = new Button("Wort ablehnen");
        reject.setScaleX(1.6);
        reject.setScaleY(1.6);
        reject.setOnMouseEntered(mouseEvent -> reject.setStyle("-fx-background-color: #d08e83"));
        reject.setOnMouseExited(mouseEvent -> reject.setStyle(""));
        reject.setOnMousePressed(mouseEvent -> reject.setStyle("-fx-background-color: #af6a62"));
        reject.setOnMouseReleased(mouseEvent -> reject.setStyle(accept.isHover() ? "-fx-background-color: #d08e83" : ""));

        VBox buttons1 = new VBox(50, accept, reject);
        buttons1.setStyle("-fx-alignment: center");

        VBox buttons2 = new VBox(40, continueButton, finishButton);
        buttons2.setStyle("-fx-alignment: center");
        buttons2.setPadding(new Insets(30, 50, 0, 50));

        Text txt = new Text("Wort der Kategorie ");
        checkStageCategory = new Text("Nahrungsmittel");
        checkStageCategory.setStyle("-fx-font-style: italic");
        Text col = new Text(":");

        Label wordTitle = new Label("", new TextFlow(txt, checkStageCategory, col));
        wordTitle.setStyle("-fx-font-size: 36");

        Label clientWord = new Label("Fluss");
        clientWord.setMinWidth(965.0);
        AnchorPane.setLeftAnchor(clientWord, 0.0);
        AnchorPane.setRightAnchor(clientWord, 0.0);
        clientWord.setAlignment(Pos.CENTER);
        clientWord.setStyle("-fx-font-size: 60");

        VBox word = new VBox(20, wordTitle, clientWord);
        word.setStyle("-fx-alignment: center");
        word.setPadding(new Insets(20, 40, 40, 40));

        checkStagePlayerNames = new ListView<>();
        checkStagePlayerNames.setEditable(false);
        checkStagePlayerNames.setMaxHeight(240);
        checkStagePlayerNames.setScaleX(1.5);
        checkStagePlayerNames.setScaleY(1.5);
        emptyListTest(checkStagePlayerNames);

        VBox label = new VBox(word);
        label.setStyle("-fx-alignment: center");

        Label playerTitle = new Label("Spieler mit diesem Wort:");
        playerTitle.setStyle("-fx-font-size: 24");
        playerTitle.setMinWidth(300);

        VBox list = new VBox(60, playerTitle, checkStagePlayerNames);
        list.setStyle("-fx-alignment: center");
        list.setPadding(new Insets(0, 60, 0, 0));
        list.setMaxWidth(368);

        HBox wordChoice = new HBox(80, list, buttons1);
        wordChoice.setStyle("-fx-alignment: center");
        wordChoice.setPadding(new Insets(20, 0, 100, 0));

        VBox top = new VBox(20, label, wordChoice);
        top.setStyle("-fx-alignment: center; -fx-background-color: #fdc009; -fx-border-color: #2a2200; -fx-border-width: 2px");

        VBox bottom = new VBox(buttons2);
        bottom.setStyle("-fx-alignment: center");
        bottom.setPadding(new Insets(0, 20, 20, 20));

        VBox check = new VBox(top, bottom);
        check.setPadding(new Insets(20));
        check.setStyle("-fx-alignment: center; -fx-background-color: #ffa603");

        Group root = new Group(check);
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root, Color.web("#d89e00")));
        stage.setTitle("Auswertung");
        stage.setMinHeight(910);
        stage.setMinWidth(1101);
        stage.show();
    }

    public void setCheckStageCategory(String categoryName) {
        checkStageCategory.setText(categoryName);
    }

    public void setCheckStagePlayerNames(ArrayList<String> playerNames) {
        checkStagePlayerNames.getItems().clear();
        emptyListTest(checkStagePlayerNames);
        for (String str : playerNames) {
            checkStagePlayerNames.getItems().add(str);
        }
    }

    public void setCheckStageCategoryAndPlayerNames(String categoryName, ArrayList<String> playerNames) {
        setCheckStageCategory(categoryName);
        setCheckStagePlayerNames(playerNames);
    }

    private void scoreStage() {
        stage.hide();

        Button confirm = new Button("fortsetzen");
        confirm.setDefaultButton(true);
        confirm.setScaleX(1.6);
        confirm.setScaleY(1.6);
        confirm.setOnAction(e -> roundStage());

        Label title = new Label("Rangliste");
        title.setStyle("-fx-font-size: 72; -fx-font-style: italic; -fx-font-weight: bold");

        VBox label = new VBox(title);
        label.setPadding(new Insets(40));
        label.setStyle("-fx-alignment: center");

        scoreboard = new ListView<>();
        scoreboard.setEditable(false);
        scoreboard.setMinWidth(900);
        scoreboard.setMaxHeight(307);
        scoreboard.getItems().add(new BorderPane());
        scoreboard.setStyle("-fx-font-size: 30; -fx-font-weight: bold");
        scoreboard.getSelectionModel().clearSelection();

        VBox board = new VBox(scoreboard);
        board.setStyle("-fx-alignment: center");

        VBox top = new VBox(120, label, board);
        top.setPadding(new Insets(20));
        top.setStyle("-fx-alignment: center; -fx-border-width: 2px; -fx-border-color: #032556; -fx-background-color: #44a3e5");

        VBox button = new VBox(confirm);
        button.setPadding(new Insets(40));
        button.setStyle("-fx-alignment: center");

        VBox all = new VBox(20, top, button);
        all.setPadding(new Insets(20));
        all.setStyle("-fx-background-color: #1168ce; -fx-alignment: center");

        Group root = new Group(all);
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root, Color.web("#0341b9")));
        stage.setTitle("Punktestand");
        stage.setMinHeight(860);
        stage.setMinWidth(1000);
        stage.show();
    }

    /**
     *
     * @param pos position on leaderboard (0-based)
     * @param name name of the player
     * @param score score of the player
     */
    public void showTopFive(int pos, String name, String score) {
        BorderPane b = new BorderPane();
        b.setLeft(new Label(name));
        b.setRight(new Label(score));
        scoreboard.getItems().add(pos, b);
        if (scoreboard.getItems().size() > 5) {
            scoreboard.getItems().remove(5);
        }
        scoreboard.getSelectionModel().clearSelection();
    }

    /**
     *
     * @param pos position on leaderboard (0-based)
     * @param name of the player
     * @param score score of the player
     */
    public void showTopFive(int pos, String name, int score) {
        showTopFive(pos, name, String.valueOf(score));
    }

    private void winnerStage() {
        stage.hide();

        Group root = new Group();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit());
        stage.setScene(new Scene(root));
        stage.setMinHeight(300);
        stage.setMinWidth(200);
        stage.show();
    }
}