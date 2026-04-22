import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class NotesAppGUI {

    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> noteList;
    private JTextArea textArea;
    private JTextField titleField;
    private List<Note> notes = new ArrayList<>();
    private static final String FILE_NAME = "notes.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NotesAppGUI().createAndShowGUI());
    }

    public NotesAppGUI() {
        loadNotes();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Мои заметки");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // === Список заметок слева ===
        listModel = new DefaultListModel<>();
        refreshListModel();

        noteList = new JList<>(listModel);
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.addListSelectionListener(e -> showSelectedNote());

        JScrollPane listScroll = new JScrollPane(noteList);
        listScroll.setPreferredSize(new Dimension(250, 0));

        // === Панель-обёртка с заголовком ===
        JPanel listPanel = new JPanel(new BorderLayout());

        JLabel listTitle = new JLabel("Список заметок", SwingConstants.CENTER);
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(new Color(44, 62, 80));
        listTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // отступы сверху/снизу

        listPanel.add(listTitle, BorderLayout.NORTH);
        listPanel.add(listScroll, BorderLayout.CENTER);

        // === Поле для заголовка заметки ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Заголовок:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);

        // === Поле для текста заметки ===
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane textScroll = new JScrollPane(textArea);

        // === Панель кнопок ===
        JButton newButton = new JButton("Новая заметка");
        JButton saveButton = new JButton("Сохранить");
        JButton deleteButton = new JButton("Удалить");

        newButton.addActionListener(e -> createNewNote());
        saveButton.addActionListener(e -> saveCurrentNote());
        deleteButton.addActionListener(e -> deleteSelectedNote());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        // === Главная раскладка ===
        // Передаём listPanel вместо listScroll
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, textScroll);
        splitPane.setDividerLocation(250);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);  // Добавили поле заголовка
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void refreshListModel() {
        listModel.clear();
        for (int i = 0; i < notes.size(); i++) {
            String title = notes.get(i).getTitle();
            if (title.trim().isEmpty()) {
                title = "Без названия";
            }
            listModel.addElement((i + 1) + ". " + title);
        }
    }

    private void showSelectedNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            Note note = notes.get(index);
            titleField.setText(note.getTitle());
            textArea.setText(note.getContent());
        }
    }

    private void createNewNote() {
        notes.add(new Note("", ""));
        refreshListModel();
        noteList.setSelectedIndex(notes.size() - 1);
        titleField.setText("");
        textArea.setText("");
        titleField.requestFocus();
    }

    private void saveCurrentNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            String title = titleField.getText().trim();
            String content = textArea.getText().trim();
            
            if (title.isEmpty()) {
                title = "Заметка " + (index + 1);
            }
            
            notes.set(index, new Note(title, content));
            refreshListModel();
            saveNotesToFile();
            JOptionPane.showMessageDialog(frame, "Заметка сохранена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Сначала выберите или создайте заметку", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Удалить эту заметку?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                notes.remove(index);
                refreshListModel();
                textArea.setText("");
                saveNotesToFile();
            }
        }
    }

    // ====================== Работа с файлом ======================
    private void saveNotesToFile() {
        try {
            List<String> lines = new ArrayList<>();
            for (Note note : notes) {
                lines.add(note.toFileString());
            }
            Files.write(Path.of(FILE_NAME), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка сохранения файла: " + e.getMessage(), 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNotes() {
        try {
            Path path = Path.of(FILE_NAME);
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (String line : lines) {
                    notes.add(Note.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }
}