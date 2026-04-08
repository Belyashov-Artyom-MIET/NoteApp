# NotesApp

Приложение для хранения заметок на Java Swing.

## Технологический стек
- Java SE (Red Hat Build of OpenJDK)
- Swing (javax.swing) для GUI
- java.nio.file для работы с файлами
- Стандартная библиотека Java (без внешних зависимостей)

## Структура проекта
src/
├── App.java # Точка входа

├── NotesApp.java # Основная логика

└── NotesAppGUI.java # Графический интерфейс


## Как запустить

### Через VS Code:
1. Откройте папку проекта в VS Code
2. Откройте `src/App.java`
3. Нажмите `Run` (F5)

### Через терминал:
```bash
# Компиляция
javac -d bin src/*.java

# Запуск
java -cp bin App

## Хранение данных
Заметки сохраняются в файл notes.txt в корневой папке проекта.

