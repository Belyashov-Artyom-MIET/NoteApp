public class Note {
    private String title;
    private String content;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Для сохранения в файл (формат: заголовок|||содержимое)
    public String toFileString() {
        return title + "|||" + content;
    }

    // Для чтения из файла
    public static Note fromFileString(String line) {
        String[] parts = line.split("\\|\\|\\|", 2);
        if (parts.length == 2) {
            return new Note(parts[0], parts[1]);
        } else {
            return new Note("Без названия", line);
        }
    }
}