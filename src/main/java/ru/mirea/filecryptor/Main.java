package ru.mirea.filecryptor;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            String originalFile = "secret.txt";
            String encryptedFile = "secret.txt.enc";
            String restoredFile = "restored.txt";
            String password = "MySuperSecretPassword123";

            Files.writeString(Path.of(originalFile), "Секретные данные для лабораторной работы по РТУ МИРЭА!");
            System.out.println("1. Исходный файл создан: " + originalFile);

            FileCryptorFacade cryptor = new FileCryptorFacade(new AesStrategy());

            CryptoTask encryptTask = new CryptoTask.Builder()
                    .setInputPath(originalFile)
                    .setOutputPath(encryptedFile)
                    .setPassword(password)
                    .setDeleteOriginal(false)
                    .build();

            cryptor.processEncryption(encryptTask);
            System.out.println("2. Файл успешно зашифрован в: " + encryptedFile);

            CryptoTask decryptTask = new CryptoTask.Builder()
                    .setInputPath(encryptedFile)
                    .setOutputPath(restoredFile)
                    .setPassword(password)
                    .build();

            cryptor.processDecryption(decryptTask);
            System.out.println("3. Файл успешно расшифрован в: " + restoredFile);

            String resultText = Files.readString(Path.of(restoredFile));
            System.out.println("4. Содержимое после расшифровки: " + resultText);

        } catch (Exception e) {
            System.err.println("Ошибка при работе программы: " + e.getMessage());
            e.printStackTrace();
        }
    }
}