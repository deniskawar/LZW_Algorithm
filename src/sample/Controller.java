package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static sample.ImageUtil.convertRGBAToGIF;
import static sample.ImageUtil.saveAnimatedGIF;


public class Controller {
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private ImageView basicGIF;
    @FXML
    private Button chooseGIFButton;
    @FXML
    private Button chooseDirButton;
    @FXML
    private Button splitIntoFramesButton;
    @FXML
    private TextArea helpTextArea;
    @FXML
    private TextField chooseGIFText;
    @FXML
    private TextField chooseDirText;
    @FXML
    private ScrollPane scrollPaneFrames;
    @FXML
    private VBox vBoxFrames;
    @FXML
    private TextArea textArea;
    @FXML
    private Button codeFramesButton;
    @FXML
    private Button clearDirButton;
    @FXML
    private Button compressButton;
    @FXML
    private Button decompressButton;
    @FXML
    private Button decodeFramesButton;
    @FXML
    private Button putFramesTogetherButton;

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private int framesAmount = 0;
    private List<File> frames = new ArrayList<>();
    private List<List<String>> pixels = new ArrayList<>();


    @FXML
    public void initialize() {
        textArea.setWrapText(true);
        textArea.setEditable(false);
        splitIntoFramesButton.setDisable(true);
        codeFramesButton.setDisable(true);
        compressButton.setDisable(true);
        decompressButton.setDisable(true);
        decodeFramesButton.setDisable(true);
        putFramesTogetherButton.setDisable(true);
        //chooseGIFText.setText("C:\\Users\\denis\\OneDrive\\Рабочий стол\\image_862607172222425924858.gif");
        //chooseDirText.setText("C:\\Users\\denis\\OneDrive\\Рабочий стол\\GIF");

    }

    @FXML
    public void clickOnChooseGIF() {
        fileChooser.setTitle("Choose a picture");
        Helper.configureFileChoose(fileChooser);
        File file = fileChooser.showOpenDialog(chooseGIFButton.getScene().getWindow());
        if (file != null) {
            basicGIF.setImage(new Image(file.toURI().toString()));
            chooseGIFText.setText(file.getAbsolutePath());
        }
    }
    @FXML
    public void clickOnChooseDir() {
        directoryChooser.setTitle("Choose a directory for frames saving");
        File file = directoryChooser.showDialog(chooseDirButton.getScene().getWindow());
        if (file != null) {
            chooseDirText.setText(file.getAbsolutePath());
            /*Helper.deleteAllFilesInDirectory(file, ".gif");
            Helper.deleteAllFilesInDirectory(file, ".txt");*/
        }
    }
    @FXML
    public void clickOnClearDir() {
        if (!chooseGIFText.getText().equals("") && !chooseDirText.getText().equals("")) {
            File file = new File(chooseDirText.getText());
            Helper.deleteAllFilesInDirectory(file, ".gif");
            Helper.deleteAllFilesInDirectory(file, ".txt");
            splitIntoFramesButton.setDisable(false);
            chooseGIFButton.setDisable(true);
            chooseDirButton.setDisable(true);
            clearDirButton.setDisable(true);

            textArea.setText(textArea.getText() + "> Папка: " + chooseDirText.getText() + " успешно очищена от кэша\n");
        }
    }
    @FXML
    private void clickOnSplitIntoFrames() {
        if ((!chooseGIFText.getText().equals("")) && (!chooseDirText.getText().equals(""))) {
            File folder = new File(chooseDirText.getText() + "\\FRAMES_#lzw");
            if (!folder.exists()) folder.mkdir();
            splitGIFtoFrames(chooseGIFText.getText(), folder.getAbsolutePath());
            splitIntoFramesButton.setDisable(true);
            codeFramesButton.setDisable(false);


            textArea.setText(textArea.getText() + "> Гиф успешно разделена на кадры! Кадры Гиф находятся в папке: "
                    + chooseDirText.getText() + "\n");
        }

    }
    @FXML
    private void clickOnEncodeFrames() {
        if (splitIntoFramesButton.isDisabled()) {
            for (int i = 0; i < frames.size(); i++) {
                pixels = new ArrayList<>();
                encodeSingleFrame(frames.get(i), i);
            }


            codeFramesButton.setDisable(true);
            compressButton.setDisable(false);
            textArea.setText(textArea.getText() + "> Цвета каждого пикселя каждого кадра гиф успешно получены! " +
                    "Файлы с описанием пикселей каждого кадра находятся в папке: " + chooseDirText.getText()
                    + "\\COLORS_OF_PIXELS_#lzw. Полное закодированное символьное представление каждого кадра нах" +
                    "одится в папке: "+ chooseDirText.getText() + "\\ENCODED_GIF_#lzw" + "\n");
        }
    }
    @FXML
    private void clickOnCompress() {
        File compressedDir = new File(chooseDirText.getText() + "\\COMPRESSED_GIF_#lzw\\");
        File decompressedDir = new File(chooseDirText.getText() + "\\DECOMPRESSED_GIF_#lzw\\");
        compressedDir.mkdir();
        decompressedDir.mkdir();
        for (int i = 0; i < frames.size(); i++) {
            File inputDoc = new File(chooseDirText.getText() + "\\ENCODED_GIF_#lzw\\" + i + "_#lzw_ENCODED_GIF.txt");
            File compressedDoc = new File(chooseDirText.getText() + "\\COMPRESSED_GIF_#lzw\\" + i + "_#lzw_COMPRESSED_GIF.txt");
            File decompressedDoc = new File(chooseDirText.getText() + "\\DECOMPRESSED_GIF_#lzw\\" + i + "_#lzw_DECOMPRESSED_GIF.txt");
            try {
                compressedDoc.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            LZW lzw = new LZW(inputDoc, compressedDoc, decompressedDoc);
            lzw.compress();
        }
        compressButton.setDisable(true);
        decompressButton.setDisable(false);
        textArea.setText(textArea.getText() + "> GIF-изображение успешно сжато! " +
                "Файлы с сжатыми кадрами находятся в папке: " + chooseDirText.getText()
                + "\\COMPRESSED_GIF_#lzw" + "\n");
    }
    @FXML
    private void clickOnDecompress() {
        for (int i = 0; i < frames.size(); i++) {
            File inputDoc = new File(chooseDirText.getText() + "\\ENCODED_GIF_#lzw\\" + i + "_#lzw_ENCODED_GIF.txt");
            File compressedDoc = new File(chooseDirText.getText() + "\\COMPRESSED_GIF_#lzw\\" + i + "_#lzw_COMPRESSED_GIF.txt");
            File decompressedDoc = new File(chooseDirText.getText() + "\\DECOMPRESSED_GIF_#lzw\\" + i + "_#lzw_DECOMPRESSED_GIF.txt");
            try {
                decompressedDoc.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            LZW lzw = new LZW(inputDoc, compressedDoc, decompressedDoc);
            lzw.decompress();
        }
        decompressButton.setDisable(true);
        decodeFramesButton.setDisable(false);
        textArea.setText(textArea.getText() + "> Текстовые файлы успешно восстановлены! " +
                "Восстановленные файлы с закодированными кадрами находятся в папке: " + chooseDirText.getText()
                + "\\DECOMPRESSED_GIF_#lzw" + "\n");
    }
    @FXML
    private void clickOnDecodeFrames() {
        File decodedDir = new File(chooseDirText.getText() + "\\DECODED_GIF_#lzw");
        decodedDir.mkdir();
        for (int i = 0; i < frames.size(); i++) {
            File frame = new File(chooseDirText.getText() + "\\DECODED_GIF_#lzw\\" + i + "_#lzw_DECODED_GIF.gif");
            File decompressedDoc = new File(chooseDirText.getText() + "\\DECOMPRESSED_GIF_#lzw\\" + i + "_#lzw_DECOMPRESSED_GIF.txt");
            encodeOneFrame(decompressedDoc, frame);
        }
        decodeFramesButton.setDisable(true);
        putFramesTogetherButton.setDisable(false);
        textArea.setText(textArea.getText() + "> Кадры GIF-изображения успешно восстановлены! " +
                "Восстановленные кадры находятся в папке: " + chooseDirText.getText()
                + "\\DECOMPRESSED_GIF_#lzw" + "\n");
    }
    @FXML
    private void clickOnPutFramesTogether() {
        File dir = new File(chooseDirText.getText() + "\\RESTORED_GIF_#lzw");
        dir.mkdir();
        File tmp = new File(chooseDirText.getText() + "\\RESTORED_GIF_#lzw\\RESTORED_GIF_#lzw.gif");
        /*try {
            tmp.createNewFile();
        } catch (Exception ex) {ex.printStackTrace();}
        try (ImageOutputStream output = new FileImageOutputStream(tmp)) {
            BufferedImage firstImage = ImageIO.read(new File(chooseDirText.getText() + "\\DECODED_GIF_#lzw\\" + "0" + "_#lzw_DECODED_GIF.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output,firstImage.getType(),1,true);
            writer.writeToSequence(firstImage);
            for (int i = 1; i < frames.size(); i++) {
                BufferedImage nextImage = ImageIO.read(new File(chooseDirText.getText() + "\\DECODED_GIF_#lzw\\" + i + "_#lzw_DECODED_GIF.gif"));
                writer.writeToSequence(nextImage);
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

        //
        List<GifFrame> gifFrames = new ArrayList<>();
        try (FileOutputStream output = new FileOutputStream(tmp)) {
            for (int i = 0; i < frames.size(); i++) {
                int transparantColor = 0xFF00FF;
                BufferedImage nextImage = ImageIO.read(new File(chooseDirText.getText() + "\\DECODED_GIF_#lzw\\" + i + "_#lzw_DECODED_GIF.gif"));
                BufferedImage gif = convertRGBAToGIF(nextImage, transparantColor);
                long delay = 100;
                String disposal = GifFrame.RESTORE_TO_BGCOLOR;
                gifFrames.add(new GifFrame(gif, delay, disposal));
            }
            int loopCount = 0; // loop indefinitely
            saveAnimatedGIF(output, gifFrames, loopCount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //


        putFramesTogetherButton.setDisable(true);
        textArea.setText(textArea.getText() + "> GIF-изображение успешно восстановлено! " +
                "Восстановленное GIF-изображение находится в папке: " + chooseDirText.getText()
                + "\\RESTORED_GIF_#lzw" + "\n");
    }

    private void encodeSingleFrame(File frame, int indexOfFrame) {
        divideFrameToPixels(frame);

        File folder = new File(chooseDirText.getText() + "\\COLORS_OF_PIXELS_#lzw");
        if (!folder.exists()) folder.mkdir();
        String[] addresses = frame.getAbsolutePath().split("\\\\");
        addresses[addresses.length-1] = addresses[addresses.length-1].replaceAll(".gif","_COLORS_OF_PIXELS.txt");
        File textFile = new File(folder.getAbsolutePath() + "\\" + addresses[addresses.length-1]);
        printPixelsOfFrameInTxt(textFile);
        //
        folder = new File(chooseDirText.getText() + "\\ENCODED_GIF_#lzw");
        if (!folder.exists()) folder.mkdir();
        printEncodedGIFToTxt(new File(folder + "\\" + indexOfFrame + "_#lzw_ENCODED_GIF.txt"));
    }

    private void printPixelsOfFrameInTxt (File textFile) {
        try (PrintWriter out = new PrintWriter(textFile.getAbsolutePath())){
            if (!textFile.exists()) {
                textFile.createNewFile();
            }
                for (int i = 0; i < pixels.size(); i++) {
                    for (int j = 0; j < pixels.get(i).size(); j++) {
                        out.println(i + "x" + j + " = " + pixels.get(i).get(j));
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printEncodedGIFToTxt (File textFile) {
        try (PrintWriter out = new PrintWriter(textFile.getAbsolutePath())){
            if (!textFile.exists()) {
                textFile.createNewFile();
            }

                for (int i = 0; i < pixels.size(); i++) {
                    for (int j = 0; j < pixels.get(i).size(); j++) {
                        out.print(pixels.get(i).get(j));
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void divideFrameToPixels(File frame) {
            Image image = new Image(frame.toURI().toString());
            PixelReader pixelReader = image.getPixelReader();
            for (int i = 0; i < image.getWidth(); i++) {
                pixels.add(new ArrayList<>());
                for (int j = 0; j < image.getHeight(); j++) {
                    int color = pixelReader.getArgb(i, j);
                    pixels.get(i).add(((String.valueOf(Integer.toHexString(color)).length() == 8) ?
                            String.valueOf(Integer.toHexString(color).substring(2)) : "000000"));
                }
            }

    }

    private void splitGIFtoFrames(String inputGIFPath, String outputPathFolder) {
        try (ImageInputStream ciis = ImageIO.createImageInputStream(new File(inputGIFPath))){
            String[] imageatt = new String[]{
                    "imageLeftPosition",
                    "imageTopPosition",
                    "imageWidth",
                    "imageHeight"
            };

            ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();

            reader.setInput(ciis, false);

            int noi = reader.getNumImages(true);
            BufferedImage master = null;

            for (int i = 0; i < noi; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);

                Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
                NodeList children = tree.getChildNodes();


                for (int j = 0; j < children.getLength(); j++) {
                    Node nodeItem = children.item(j);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        Map<String, Integer> imageAttr = new HashMap<>();

                        for (int k = 0; k < imageatt.length; k++) {
                            NamedNodeMap attr = nodeItem.getAttributes();
                            Node attnode = attr.getNamedItem(imageatt[k]);
                            imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
                        }
                        if (i == 0) {
                            master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
                        }
                        master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
                    }

                }
                File file = new File(outputPathFolder + "\\" + i + "_#lzw.gif");
                ImageIO.write(master, "GIF", file);

                Image tmpImage = new Image(file.toURI().toString());
                double width = tmpImage.getWidth();
                double height = tmpImage.getHeight();
                frames.add(file);
                ImageView imageView = new ImageView(tmpImage);
                imageView.setFitWidth(vBoxFrames.getPrefWidth());
                imageView.setFitHeight((height * imageView.getFitWidth()) / width);

                vBoxFrames.getChildren().add(imageView);
                framesAmount++;
            }
            if (noi == 0) {
                framesAmount = 1;
                File file = new File(outputPathFolder + "\\" + 0 + "_#lzw.gif");

                frames.add(file);
                Image tmpImage = new Image(new File(inputGIFPath).toURI().toString());
                ImageIO.write(ImageIO.read(new File(inputGIFPath)), "GIF", file);
                double width = tmpImage.getWidth();
                double height = tmpImage.getHeight();

                ImageView imageView = new ImageView(tmpImage);
                imageView.setFitWidth(vBoxFrames.getPrefWidth());
                imageView.setFitHeight((height * imageView.getFitWidth()) / width);

                vBoxFrames.getChildren().add(imageView);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void encodeOneFrame(File decompressedDoc, File frame) {
        BufferedImage img = new BufferedImage(pixels.size(), pixels.get(0).size(), BufferedImage.TYPE_INT_RGB);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(decompressedDoc))){
            int symbol = bufferedReader.read();
            int sixSymbols = 0;
            int width = 0;
            int height = 0;
            StringBuilder pixel = new StringBuilder();
            while (symbol != -1) {
                if (sixSymbols == pixels.get(0).size() * 6) {
                    img.setRGB(height, width, Integer.parseInt((pixel.toString().equals("000000") ? "FFFFFF" : pixel.toString()), 16));
                    sixSymbols = 0;
                    width = 0;
                    height++;
                    pixel = new StringBuilder();
                } else if (sixSymbols % 6 == 0 && sixSymbols != 0) {
                    img.setRGB(height, width, Integer.parseInt((pixel.toString().equals("000000") ? "FFFFFF" : pixel.toString()), 16));
                    pixel = new StringBuilder();
                    width++;
                }
                pixel.append((char) symbol);
                symbol = bufferedReader.read();
                sixSymbols++;
            }
            ImageIO.write(img, "gif",frame);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



}


