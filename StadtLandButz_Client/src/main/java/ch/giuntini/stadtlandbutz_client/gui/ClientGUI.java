package ch.giuntini.stadtlandbutz_client.gui;

import ch.giuntini.stadtlandbutz_client.game.Game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Objects;

public class ClientGUI extends Application {

    private Stage stage;

    private final String codeRegex = "^([1-9]\\d{0,6})$";
    private final String validateCodeRegex = "^([1-9]\\d{6})$";
    private final String nameRegex = "^(?=.{0,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z\\d._]+(?<![_.])$";
    private final String validateNameRegex = "^[a-zA-Z\\d._]{0,20}$";

    private TableView<Row> categories;
    private TableView<Point> points;

    private TextField totalPoints;


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.getIcons().add(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/stadtlandbutz.png"))));
        Game.setGui(this);

        joinStage();
    }

    public void joinStage() {
        TextField code = new TextField();
        code.setPromptText("Game-Code");
        code.setTooltip(new Tooltip("GameCode"));
        code.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if  (text.matches(codeRegex)) {
                return change ;
            } else {
                return null ;
            }
        }));

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
            if (code.getText().matches(validateCodeRegex)) {
                if (playerName.getText().matches(nameRegex)) {
                    Game.setGameCode(Integer.parseInt(code.getText()));
                    if (!Game.createClient(playerName.getText(), getParameters().getRaw())) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Verbindung");
                        a.setHeaderText("Keine Verbindung möglich");
                        a.setContentText("Es konte keine Verbindung zum Server hergestellt werden.");
                        a.showAndWait();
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
                a.setTitle("Game-Code");
                a.setHeaderText("Ungültiger Game-Code");
                a.showAndWait();
            }
        });

        HBox button = new HBox(confirm);
        button.setPadding(new Insets(10, 0, 10, 0));
        button.setAlignment(Pos.CENTER);

        Label sep = new Label(":");
        sep.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-alignment: center");

        VBox textFields = new VBox(10, code, playerName, button);
        textFields.setAlignment(Pos.CENTER);
        textFields.setPadding(new Insets(10));
        textFields.setStyle("-fx-background-color: white");

        ImageView logo = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/stadtlandbutz.png"))));
        logo.setFitWidth(221);
        logo.setFitHeight(221);

        VBox all = new VBox(40, logo, textFields);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #004445");
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Beitreten");
        stage.setMinHeight(500);
        stage.setMinWidth(470);
        stage.setHeight(500);
        stage.setWidth(470);
        stage.hide();
        stage.show();
        centerStageOnScreen();
        confirm.requestFocus();
    }

    public void waitStage() {
        Label text = new Label("Bitte warten");
        text.setStyle("-fx-font-size: 60; -fx-font-weight: bold");

        VBox all = new VBox(text);
        all.setAlignment(Pos.CENTER);

        Group group = new Group(all);
        BorderPane root = new BorderPane();
        root.setCenter(group);
        root.setStyle("-fx-background-color: #015657");
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.getScene().setRoot(root);
        stage.setTitle("Beitreten");
        if (!stage.isMaximized()) {
            stage.setWidth(stage.getWidth() + 1);
        }
        stage.show();
        if (!stage.isMaximized()) {
            stage.setWidth(stage.getWidth() + 1);
        }
        centerStageOnScreen();
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

        ImageView stadt = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/stadt.png"))));
        stadt.setFitHeight(100);
        stadt.setFitWidth(300);

        ImageView land = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/land.png"))));
        land.setRotate(-90);
        land.setFitHeight(40);
        land.setFitWidth(150);

        ImageView butz = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/butz.png"))));
        butz.setFitHeight(100);
        butz.setFitWidth(300);

        ImageView reachablePoints = new ImageView(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/points.png"))));
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
        //stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();

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

    public void setTableEditable(boolean editable, int row) {
        categories.getColumns().get(row).getTableView().getItems().get(row).editableProperty().setValue(editable);

        // workaround to cancel ongoing edits
        if (!editable) {
            categories.edit(-1, null);
        }
    }

    public void setMadePointsInRound(int points, int round) {
        while (this.points.getItems().size() <= round) {
            this.points.getItems().add(new Point(0));
        }
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
        Label name = new Label(Game.getClient().getPlayerName());
        name.setTextFill(Color.WHITE);
        name.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-alignment: center");
        name.setWrapText(true);
        name.setMaxWidth(340);

        Label placeLabel = new Label(place + " Platz");
        placeLabel.setTextFill(Color.WHITE);
        placeLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold");

        Label pointsLabel = new Label(Game.getClient().getPoints() + " Pt.");
        pointsLabel.setTextFill(Color.WHITE);
        pointsLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold");

        VBox pod = new VBox(20, placeLabel, pointsLabel);
        pod.setMinSize(360, 410);
        pod.setMaxSize(360, 410);
        pod.setAlignment(Pos.TOP_CENTER);
        pod.setPadding(new Insets(40, 0, 0, 0));
        pod.setStyle("-fx-background-color: #864bbf; -fx-border-color: #302177; -fx-border-width: 2px");

        VBox topFirst = new VBox(20, name, pod);
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
        stage.getIcons().add(new Image(Objects.requireNonNull(ClientGUI.class.getResourceAsStream("/ch/giuntini/stadtlandbutz_client/images/stadtlandbutz.png"))));
        stage.setOnCloseRequest(windowEvent -> Game.exit(false));
        stage.setScene(new Scene(root));
        stage.setTitle("Resultat");
        stage.setMinHeight(660);
        stage.setMinWidth(460);
        if (!stage.isMaximized()) {
            stage.setWidth(stage.getWidth() + 1);
        }
        stage.show();
        if (!stage.isMaximized()) {
            stage.setWidth(stage.getWidth() + 1);
        }
        centerStageOnScreen();
    }

    private strictfp void centerStageOnScreen() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = visualBounds.getWidth();
        double screenHeight = visualBounds.getHeight();
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        double x;
        double y;

        if (stageWidth < stage.getMinWidth()) {
            stageWidth = stage.getMinWidth();
        }
        if (stageHeight < stage.getMinHeight()) {
            stageHeight = stage.getMinHeight();
        }

        x = (screenWidth / 2.0) - (stageWidth / 2.0);
        y = (screenHeight / 2.0) - (stageHeight / 2.0);

        stage.setX(x);
        stage.setY(y);
    }
}
