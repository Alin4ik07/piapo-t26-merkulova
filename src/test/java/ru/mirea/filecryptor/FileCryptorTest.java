package ru.mirea.filecryptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mirea.filecryptor.crypto.AesStrategy;
import ru.mirea.filecryptor.crypto.EncryptionStrategy;
import ru.mirea.filecryptor.facade.FileCryptorFacade;
import ru.mirea.filecryptor.model.CryptoTask;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileCryptorTest {

    private AesStrategy aesStrategy;
    private FileCryptorFacade facade;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        aesStrategy = new AesStrategy();
        facade = new FileCryptorFacade(aesStrategy);
    }

    private CryptoTask createTask(Path in, Path out, String pass) {
        return new CryptoTask.Builder()
                .setInputPath(in.toString())
                .setOutputPath(out.toString())
                .setPassword(pass)
                .build();
    }

    // --- Блок 1: Тесты стратегии шифрования AesStrategy (14 тестов) ---

    @Test
    void test01_EncryptAndDecryptBasicText() throws Exception {
        byte[] data = "Hello, MIREA!".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "secret123");
        assertArrayEquals(data, aesStrategy.decrypt(encrypted, "secret123"));
    }

    @Test
    void test02_EncryptedDataDiffersFromOriginal() throws Exception {
        byte[] data = "Sensitive information".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "pass");
        assertNotEquals(new String(data, StandardCharsets.UTF_8), new String(encrypted, StandardCharsets.ISO_8859_1));
    }

    @Test
    void test03_EncryptAndDecryptCyrillicText() throws Exception {
        byte[] data = "Секретный текст на русском языке 12345".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "парольКириллица");
        assertArrayEquals(data, aesStrategy.decrypt(encrypted, "парольКириллица"));
    }

    @Test
    void test04_EncryptAndDecryptEmptyArray() throws Exception {
        byte[] empty = new byte[0];
        byte[] encrypted = aesStrategy.encrypt(empty, "password");
        assertArrayEquals(empty, aesStrategy.decrypt(encrypted, "password"));
    }

    @Test
    void test05_EncryptAndDecryptSingleByte() throws Exception {
        byte[] single = new byte[]{42};
        byte[] encrypted = aesStrategy.encrypt(single, "key");
        assertArrayEquals(single, aesStrategy.decrypt(encrypted, "key"));
    }

    @Test
    void test06_EncryptAndDecryptBlockSize16Bytes() throws Exception {
        byte[] exactBlock = "1234567890123456".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(exactBlock, "blockPass");
        assertArrayEquals(exactBlock, aesStrategy.decrypt(encrypted, "blockPass"));
    }

    @Test
    void test07_EncryptAndDecryptLargeData512KB() throws Exception {
        byte[] largeData = new byte[1024 * 512];
        new Random(123).nextBytes(largeData);
        byte[] encrypted = aesStrategy.encrypt(largeData, "strongPassword!@#");
        assertArrayEquals(largeData, aesStrategy.decrypt(encrypted, "strongPassword!@#"));
    }

    @Test
    void test08_EncryptAndDecryptSpecialCharactersPassword() throws Exception {
        byte[] data = "Test payload".getBytes(StandardCharsets.UTF_8);
        String complexPass = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        byte[] encrypted = aesStrategy.encrypt(data, complexPass);
        assertArrayEquals(data, aesStrategy.decrypt(encrypted, complexPass));
    }

    @Test
    void test09_EncryptAndDecryptVeryLongPassword() throws Exception {
        byte[] data = "Payload".getBytes(StandardCharsets.UTF_8);
        String longPass = "a".repeat(500);
        byte[] encrypted = aesStrategy.encrypt(data, longPass);
        assertArrayEquals(data, aesStrategy.decrypt(encrypted, longPass));
    }

    @Test
    void test10_EncryptAndDecryptWithEmptyPassword() throws Exception {
        byte[] data = "Some data".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "");
        assertArrayEquals(data, aesStrategy.decrypt(encrypted, ""));
    }

    @Test
    void test11_DecryptWithWrongPasswordThrowsException() throws Exception {
        byte[] data = "Secret message".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "correctPassword");
        assertThrows(Exception.class, () -> aesStrategy.decrypt(encrypted, "wrongPassword"));
    }

    @Test
    void test12_DecryptCorruptedDataThrowsException() throws Exception {
        byte[] data = "Valid data".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "password");
        encrypted[encrypted.length - 1] ^= 0xFF;
        assertThrows(Exception.class, () -> aesStrategy.decrypt(encrypted, "password"));
    }

    @Test
    void test13_DecryptTruncatedDataThrowsException() throws Exception {
        byte[] data = "Valid data for truncation".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesStrategy.encrypt(data, "password");
        byte[] truncated = new byte[encrypted.length - 3];
        System.arraycopy(encrypted, 0, truncated, 0, truncated.length);
        assertThrows(Exception.class, () -> aesStrategy.decrypt(truncated, "password"));
    }

    @Test
    void test14_DifferentPasswordsProduceDifferentCiphertexts() throws Exception {
        byte[] data = "Same plaintext".getBytes(StandardCharsets.UTF_8);
        byte[] enc1 = aesStrategy.encrypt(data, "password1");
        byte[] enc2 = aesStrategy.encrypt(data, "password2");
        assertFalse(Arrays.equals(enc1, enc2));
    }

    // --- Блок 2: Тесты модели и строителя CryptoTask.Builder (8 тестов) ---

    @Test
    void test15_BuilderSetsInputAndOutputPaths() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("input.txt")
                .setOutputPath("output.enc")
                .setPassword("123")
                .build();
        assertEquals("input.txt", task.getInputPath());
        assertEquals("output.enc", task.getOutputPath());
    }

    @Test
    void test16_BuilderSetsPasswordCorrectly() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("mySecretPass")
                .build();
        assertEquals("mySecretPass", task.getPassword());
    }

    @Test
    void test17_BuilderDefaultDeleteOriginalIsFalse() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("pass")
                .build();
        assertFalse(task.isDeleteOriginal());
    }

    @Test
    void test18_BuilderSetsDeleteOriginalTrue() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("pass")
                .setDeleteOriginal(true)
                .build();
        assertTrue(task.isDeleteOriginal());
    }

    @Test
    void test19_BuilderSetsDeleteOriginalFalseExplicitly() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("pass")
                .setDeleteOriginal(false)
                .build();
        assertFalse(task.isDeleteOriginal());
    }

    @Test
    void test20_BuilderAllowsChainingInAnyOrder() {
        CryptoTask task = new CryptoTask.Builder()
                .setDeleteOriginal(true)
                .setPassword("pass123")
                .setOutputPath("out.txt")
                .setInputPath("in.txt")
                .build();
        assertTrue(task.isDeleteOriginal());
        assertEquals("pass123", task.getPassword());
        assertEquals("in.txt", task.getInputPath());
        assertEquals("out.txt", task.getOutputPath());
    }

    @Test
    void test21_BuilderCreatesDistinctTaskInstances() {
        CryptoTask.Builder builder = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("123");
        CryptoTask task1 = builder.build();
        CryptoTask task2 = builder.build();
        assertNotSame(task1, task2);
    }

    @Test
    void test22_BuilderOverwritesPasswordWhenCalledTwice() {
        CryptoTask task = new CryptoTask.Builder()
                .setInputPath("a")
                .setOutputPath("b")
                .setPassword("oldPass")
                .setPassword("newPass")
                .build();
        assertEquals("newPass", task.getPassword());
    }

    // --- Блок 3: Тесты фасада FileCryptorFacade и работы с файлами (13 тестов) ---

    @Test
    void test23_EncryptCreatesOutputFile() throws Exception {
        Path src = tempDir.resolve("test.txt");
        Path enc = tempDir.resolve("test.txt.enc");
        Files.writeString(src, "Hello World");

        facade.processEncryption(createTask(src, enc, "pass"));
        assertTrue(Files.exists(enc));
    }

    @Test
    void test24_FullEncryptionAndDecryptionCycle() throws Exception {
        Path src = tempDir.resolve("secret.txt");
        Path enc = tempDir.resolve("secret.txt.enc");
        Path dec = tempDir.resolve("restored.txt");
        String content = "Секретные данные для проверки фасада!";
        Files.writeString(src, content, StandardCharsets.UTF_8);

        facade.processEncryption(createTask(src, enc, "12345"));
        facade.processDecryption(createTask(enc, dec, "12345"));

        assertEquals(content, Files.readString(dec, StandardCharsets.UTF_8));
    }

        @Test
    void test25_DeleteOriginalTrueRemovesSourceAfterEncryption() throws Exception {
        Path src = tempDir.resolve("delete_me.txt");
        Path enc = tempDir.resolve("delete_me.enc");
        Files.writeString(src, "Temporary data");

        CryptoTask task = new CryptoTask.Builder()
                .setInputPath(src.toString())
                .setOutputPath(enc.toString())
                .setPassword("pass")
                .setDeleteOriginal(true)
                .build();
        facade.processEncryption(task);

        assertFalse(Files.exists(src));
        assertTrue(Files.exists(enc));
    }

    @Test
    void test26_DeleteOriginalFalseKeepsSourceAfterEncryption() throws Exception {
        Path src = tempDir.resolve("keep_me.txt");
        Path enc = tempDir.resolve("keep_me.enc");
        Files.writeString(src, "Keep this file");

        CryptoTask task = new CryptoTask.Builder()
                .setInputPath(src.toString())
                .setOutputPath(enc.toString())
                .setPassword("pass")
                .setDeleteOriginal(false)
                .build();
        facade.processEncryption(task);

        assertTrue(Files.exists(src));
    }

    @Test
    void test27_DeleteOriginalTrueRemovesEncryptedFileAfterDecryption() throws Exception {
        Path src = tempDir.resolve("orig.txt");
        Path enc = tempDir.resolve("orig.enc");
        Path dec = tempDir.resolve("dec.txt");
        Files.writeString(src, "Data");

        facade.processEncryption(createTask(src, enc, "pass"));
        CryptoTask decTask = new CryptoTask.Builder()
                .setInputPath(enc.toString())
                .setOutputPath(dec.toString())
                .setPassword("pass")
                .setDeleteOriginal(true)
                .build();
        facade.processDecryption(decTask);

        assertFalse(Files.exists(enc));
        assertTrue(Files.exists(dec));
    }

    @Test
    void test28_EncryptAndDecryptEmptyFile() throws Exception {
        Path src = tempDir.resolve("empty.txt");
        Path enc = tempDir.resolve("empty.enc");
        Path dec = tempDir.resolve("empty_dec.txt");
        Files.createFile(src);

        facade.processEncryption(createTask(src, enc, "pass"));
        facade.processDecryption(createTask(enc, dec, "pass"));

        assertEquals(0, Files.size(dec));
    }

    @Test
    void test29_EncryptAndDecryptBinaryFile() throws Exception {
        Path src = tempDir.resolve("binary.bin");
        Path enc = tempDir.resolve("binary.enc");
        Path dec = tempDir.resolve("binary_dec.bin");
        byte[] binaryData = new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFF};
        Files.write(src, binaryData);

        facade.processEncryption(createTask(src, enc, "binPass"));
        facade.processDecryption(createTask(enc, dec, "binPass"));

        assertArrayEquals(binaryData, Files.readAllBytes(dec));
    }

    @Test
    void test30_EncryptNonExistentFileThrowsException() {
        Path missing = tempDir.resolve("does_not_exist.txt");
        Path enc = tempDir.resolve("out.enc");

        assertThrows(Exception.class, () -> facade.processEncryption(createTask(missing, enc, "pass")));
    }

    @Test
    void test31_DecryptNonExistentFileThrowsException() {
        Path missing = tempDir.resolve("missing.enc");
        Path dec = tempDir.resolve("out.txt");

        assertThrows(Exception.class, () -> facade.processDecryption(createTask(missing, dec, "pass")));
    }

    @Test
    void test32_DecryptWithWrongPasswordThrowsException() throws Exception {
        Path src = tempDir.resolve("data.txt");
        Path enc = tempDir.resolve("data.enc");
        Path dec = tempDir.resolve("data_dec.txt");
        Files.writeString(src, "Secret");

        facade.processEncryption(createTask(src, enc, "right"));
        assertThrows(Exception.class, () -> facade.processDecryption(createTask(enc, dec, "wrong")));
    }

    @Test
    void test33_SetStrategySwitchesAlgorithmDynamically() throws Exception {
        Path src = tempDir.resolve("custom.txt");
        Path enc = tempDir.resolve("custom.enc");
        Files.writeString(src, "ABC");

        EncryptionStrategy xorStrategy = new EncryptionStrategy() {
            @Override
            public byte[] encrypt(byte[] data, String password) {
                byte[] res = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    res[i] = (byte) (data[i] ^ 0x55);
                }
                return res;
            }

            @Override
            public byte[] decrypt(byte[] data, String password) {
                return encrypt(data, password);
            }
        };

        facade.setStrategy(xorStrategy);
        facade.processEncryption(createTask(src, enc, "any"));

        byte[] expected = new byte[]{'A' ^ 0x55, 'B' ^ 0x55, 'C' ^ 0x55};
        assertArrayEquals(expected, Files.readAllBytes(enc));
    }

    @Test
    void test34_FileWithSpacesInPath() throws Exception {
        Path subDir = tempDir.resolve("folder with spaces");
        Files.createDirectories(subDir);
        Path src = subDir.resolve("my secret file.txt");
        Path enc = subDir.resolve("my secret file.enc");
        Path dec = subDir.resolve("restored file.txt");
        Files.writeString(src, "Spaces test");

        facade.processEncryption(createTask(src, enc, "pass"));
        facade.processDecryption(createTask(enc, dec, "pass"));

        assertEquals("Spaces test", Files.readString(dec));
    }

    @Test
    void test35_MultipleSequentialTasksWithSameFacade() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Path src = tempDir.resolve("file" + i + ".txt");
            Path enc = tempDir.resolve("file" + i + ".enc");
            Path dec = tempDir.resolve("dec" + i + ".txt");
            Files.writeString(src, "Content #" + i);

            facade.processEncryption(createTask(src, enc, "p" + i));
            facade.processDecryption(createTask(enc, dec, "p" + i));
            assertEquals("Content #" + i, Files.readString(dec));
        }
    }
}
