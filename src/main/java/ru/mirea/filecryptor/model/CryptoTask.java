package ru.mirea.filecryptor.model;

public class CryptoTask {
    private final String inputPath;
    private final String outputPath;
    private final String password;
    private final boolean deleteOriginal;

    private CryptoTask(Builder builder) {
        this.inputPath = builder.inputPath;
        this.outputPath = builder.outputPath;
        this.password = builder.password;
        this.deleteOriginal = builder.deleteOriginal;
    }

    public String getInputPath() { return inputPath; }
    public String getOutputPath() { return outputPath; }
    public String getPassword() { return password; }
    public boolean isDeleteOriginal() { return deleteOriginal; }

    public static class Builder {
        private String inputPath;
        private String outputPath;
        private String password;
        private boolean deleteOriginal = false;

        public Builder setInputPath(String inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public Builder setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setDeleteOriginal(boolean deleteOriginal) {
            this.deleteOriginal = deleteOriginal;
            return this;
        }

        public CryptoTask build() {
            return new CryptoTask(this);
        }
    }
}
