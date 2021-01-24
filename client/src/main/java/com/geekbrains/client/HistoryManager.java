package com.geekbrains.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HistoryManager {

    private static final int COUNT_LAST_LINES = 100;

    public static void saveMessage(String login, String message) {
        try {
            Files.write(getHistoryFilePath(login), message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getHistoryFilePath(String login) {
        Path historyPath = Paths.get("history", "history_" + login + ".txt");
        if(Files.notExists(historyPath.getParent())) {
            createHistoryDirectory(historyPath);
        }

        return historyPath;
    }

    private static void createHistoryDirectory(Path path) {
        try {
            Files.createDirectories(path.getParent());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLastLinesOfHistory(String login) {
        Path historyFilePath = getHistoryFilePath(login);

        if(Files.notExists(historyFilePath)) return "";

        try {
            List<String> lines = Files.readAllLines(historyFilePath, StandardCharsets.UTF_8);
            return getLastLines(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String getLastLines(List<String> lines) {
        StringBuilder result = new StringBuilder();

        int startPos = 0;
        if(lines.size() > COUNT_LAST_LINES) {
            startPos = lines.size() - COUNT_LAST_LINES;
        }

        for (int i = startPos; i < lines.size(); i++) {
            result.append(lines.get(i)).append(System.lineSeparator());
        }

        return result.toString();
    }
}
