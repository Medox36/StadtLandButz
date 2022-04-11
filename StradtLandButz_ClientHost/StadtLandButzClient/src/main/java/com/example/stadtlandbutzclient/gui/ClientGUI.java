package com.example.stadtlandbutzclient.gui;

import com.example.stadtlandbutzclient.game.Game;
import com.example.stadtlandbutzclient.net.ClientInterpreter;
import com.example.stadtlandbutzclient.net.Package;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientGUI extends Application {

    private Stage stage;

    private final String ipRegex = "^(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))?(\\.(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))){0,3}";
    private final String validateIpRegex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private final String nameRegex = "^(?=.{0,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    private final String validateNameRegex = "^[a-zA-Z0-9._]{0,20}$";

    private TableView<Row> categories;
    private TableView<Point> points;

    private TextField totalPoints;


    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Game.setGui(this);

        joinStage();

        //testInit();
        //test();
    }

    public void joinStage() {
        TextField ip = new TextField();
        ip.setPromptText("192.168.0.1");
        ip.setMaxWidth(120);
        ip.setTooltip(new Tooltip("IP-Adresse"));

        ip.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if  (text.matches(ipRegex)) {
                return change ;
            } else {
                return null ;
            }
        }));

        Spinner<Integer> port = new Spinner<>(0, 65535, 24452, 1);
        port.setEditable(true);
        port.setMaxWidth(72);
        port.setTooltip(new Tooltip("Port"));

        TextField playerName = new TextField();
        playerName.setPromptText("Spielername");

        playerName.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches(validateNameRegex)) {
                return change;
            } else {
                return null;
            }
        }));

        Button confirm = new Button("Eingeben");
        confirm.setScaleX(1.4);
        confirm.setScaleY(1.4);
        confirm.setDefaultButton(true);
        confirm.setOnAction(e -> {
            if (ip.getText().matches(validateIpRegex)) {
                if (playerName.getText().matches(nameRegex)) {
                    if (!Game.createClient(ip.getText(), port.getValue(), playerName.getText())) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Verbindung");
                        a.setHeaderText("Keine Verbindung möglich");
                        a.setContentText("Es konte keine Verbindung zum Server hergestellt werden.");
                        a.showAndWait();
                    } else {
                        waitStage();
                    }
                } else {
                    Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setTitle("Spielername");
                    a.setHeaderText("Ungültiger Spielername");
                    a.setContentText("Kein \"_\" und \".\" am Anfang sowie am Schluss.\nKein \"__\" oder \"_.\" oder \"._\" oder \"..\" mitten drin.");
                    a.showAndWait();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("IP-Adresse");
                a.setHeaderText("Ungültige IP-Adresse");
                a.showAndWait();
            }
        });

        HBox button = new HBox(confirm);
        button.setPadding(new Insets(10, 0, 10, 0));
        button.setAlignment(Pos.CENTER);

        Label sep = new Label(":");
        sep.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-alignment: center");

        HBox ipAndPort = new HBox(2.5, ip, sep, port);

        VBox textFields = new VBox(10, ipAndPort, playerName, button);
        textFields.setAlignment(Pos.CENTER);
        textFields.setPadding(new Insets(10));
        textFields.setStyle("-fx-background-color: white");

        ImageView logo = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/com/example/stadtlandbutzclient/images/stadtlandbutz.png"))));
        logo.setFitWidth(221);
        logo.setFitHeight(221);

        VBox all = new VBox(40, logo, textFields);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #004445");
        stage.hide();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Beitreten");
        stage.setMinHeight(500);
        stage.setMinWidth(470);
        stage.setMaxHeight(500);
        stage.setMaxWidth(470);
        stage.setHeight(500);
        stage.setWidth(470);
        stage.setResizable(false);
        stage.show();
        confirm.requestFocus();
    }

    private void waitStage() {
        Label text = new Label("Bitte warten");
        text.setStyle("-fx-font-size: 60; -fx-font-weight: bold");

        VBox all = new VBox(text);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #015657");
        stage.hide();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Beitreten");
        stage.setMinHeight(500);
        stage.setMinWidth(470);
        stage.setResizable(false);
        stage.show();
    }

    public void gameStage() {
        categories = new TableView<>();

        TableColumn<Row, Character> letter = new TableColumn<>("Buchstabe");
        letter.setEditable(false);
        letter.setResizable(false);
        letter.setSortable(false);
        letter.setReorderable(false);
        letter.setCellValueFactory(new PropertyValueFactory<>("letter"));
        letter.setStyle("-fx-alignment: center");
        categories.getColumns().add(letter);

        ArrayList<String> strs = Game.getCategories();
        for (int i = 0; i < strs.size(); i++) {
            int finalI = i;

            TableColumn<Row, String> cat = new TableColumn<>(strs.get(i));
            cat.setEditable(true);
            cat.setResizable(true);
            cat.setSortable(false);
            cat.setReorderable(false);
            cat.setCellValueFactory(cellData -> cellData.getValue().getCategoryProperty(finalI));
            cat.setCellFactory(TextFieldTableCell.forTableColumn());
            cat.setOnEditCommit(t -> {
                boolean edit = t.getTableView().getItems().get(t.getTablePosition().getRow()).editableProperty().getValue();
                    if (Game.isEditAllowed() && edit) {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setCat(t.getNewValue(), finalI);
                        cat.getTableView().getItems().get(t.getTablePosition().getRow()).setCat(t.getNewValue(), finalI);

                    } else {
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setCat(t.getOldValue(), finalI);
                        cat.getTableView().getItems().get(t.getTablePosition().getRow()).setCat(t.getOldValue(), finalI);
                    }
                }
            );
            cat.setOnEditStart(t -> {
                boolean edit = t.getTableView().getItems().get(t.getTablePosition().getRow()).editableProperty().getValue();
                if (!edit) {
                    Platform.runLater(() -> categories.edit(-1, null));
                }
            });
            cat.setPrefWidth(200);
            cat.setMinWidth(100);
            cat.setMaxWidth(400);
            categories.getColumns().add(cat);
        }
        categories.setEditable(true);

        TableColumn<Point, Integer> point = new TableColumn<>("Punkte");
        point.setEditable(false);
        point.setResizable(false);
        point.setSortable(false);
        point.setReorderable(false);
        point.setCellValueFactory(new PropertyValueFactory<>("point"));
        point.setStyle("-fx-alignment: center");

        points = new TableView<>();
        points.getColumns().add(point);
        points.setMaxWidth(83);
        points.setMinWidth(83);

        HBox tables = new HBox(categories, points);
        tables.setAlignment(Pos.CENTER);

        ImageView stadt = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/com/example/stadtlandbutzclient/images/stadt.png"))));
        stadt.setFitHeight(100);
        stadt.setFitWidth(300);

        ImageView land = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/com/example/stadtlandbutzclient/images/land.png"))));
        land.setRotate(-90);
        land.setFitHeight(40);
        land.setFitWidth(150);

        ImageView butz = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/com/example/stadtlandbutzclient/images/butz.png"))));
        butz.setFitHeight(100);
        butz.setFitWidth(300);

        ImageView reachablePoints = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/com/example/stadtlandbutzclient/images/points.png"))));
        reachablePoints.setFitHeight(100);
        reachablePoints.setFitWidth(270);

        VBox pointImage = new VBox(reachablePoints);
        pointImage.setAlignment(Pos.CENTER);
        pointImage.setPadding(new Insets(0, 50, 0, 50));

        HBox butzImage = new HBox(butz);
        butzImage.setAlignment(Pos.CENTER);
        butzImage.setPadding(new Insets(0, 40, 0, 0));

        HBox stadtLandButz = new HBox(stadt, land, butzImage, reachablePoints);
        stadtLandButz.setAlignment(Pos.CENTER);

        Text name = new Text("NAME:\n");
        name.setStyle("-fx-font-size: 36; -fx-font-weight: bold");
        name.setFill(Color.WHITE);

        Text playerName = new Text(Game.getClient().getPlayerName());
        playerName.setStyle("-fx-font-style: italic; -fx-font-size: 30");
        playerName.setFill(Color.WHITE);

        Label nameAll = new Label("", new TextFlow(name, playerName));

        BorderPane header = new BorderPane();
        header.setLeft(stadtLandButz);
        header.setRight(nameAll);

        totalPoints = new TextField("0");
        totalPoints.setMinWidth(points.getMaxWidth());
        totalPoints.setMaxWidth(points.getMaxWidth());
        totalPoints.setEditable(false);
        totalPoints.setStyle("-fx-alignment: center");

        Label totalText = new Label("TOTAL:");
        totalText.setTextFill(Color.WHITE);
        totalText.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-alignment: center");

        VBox totalWrap = new VBox(totalText, totalPoints);
        totalWrap.setPadding(new Insets(10, 0, 0, 0));

        HBox total = new HBox(totalWrap);
        total.setAlignment(Pos.CENTER_RIGHT);

        //the number 100 is just a placeholder
        Rectangle r1 = new Rectangle(100, 3, Color.WHITE);

        HBox sep1 = new HBox(r1);
        sep1.setPadding(new Insets(40, 0, 40, 0));
        sep1.setAlignment(Pos.CENTER);

        //the number 100 is just a placeholder
        Rectangle r2 = new Rectangle(100, 3, Color.WHITE);

        HBox sep2 = new HBox(r2);
        sep2.setPadding(new Insets(20, 0, 20, 0));
        sep2.setAlignment(Pos.CENTER);

        VBox all = new VBox(header, sep1, tables, total, sep2);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #131313"); //#015657
        stage.hide();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Stadt Land Butz");
        stage.setAlwaysOnTop(true);
        stage.show();
        stage.setMaximized(true);
        stage.setResizable(false);

        // calc widths
        double MAX = 1865;
        double MARGIN = 100;
        double STAGEWIDTH = stage.getWidth();
        double POINTS = points.getWidth();
        double MAXWIDTH = MAX - MARGIN;
        double CORRECTION;

        if (STAGEWIDTH < MAX) {
            CORRECTION = MAXWIDTH - STAGEWIDTH + MARGIN;
            MAXWIDTH -= CORRECTION;
        }

        double MAXCATEGORIES = MAXWIDTH - POINTS;
        double RECTANGLELINES = MAXWIDTH;
        double MAXTABLES = MAXCATEGORIES + POINTS;
        //

        categories.setMaxWidth(MAXCATEGORIES);
        categories.setMinWidth(MAXCATEGORIES);
        tables.setMaxWidth(MAXTABLES);
        header.setMaxWidth(MAXTABLES);
        all.setMaxWidth(MAXWIDTH);
        r1.setWidth(RECTANGLELINES);
        r2.setWidth(RECTANGLELINES);
        sep1.setMaxWidth(MAXTABLES);
        sep2.setMaxWidth(MAXTABLES);

        // originally: stage.getWidth() - points.getWidth() - 100
    }

    public static class Point {
        private Integer point;

        public Point() {
        }

        public Point(int point) {
            this.point = point;
        }

        public Integer getPoint() {
            return point;
        }

        public void setPoint(Integer point) {
            this.point = point;
        }
    }

    public void setTableEditable(boolean editable, int row) {
        categories.getColumns().get(row).getTableView().getItems().get(row).editableProperty().setValue(editable);

        // workaround to cancel ongoing edits
        if (!editable) {
            categories.edit(-1, null);
        }
    }

    public void setMadePointsInRound(int points, int round) {
        this.points.getItems().add(new Point());
        this.points.getItems().get(round).setPoint(points);
        totalPoints.setText(String.valueOf(Game.getClient().getPoints()));
    }

    public void addRound(Character letter) {
        categories.getItems().add(new Row(letter));
    }

    public Row getCurrentRow(int currRow) {
        return categories.getItems().get(currRow);
    }

    public void resultStage(String place) {
        Label first = new Label(Game.getClient().getPlayerName());
        first.setTextFill(Color.WHITE);
        first.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-alignment: center");
        first.setWrapText(true);
        first.setMaxWidth(280);

        Label firstPoints = new Label("0 Pt.");
        firstPoints.setTextFill(Color.WHITE);
        firstPoints.setStyle("-fx-font-size: 48px; -fx-font-weight: bold");

        Label placeLabel = new Label(place);
        placeLabel.setTextFill(Color.WHITE);
        placeLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold");

        VBox pod = new VBox(20, new Label(place), new Label(String.valueOf(Game.getClient().getPoints())));
        pod.setMinSize(300, 350);
        pod.setMaxSize(300, 350);
        pod.setAlignment(Pos.TOP_CENTER);
        pod.setPadding(new Insets(40, 0, 0, 0));
        pod.setStyle("-fx-background-color: #864bbf; -fx-border-color: #302177; -fx-border-width: 2px");

        VBox topFirst = new VBox(20, first, pod);
        topFirst.setAlignment(Pos.BOTTOM_CENTER);

        HBox podium = new HBox(topFirst);
        podium.setAlignment(Pos.BOTTOM_CENTER);

        VBox top = new VBox(podium);
        top.setStyle("-fx-alignment: center");

        VBox all = new VBox(top);
        all.setStyle("-fx-background-color: #55309b");
        all.setPadding(new Insets(20));

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #24066b");
        stage.hide();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Resultat");
        stage.setMinHeight(600);
        stage.setMinWidth(400);
        stage.show();
    }

    private void testInit() {
        Game.setCategories(new ArrayList<>(List.of("Stadt,Land,Gewässer,Nahrungsmittel,Gebirge,Beruf,Modemarke,Sportart".split(","))));
        Game.createClient("192.168.0.4", 24452, "ABC");
        Game.getClient().setUUID(UUID.randomUUID());
    }

    private void test() {
        Platform.runLater(() -> new Thread(() -> {
            ClientInterpreter.interpret(new Package("0100", "0@L", Game.getClient().getUUID()));
            ClientInterpreter.interpret(new Package("0101", "", Game.getClient().getUUID()));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ClientInterpreter.interpret(new Package("0110", "", Game.getClient().getUUID()));
            ClientInterpreter.interpret(new Package("1000", "15@0", Game.getClient().getUUID()));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Game.collectWordsOfCurrentRound());
        }).start());
    }
}