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
    private List<String> notes = new ArrayList<>();
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
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, textScroll);
        splitPane.setDividerLocation(250);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void refreshListModel() {
        listModel.clear();
        for (int i = 0; i < notes.size(); i++) {
            listModel.addElement((i + 1) + ". " + shortenText(notes.get(i)));
        }
    }

    private String shortenText(String text) {
        if (text.length() > 50) {
            return text.substring(0, 47) + "...";
        }
        return text;
    }

    private void showSelectedNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            textArea.setText(notes.get(index));
        }
    }

    private void createNewNote() {
        notes.add("");
        refreshListModel();
        noteList.setSelectedIndex(notes.size() - 1);
        textArea.setText("");
        textArea.requestFocus();
    }

    private void saveCurrentNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            notes.set(index, textArea.getText().trim());
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
            Files.write(Path.of(FILE_NAME), notes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Ошибка сохранения файла: " + e.getMessage(), 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNotes() {
        try {
            Path path = Path.of(FILE_NAME);
            if (Files.exists(path)) {
                notes = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }
}