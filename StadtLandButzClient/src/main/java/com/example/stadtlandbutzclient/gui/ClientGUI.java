package com.example.stadtlandbutzclient.gui;

import com.example.stadtlandbutzclient.game.Game;
import com.example.stadtlandbutzclient.net.ClientInterpreter;
import com.example.stadtlandbutzclient.net.Package;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ClientGUI extends Application {

    private Stage stage;

    private final String ipRegex = "^(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))?(\\.(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))){0,3}";
    private final String nameRegex = "^(?=[a-zA-Z0-9._]{0,20}$)(?!.*[_.]{2})[^_.].*[^_.]$";

    private TableView<Row> categories;
    private TableView<Point> points;


    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Game.setGui(this);
        //joinStage();

        Game.setCategories(new ArrayList<>(List.of("Stadt,Land,Gewässer,Nahrungsmittel,Gebirge,Beruf,Modemarke,Sportart".split(","))));
        gameStage();
    }

    private void joinStage() {
        TextField ip = new TextField();
        ip.setPromptText("192.168.0.1");
        ip.setMaxWidth(120);
        ip.setTooltip(new Tooltip("IP-Adresse"));

        final UnaryOperator<TextFormatter.Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(ipRegex)) {
                return c ;
            } else {
                return null ;
            }
        };
        ip.setTextFormatter(new TextFormatter<>(ipAddressFilter));

        Spinner<Integer> port = new Spinner<>(0, 65535, 24452, 1);
        port.setEditable(true);
        port.setMaxWidth(70);
        port.setTooltip(new Tooltip("Port"));

        TextField playerName = new TextField();
        playerName.setPromptText("Spielername");

        final UnaryOperator<TextFormatter.Change> playerNameFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(nameRegex)) {
                return c ;
            } else {
                return null ;
            }
        };
        playerName.setTextFormatter(new TextFormatter<>(playerNameFilter));

        Button confirm = new Button("Eingeben");
        confirm.setScaleX(1.4);
        confirm.setScaleY(1.4);
        confirm.setDefaultButton(true);
        confirm.setOnAction(e -> {
            if (!Game.createClient(ip.getText(), port.getValue(), playerName.getText())) {
                //TODO show that connection can't be made
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Verbindung");
                a.setHeaderText("Keine Verbindung möglich");
                a.setContentText("Es konte keine Verbindung zum Server hergestellt werden.");
                a.showAndWait();
            } else {
                waitStage();
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

        TableColumn<Row, Boolean> editBox = new TableColumn<>();
        editBox.setCellValueFactory(cellData -> cellData.getValue().getEditableProperty());
        editBox.setCellFactory(CheckBoxTableCell.forTableColumn(editBox));
        editBox.setVisible(false);
        categories.getColumns().add(editBox);

        ArrayList<String> strs = Game.getCategories();
        for (int i = 0; i < strs.size(); i++) {
            TableColumn<Row, String> cat = new TableColumn<>(strs.get(i));
            cat.setEditable(true);
            cat.setResizable(false);
            cat.setSortable(false);
            cat.setReorderable(false);
            cat.setCellValueFactory(new PropertyValueFactory<>("cat" + i));
            cat.setCellFactory(cb -> new TextFieldTableCell<>(new DefaultStringConverter()) {
                @Override
                public void startEdit() {
                    boolean checkbox = getTableView().getItems().get(getIndex()).getEditableProperty().getValue();
                    if (checkbox) {
                        super.startEdit();
                    }
                }

                @Override
                public void commitEdit(String item) {
                    if (Game.isEditAllowed()) {
                        if (!isEditing() && !item.equals(getItem())) {
                            TableView<Row> table = getTableView();
                            if (table != null) {
                                TableColumn<Row, String> column = getTableColumn();
                                TableColumn.CellEditEvent<Row, String> event = new TableColumn.CellEditEvent<>(table,
                                        new TablePosition<>(table, getIndex(), column), TableColumn.editCommitEvent(), item);
                                Event.fireEvent(column, event);
                            }
                        }

                        super.commitEdit(item);
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                }
            });
            cat.setPrefWidth(200);
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
        points.setMaxWidth(82);

        HBox tables = new HBox(categories, points);
        tables.setAlignment(Pos.CENTER);

        VBox all = new VBox(tables);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #015657");
        stage.hide();
        stage = new Stage();
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Stadt Land Butz");
        stage.setMinHeight(Screen.getPrimary().getBounds().getHeight());
        stage.setMinWidth(Screen.getPrimary().getBounds().getWidth());
        stage.setAlwaysOnTop(true);
        stage.show();
        stage.setMaximized(true);
        stage.setResizable(false);

        Platform.runLater(() -> new Thread(() -> {
            Game.createClient("192.168.0.4", 24452, "ABC");
            Game.getClient().setUuid(UUID.randomUUID());
            ClientInterpreter.interpret(new Package("0100", "0@L", Game.getClient().getUUID()));
            ClientInterpreter.interpret(new Package("0101", "", Game.getClient().getUUID()));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ClientInterpreter.interpret(new Package("0110", "", Game.getClient().getUUID()));
            ClientInterpreter.interpret(new Package("1000", "15@0", Game.getClient().getUUID()));
        }).start());
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
        categories.getColumns().get(row).getTableView().getItems().get(0).getEditableProperty().setValue(editable);
    }

    public void setMadePointsInRound(int points, int round) {
        this.points.getItems().add(new Point());
        this.points.getItems().get(round).setPoint(points);
    }

    public void addRound(Character letter) {
        categories.getItems().add(new Row(letter));
    }

    private void resultStage() {

    }

}