package ch.giuntini.stadtlandbutz_host.game;

import ch.giuntini.stadtlandbutz_host.gui.HostGUI;
import ch.giuntini.stadtlandbutz_host.net.Client;
import ch.giuntini.stadtlandbutz_host.net.Host;
import ch.giuntini.stadtlandbutz_host.stringsimilarity.LevenshteinDistanceStrategy;
import ch.giuntini.stadtlandbutz_host.stringsimilarity.SimilarityScore;
import ch.giuntini.stadtlandbutz_host.stringsimilarity.StringSimilarityService;
import ch.giuntini.stadtlandbutz_package.Package;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class Game {
    private static ArrayList<Character> letters = new ArrayList<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static String categoryString;
    private static Vector<Client> clients = new Vector<>();
    private volatile static LinkedHashMap<Integer, LinkedHashMap<UUID, Vector<String>>> words = new LinkedHashMap<>();
    private volatile static LinkedHashMap<Integer, LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>>> sortedWords = new LinkedHashMap<>();
    private volatile static HashMap<UUID, Integer> points = new HashMap<>();
    private static Integer catPointer = 0;
    private static Integer wordPointer = 0;
    private static Integer addPointer = 0;
    private static Integer sortPointer = 0;
    private static Integer sortCatPointer = 0;
    private volatile static Boolean ready = false;
    private volatile static Boolean finished = false;
    private volatile static Boolean receiving = false;
    private static HostGUI gui;
    private static Host host;
    private static int gameCode;
    private volatile static int roundNumber = -1;

    public static void startHost(List<String> args) throws IOException {
        if (host != null) {
            host.stop();
        }
        host = new Host(args);
    }

    public static void stopHost() {
        if (clients != null) clients.clear();
        if (letters != null) letters.clear();
        if (clients != null) categories.clear();
        if (words != null) words.clear();
        if (sortedWords != null) sortedWords.clear();
        if (points != null) points.clear();
        roundNumber = -1;
    }

    public synchronized static void addWordsOfClient(String[] arr, UUID uuid) {
        LinkedHashMap<UUID, Vector<String>> tempMap = new LinkedHashMap<>();
        tempMap.put(uuid, new Vector<>(List.of(arr)));
        words.put(addPointer, tempMap);
        if (words.size() != clients.size()) {
            addPointer++;
            return;
        }
        sortWordsAndCats();
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public static void sortWordsAndCats() {
        receiving = false;
        while (sortCatPointer < categories.size()) {
            while (sortPointer <= addPointer) {
                LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> sortedCat = new LinkedHashMap<>();
                String cat = categories.get(sortCatPointer);

                LinkedHashMap<String, Vector<UUID>> sortedWordsOfCat = new LinkedHashMap<>();

                for (int i = 0; i <= addPointer; i++) {
                    LinkedHashMap<UUID, Vector<String>> temp = words.get(i);

                    for (UUID key : temp.keySet()) {
                        String strL = temp.get(key).get(sortCatPointer).toLowerCase();

                        SimilarityScore topScore = new StringSimilarityService(new LevenshteinDistanceStrategy())
                                .findTop(List.copyOf(sortedWordsOfCat.keySet()), strL);

                        if (sortedWordsOfCat.containsKey(strL)) {
                            sortedWordsOfCat.get(strL).add(key);
                        } else if (topScore != null && topScore.getScore() > 0.95) {
                            sortedWordsOfCat.get(topScore.getKey()).add(key);
                        } else {
                            Vector<UUID> uuids = new Vector<>();
                            uuids.add(key);
                            sortedWordsOfCat.put(strL, uuids);
                        }
                    }
                }
                sortedCat.put(cat, sortedWordsOfCat);
                sortedWords.put(sortCatPointer, sortedCat);
                sortPointer++;
            }
            sortPointer = 0;
            sortCatPointer++;
        }
        synchronized (ready) {
            ready = true;
        }
        if (sortedWords.isEmpty()) {
            synchronized (finished) {
                finished = true;
            }
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Korrektur");
                alert.setHeaderText("Keine Wörter zum korrigieren");
                alert.setContentText("Keiner hat in dieser Kategorie ein Wort eingegeben.");
                alert.showAndWait();
            });
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    public static void nextWordOrCategory() {
        LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> tempCat = sortedWords.get(catPointer);

        if (sortedWords.keySet().size() <= catPointer) {
            synchronized (finished) {
                finished = true;
            }
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Korrektur");
                alert.setHeaderText("Alles korrigiert");
                alert.showAndWait();
            });
            return;
        }

        String catName = tempCat.keySet().stream().findFirst().orElseThrow();

        LinkedHashMap<String, Vector<UUID>> tempWords = tempCat.get(catName);
        String keyWord = (String) tempWords.keySet().toArray()[wordPointer];

        Vector<UUID> uuids = tempWords.get(keyWord);

        ArrayList<String> playerNames = new ArrayList<>();

        for (UUID uuid : uuids) {
            Client client = getClientByUUID(uuid);
            if (client != null) {
                playerNames.add(client.getPlayerName());
            }
        }

        if (keyWord.isEmpty() || keyWord.isBlank() || keyWord.equals("~")) {
            incrementCorrPointers(tempWords);
            Platform.runLater(Game::nextWordOrCategory);
        } else {
            Platform.runLater(() -> gui.setCheckStageCategoryAndPlayerNames(catName, keyWord, playerNames));
        }
    }

    public synchronized static void acceptCurrWord() {
        LinkedHashMap<String, Vector<UUID>> tempWords = getTempWords();
        String keyWord = (String) tempWords.keySet().toArray()[wordPointer];

        Vector<UUID> uuids = tempWords.get(keyWord);

        int pointsMade = 0;
        int playerInCat = numberOfPlayersInCatInCurrRound();
        int playersWithSameWord = uuids.size();

        if (playerInCat == 1 && playersWithSameWord == 1) {
            pointsMade = 10;
        }
        if (playerInCat > 1) {
            pointsMade = 5;
        }
        if (playerInCat > 1 && playersWithSameWord > 1) {
            pointsMade = 1;
        }

        for (UUID uuid : uuids) {
            if (points.containsKey(uuid)) {
                Integer oldVal = points.get(uuid);
                points.put(uuid, oldVal + pointsMade);
            } else {
                points.put(uuid, pointsMade);
            }
        }

        incrementCorrPointers(tempWords);
    }

    public synchronized static void rejectCurrWord() {
        incrementCorrPointers(getTempWords());
    }

    public synchronized static void incrementCorrPointers(LinkedHashMap<String, Vector<UUID>> tempWords) {
        if ((tempWords.size()-1) <= wordPointer) {
            wordPointer = 0;
            catPointer++;
        } else {
            wordPointer++;
        }
    }

    private static int numberOfPlayersInCatInCurrRound() {
        int playerCount = 0;

        LinkedHashMap<String, Vector<UUID>> tempWords = getTempWords();
        Set<String> keyWord =  tempWords.keySet();
        for (String str : keyWord) {
            if (Objects.equals(str, "~")) {
                continue;
            }
            playerCount += tempWords.get(str).size();
        }
        return playerCount;
    }

    private static LinkedHashMap<String, Vector<UUID>> getTempWords() {
        LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> tempCat = sortedWords.get(catPointer);
        return tempCat.get(tempCat.keySet().stream().findFirst().orElseThrow());
    }

    public synchronized static void distPoints() {
        for (Client client : clients) {
            Integer madePoints = 0;
            if (points.containsKey(client.getUUID())) {
                 madePoints = points.get(client.getUUID());
            }
            client.addPoints(madePoints);
            host.sendPackage(new Package("010", "1000",  madePoints + "@" + getRoundNumber(), client.getUUID().toString()));
        }
    }

    public synchronized static boolean allWordsCorrected() {
        return finished || sortedWords.isEmpty();
    }

    public static void callScoreStage() {
        List<Client> sortedClients = clients.stream().sorted(Comparator.comparingInt(Client::getPoints).reversed()).collect(Collectors.toList());
        for (int i = 0; i < sortedClients.size() && i < 5; i++) {
            gui.showTopFive(i, sortedClients.get(i).getPlayerName(), sortedClients.get(i).getPoints());
        }
    }

    public static void callWinnerStage() {
        List<Client> sortedClients = clients.stream().sorted(Comparator.comparingInt(Client::getPoints).reversed()).collect(Collectors.toList());
        if (sortedClients.size() > 0)
            gui.setFirst(sortedClients.get(0));
        if (sortedClients.size() > 1)
            gui.setSecond(sortedClients.get(1));
        if (sortedClients.size() > 2)
            gui.setThird(sortedClients.get(2));
        for (int i = 0; i < sortedClients.size(); i++) {
            host.sendPackage(new Package("010", "1001", String.valueOf(i + 1), sortedClients.get(i).getUUID().toString()));
            System.out.println("Sent place: " + (i + 1) + " to: " + sortedClients.get(i).getUUID().toString());
        }
    }

    public synchronized static void resetCurrRoundStuff() {
        sortCatPointer = 0;
        catPointer = 0;
        wordPointer = 0;
        addPointer = 0;
        sortPointer = 0;
        words.clear();
        sortedWords.clear();
        points.clear();
        ready = false;
        finished = false;
    }

    public synchronized static boolean isCurrRoundReady() {
        return ready;
    }

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static void setGui(HostGUI gui) {
        Game.gui = gui;
    }

    public static HostGUI getGui() {
        return gui;
    }

    public static void addClientToGUI(Client client) {
        Platform.runLater(() -> gui.addPlayer(client));
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static String getCatsForClients() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = Game.getCategories().iterator();
        while (true) {
            String e = it.next();
            sb.append(e);
            if (it.hasNext()) {
                sb.append(',');
            } else {
                break;
            }
        }
        return sb.toString();
    }

    public static void generateCategoryString() {
        categoryString = getCatsForClients();
    }

    public static String getCategoryString() {
        return categoryString;
    }

    public static int getGameCode() {
        return gameCode;
    }

    public static void setGameCode(int gameCode) {
        Game.gameCode = gameCode;
    }

    public static Host getHost() {
        return host;
    }

    public static Client getClientByUUID(UUID uuid) {
        for (Client client : clients) {
            if (client.getUUID().equals(uuid)) {
                return client;
            }
        }
        return null;
    }

    public static void removeClientByUUID(UUID uuid) {
        Client client = getClientByUUID(uuid);
        if (client != null) {
            clients.remove(client);
            points.remove(client.getUUID());
            System.out.println("removed client:" + client.getUUID().toString() + " " + client.getPlayerName());
            if (receiving && words.size() == clients.size()) {
                System.out.println("Sorting from removal");
                sortWordsAndCats();
            }
        }
    }

    public synchronized static void incRoundNumber() {
        roundNumber++;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static int getVisualRoundNumber() {
        return roundNumber + 1;
    }

    public static void setReceiving(Boolean receiving) {
        Game.receiving = receiving;
    }

    public synchronized static void sendToAllClients(String prefix, String information) {
        host.sendPackage(new Package("011", prefix, information, null));
    }

    public static String nextLetter() {
        SecureRandom s = new SecureRandom();
        Character letter = (char) (s.nextInt(26) + 'a');
        while (letters.contains(letter)) {
            letter = (char) (s.nextInt(26) + 'a');
            //TODO check if all letters already have been used
        }
        letters.add(letter);
        return String.valueOf(letter).toUpperCase();
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        stopHost();
        host.stop();
        Platform.exit();
        gui = null;
        letters = null;
        categories = null;
        clients = null;
        words = null;
        sortedWords = null;
        points = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}
