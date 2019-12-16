package sample;

import javafx.stage.FileChooser;

import java.io.File;

public class Helper {
    public static void configureFileChoose(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
    }

    public static void deleteAllFilesInDirectory(File directory, String formatName) {
        if (directory != null)
            for (File files: directory.listFiles()) {
                if (files.isDirectory() && files.getName().contains("#lzw")) {
                    deleteAllFilesInDirectory(files, formatName);
                    files.delete();
                }
                else if (files.getName().contains("#lzw") && (files.getAbsolutePath().contains(formatName)))
                    files.delete();
            }

    }
}
