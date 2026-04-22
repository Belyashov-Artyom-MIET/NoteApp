@echo off
chcp 65001 >nul

echo [1/3] Очищаю старые классы...
if exist bin rmdir /s /q bin
mkdir bin

echo [2/3] Компилирую все Java-файлы...
javac -encoding UTF-8 -d bin src\*.java

if errorlevel 1 (
    echo [ОШИБКА] Компиляция не удалась. Проверьте консоль выше.
    pause
    exit /b 1
)

echo [3/3] Запускаю приложение...
javaw -Dfile.encoding=UTF-8 -cp bin NotesAppGUI