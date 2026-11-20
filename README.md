# piapo-t26-merkulova

# FileCryptor — Система локального шифрования файлов

Кроссплатформенная утилита на Java 17+ для шифрования и дешифрования локальных файлов с использованием алгоритма **AES-256** и паттернов проектирования GoF (**Builder**, **Facade**, **Strategy**).

## Структура проекта
* `src/main/java/ru/mirea/filecryptor/` — исходный код приложения:
  * `crypto/EncryptionStrategy.java` и `crypto/AesStrategy.java` — поведенческий паттерн **Стратегия (Strategy)**;
  * `model/CryptoTask.java` — порождающий паттерн **Строитель (Builder)**;
  * `facade/FileCryptorFacade.java` — структурный паттерн **Фасад (Facade)**;
  * `Main.java` — точка входа в приложение.
* `src/main/resources/logback.xml` — конфигурация системы логирования **Logback**.
* `src/test/java/ru/mirea/filecryptor/FileCryptorTest.java` — модульные тесты JUnit 5.
* `pom.xml` — конфигурация системы сборки **Apache Maven**.

## Требования для сборки
* JDK 17 или выше
* Apache Maven 3.8+

## Инструкция по сборке и запуску

1. **Сборка проекта и запуск модульных тестов:**
   ```bash
   mvn clean package
   ```
   После успешной сборки исполняемый файл появится по пути `target/filecryptor-1.0.0.jar`.

2. **Запуск скомпилированного приложения:**
   ```bash
   java -Dfile.encoding=UTF-8 -jar target/filecryptor-1.0.0.jar
   ```

## Использованные зависимости и плагины
1. **SLF4J / Logback (`ch.qos.logback:logback-classic`)** — библиотека логирования хода выполнения операций шифрования и дешифрования.
2. **JUnit Jupiter (`org.junit.jupiter:junit-jupiter:5.10.2`)** — фреймворк модульного тестирования для автоматической проверки цикла шифрования и дешифрования при сборке.
3. **Maven Surefire Plugin (`3.2.5`)** — плагин автоматического запуска тестов на фазе `test`.
4. **Maven JAR Plugin (`3.3.0`)** — плагин упаковки проекта в исполняемый JAR-архив с указанием точки входа `Main-Class`.
