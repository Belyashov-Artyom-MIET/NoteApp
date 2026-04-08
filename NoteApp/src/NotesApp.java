import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class NotesApp {
    
    private static final String FILE_NAME = "notes.txt";
    
    public static void main(String[] args) {
    // Принудительно ставим UTF-8 везде
    System.setProperty("file.encoding", "UTF-8");
    
    try {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    } catch (Exception e) {
        e.printStackTrace();
    }

    List<String> notes = new ArrayList<>();
    // Новый способ чтения ввода — более стабильный для русского
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in, StandardCharsets.UTF_8)
    );

    loadNotes(notes);

    while (true) {
        System.out.println("\n=== МОИ ЗАМЕТКИ ===");
        System.out.println("1. Добавить заметку");
        System.out.println("2. Показать заметки");
        System.out.println("3. Удалить заметку");
        System.out.println("0. Выйти");
        System.out.print("Выбери действие: ");

        String choice;
        try {
            choice = reader.readLine().trim();
        } catch (IOException e) {
            System.out.println("Ошибка ввода");
            continue;
        }

        if (choice.equals("1")) {
            System.out.print("Напиши заметку: ");
            String newNote;
            try {
                newNote = reader.readLine().trim();
            } catch (IOException e) {
                System.out.println("Ошибка ввода");
                continue;
            }
            
            if (!newNote.isEmpty()) {
                notes.add(newNote);
                saveNotes(notes);
                System.out.println("✓ Заметка добавлена!");
            } else {
                System.out.println("Заметка пустая");
            }
            
        } else if (choice.equals("2")) {
            System.out.println("\n--- СПИСОК ЗАМЕТОК ---");
            if (notes.isEmpty()) {
                System.out.println("Нет заметок");
            } else {
                for (int i = 0; i < notes.size(); i++) {
                    System.out.println((i + 1) + ". " + notes.get(i));
                }
            }
            
        } else if (choice.equals("3")) {
            System.out.print("Номер заметки для удаления: ");
            try {
                String numStr = reader.readLine().trim();
                int number = Integer.parseInt(numStr);
                if (number > 0 && number <= notes.size()) {
                    notes.remove(number - 1);
                    saveNotes(notes);
                    System.out.println("✓ Заметка удалена!");
                } else {
                    System.out.println("✗ Нет такой заметки");
                }
            } catch (Exception e) {
                System.out.println("✗ Введи правильное число!");
            }
            
        } else if (choice.equals("0")) {
            System.out.println("Пока!");
            break;
            
        } else {
            System.out.println("✗ Неверный выбор");
        }
    }
    
    try {
        reader.close();
    } catch (IOException ignored) {}
}

    private static void saveNotes(List<String> notes) {
        try {
            Files.write(Path.of(FILE_NAME), notes, StandardCharsets.UTF_8);
            System.out.println("Сохранено " + notes.size() + " заметок");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private static void loadNotes(List<String> notes) {
        try {
            Path path = Path.of(FILE_NAME);
            if (Files.exists(path)) {
                List<String> loaded = Files.readAllLines(path, StandardCharsets.UTF_8);
                notes.addAll(loaded);
                if (!loaded.isEmpty()) {
                    System.out.println("✓ Загружено " + loaded.size() + " заметок");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }
}