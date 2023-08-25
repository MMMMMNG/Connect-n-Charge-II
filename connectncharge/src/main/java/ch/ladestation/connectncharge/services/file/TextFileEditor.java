package ch.ladestation.connectncharge.services.file;

import ch.ladestation.connectncharge.model.game.gameinfo.Player;
import ch.ladestation.connectncharge.util.mvcbase.MvcLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class TextFileEditor {
    private static final String DEFAULT_ADMIN_CODE = "123456";

    private static final MvcLogger LOGGER = new MvcLogger();

    private static final int NUMBER_OF_LEVELS = 5;

    private TextFileEditor() {
    }

    public static Map<Integer, List<Object>> readLevels() {
        Map<Integer, List<Object>> levels = new HashMap<>();

        for (int i = 1; i < NUMBER_OF_LEVELS + 1; i++) {
            int levelNumber = i;
            InputStream level = TextFileEditor.class.getResourceAsStream("/textfiles/levels/" + i + ".txt");
            Objects.requireNonNull(level, "error, " + "/textfiles/levels/" + i + ".txt" + "file was null");

            List<String> lines = readFile(level);

            List<Integer> terminals = new ArrayList<>();
            List<List<Integer>> solution = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts[0].equals("T")) {
                    terminals.add(Integer.parseInt(parts[1]));
                } else {
                    List<Integer> connection = new ArrayList<>();
                    connection.add(Integer.parseInt(parts[0]));
                    connection.add(Integer.parseInt(parts[1]));
                    solution.add(connection);
                }
            }
            List<Object> levelContent = new ArrayList<>();
            levelContent.add(terminals);
            levelContent.add(solution);
            levels.put(levelNumber, levelContent);

        }

        return levels;
    }

    private static List<String> readFile(InputStream filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return lines;
    }

    /**
     * This method takes the data from the player file if there is one.
     *
     * @param filePath
     * @return a list of saved players.
     */
    public static List<Player> readPlayerDataFromFile(String filePath) {
        List<Player> players = new ArrayList<>();
        if (!createPlayerFile(filePath)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null && !line.replaceAll(" ", "").equals("")
                    && line.contains(",")) {
                    players.add(new Player(line.split(",")[0], line.split(",")[1]));
                }
            } catch (IOException e) {
                LOGGER.logError("An error occurred while reading the file: " + e.getMessage());
            }
        }
        return players;
    }

    /**
     * This methode creates the save file for the players.
     *
     * @param filePath
     * @return a true if a new file is created and false when a file is already existing.
     */
    private static boolean createPlayerFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.createNewFile()) {
                LOGGER.logInfo("File created successfully.");
                return true;
            } else {
                LOGGER.logInfo("File already exists.");
                return false;
            }
        } catch (IOException e) {
            LOGGER.logError("An error occurred while creating the file: " + e.getMessage());
        }
        return false;
    }

    /**
     * This method writes to the defined file given by the parameter files.
     *
     * @param filePath
     * @param lines
     */
    public static void writeTextFile(String filePath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.logError("An error has occurred while writing the player file. " + e);
        }
    }

    public static String getAdminCode() {
        InputStream adminCodeFile = TextFileEditor.class.getResourceAsStream("/textfiles/code/Code.txt");

        List<String> adminCode = readFile(adminCodeFile);
        return adminCode.get(0);
    }
}
