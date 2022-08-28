package ch.giuntini.stadtlandbutz_host.game;

import ch.giuntini.stadtlandbutz_host.gui.HostGUI;
import ch.giuntini.stadtlandbutz_host.net.Client;
import ch.giuntini.stadtlandbutz_host.net.Host;
import ch.giuntini.stadtlandbutz_package.Package;
import javafx.application.Platform;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class Game {
    private static ArrayList<Character> letters = new ArrayList<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static Vector<Client> clients = new Vector<>();
    private static LinkedHashMap<Integer, LinkedHashMap<UUID, Vector<String>>> words = new LinkedHashMap<>();
    private static LinkedHashMap<Integer, LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>>> sortedWords = new LinkedHashMap<>();
    private static HashMap<UUID, Integer> points = new HashMap<>();
    private static Integer catPointer = 0;
    private static Integer wordPointer = 0;
    private static Integer addPointer = 0;
    private static Integer sortPointer = 0;
    private static Boolean ready = false;
    private static Boolean finished = false;
    private static HostGUI gui;
    private static Host host;
    private static int gameCode;
    private static int roundNumber = -1;

    public static void startHost() throws IOException {
        if (host != null) {
            host.stop();
        }
        host = new Host();
    }

    public static void stopHost() {
        clients = null;
        letters.clear();
        categories.clear();
        words.clear();
        sortedWords.clear();
        points.clear();
        roundNumber = -1;
    }

    public static void addWordsOfClient(String[] arr, UUID uuid) {
        LinkedHashMap<UUID, Vector<String>> tempMap = new LinkedHashMap<>();
        tempMap.put(uuid, new Vector<>(List.of(arr)));
        words.put(addPointer, tempMap);
        if (words.size() != clients.size()) {
            addPointer++;
            return;
        }
        sortWordsAndCats();
    }

    public static void sortWordsAndCats() {
        while (sortPointer <= addPointer) {
            LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> sortedCat = new LinkedHashMap<>();
            String cat = categories.get(sortPointer);

            LinkedHashMap<String, Vector<UUID>> sortedWordsOfCat = new LinkedHashMap<>();

            for (int i = 0; i <= addPointer; i++) {
                LinkedHashMap<UUID, Vector<String>> temp = words.get(i);

                Optional<UUID> firstKey = temp.keySet().stream().findFirst();
                if (firstKey.isPresent()) {
                    UUID key = firstKey.get();
                    String tempStr = temp.get(key).get(sortPointer);

                    if (sortedWordsOfCat.containsKey(tempStr)) {
                        sortedWordsOfCat.get(tempStr).add(key);
                    } else {
                        Vector<UUID> uuids = new Vector<>();
                        uuids.add(key);
                        sortedWordsOfCat.put(tempStr.toLowerCase(), uuids);
                    }
                }
            }
            if (sortedWordsOfCat.keySet().stream().allMatch(s -> s.isEmpty() || s.isBlank())) {
                continue;
            }
            sortedCat.put(cat, sortedWordsOfCat);
            sortedWords.put(sortPointer, sortedCat);
            sortPointer++;
        }
        synchronized (ready) {
            ready = true;
        }
    }

    public static void nextWordOrCategory() {
        LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> tempCat = sortedWords.get(catPointer);

        if (tempCat.keySet().size() >= catPointer) {
            synchronized (finished) {
                finished = true;
            }
        }

        String catName = "";
        Optional<String> firstKey = tempCat.keySet().stream().findFirst();
        if (firstKey.isPresent()) {
            catName = firstKey.get();
        }

        LinkedHashMap<String, Vector<UUID>> tempWords = tempCat.get(catName);
        String keyWord = (String) tempWords.keySet().toArray()[wordPointer];

        Vector<UUID> uuids = tempWords.get(keyWord);

        ArrayList<String> playerNames = new ArrayList<>();

        for (UUID uuid : uuids) {
            playerNames.add(Objects.requireNonNull(getClientByUUID(uuid)).getPlayerName());
        }

        String finalCatName = catName;

        if (tempWords.keySet().size() >= wordPointer) {
            wordPointer = 0;
            catPointer++;
        } else {
            wordPointer++;
        }

        Platform.runLater(() -> gui.setCheckStageCategoryAndPlayerNames(finalCatName, keyWord, playerNames));
    }

    public synchronized static void acceptCurrWord() {
        int catPointer0 = catPointer - 1;
        int wordPointer0 = wordPointer - 1;

        LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> tempCat = sortedWords.get(catPointer0);

        String catName = "";
        Optional<String> firstKey = tempCat.keySet().stream().findFirst();
        if (firstKey.isPresent()) {
            catName = firstKey.get();
        }

        LinkedHashMap<String, Vector<UUID>> tempWords = tempCat.get(catName);
        String keyWord = (String) tempWords.keySet().toArray()[wordPointer0];

        Vector<UUID> uuids = tempWords.get(keyWord);

        int pointsMade = 0;
        int playerInCat = numberOfPlayersInCatInCurrRound();
        if (uuids.size() >= 1) {
            pointsMade = 1;
        }
        if (playerInCat == 1) {
            pointsMade = 10;
        }
        if (tempWords.keySet().size() > 1) {
            pointsMade = 5;
        }

        for (UUID uuid : uuids) {
            if (points.containsKey(uuid)) {
                Integer oldVal = points.get(uuid);
                points.put(uuid, oldVal + pointsMade);
            }
        }
    }

    private static int numberOfPlayersInCatInCurrRound() {
        int catPointer0 = catPointer - 1;

        LinkedHashMap<String, LinkedHashMap<String, Vector<UUID>>> tempCat = sortedWords.get(catPointer0);

        String catName = "";
        Optional<String> firstKey = tempCat.keySet().stream().findFirst();
        if (firstKey.isPresent()) {
            catName = firstKey.get();
        }

        int playerCount = 0;

        LinkedHashMap<String, Vector<UUID>> tempWords = tempCat.get(catName);
        Set<String> keyWord =  tempWords.keySet();
        for (String str : keyWord) {
            playerCount += tempWords.get(str).size();
        }
        return playerCount;
    }

    public synchronized static void rejectCurrWord() {

    }

    public synchronized static void distPoints() {
        for (UUID uuid : points.keySet()) {
            Integer madePoints = points.get(uuid);
            Client client = Objects.requireNonNull(getClientByUUID(uuid));
            client.addPoints(madePoints);
            host.sendPackage(new Package("010", "1000",  madePoints + "@" + getRoundNumber(), uuid.toString()));
        }
    }

    public synchronized static boolean allWordsCorrected() {
        return finished;
    }

    public static void collScoreStage() {
        List<Client> sortedClients = clients.stream().sorted(Comparator.comparingInt(Client::getPoints).reversed()).collect(Collectors.toList());
        gui.showTopFive(0, sortedClients.get(0).getPlayerName(), sortedClients.get(0).getPoints());
        gui.showTopFive(1, sortedClients.get(1).getPlayerName(), sortedClients.get(1).getPoints());
        gui.showTopFive(2, sortedClients.get(2).getPlayerName(), sortedClients.get(2).getPoints());
        gui.showTopFive(3, sortedClients.get(3).getPlayerName(), sortedClients.get(3).getPoints());
        gui.showTopFive(4, sortedClients.get(4).getPlayerName(), sortedClients.get(4).getPoints());
    }

    public static void collWinnerStage() {
        List<Client> sortedClients = clients.stream().sorted(Comparator.comparingInt(Client::getPoints).reversed()).collect(Collectors.toList());
        gui.setFirst(sortedClients.get(0));
        gui.setSecond(sortedClients.get(1));
        gui.setThird(sortedClients.get(2));
        for (int i = 0; i < sortedClients.size(); i++) {
            host.sendPackage(new Package("010", "1001", String.valueOf(i), sortedClients.get(i).getUUID().toString()));
        }
    }

    public static void resetCurrRoundStuff() {
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

    public static void incRoundNumber() {
        roundNumber++;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static int getVisualRoundNumber() {
        return roundNumber + 1;
    }

    public synchronized static void sendToAllClients(String prefix, String information) {
        for (Client client : clients) {
            host.sendPackage(new Package("011", prefix, information, client.getUUID().toString()));
        }
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
        Platform.exit();
        gui = null;
        letters = null;
        categories = null;
        words = null;
        sortedWords = null;
        points = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}
