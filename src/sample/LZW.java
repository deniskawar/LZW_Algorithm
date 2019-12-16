package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LZW {
    private File inputDoc = null;
    private File compressedDoc = null;
    private File decompressedDoc = null;
    private List<String> dictionary = new ArrayList<>();

    public LZW(File inputDoc, File compressedDoc, File decompressedDoc) {
        this.inputDoc = inputDoc;
        this.compressedDoc = compressedDoc;
        this.decompressedDoc = decompressedDoc;
        dictionaryFilling(inputDoc);
    }

    public void dictionaryFilling(File txtDoc) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(txtDoc),"Cp1251"))){
            reader.mark(0);
            int symbol = reader.read();
            while (symbol != -1) {
                if (!dictionary.contains(String.valueOf((char) symbol))) dictionary.add(String.valueOf((char) symbol));
                symbol = reader.read();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void compress() {
       /* List<String> tmpDict = new ArrayList<>(dictionary);
        StringBuilder toFile = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputDoc)))) {
                    String p = String.valueOf((char) reader.read());
                    int c = reader.read();
                    while (c != -1) {
                        String pPlusC = p + "" + (char) c;
                        if (tmpDict.contains(pPlusC)) {
                            p = pPlusC;
                        } else {
                            String goal = String.valueOf(tmpDict.indexOf(p));
                            StringBuilder points = new StringBuilder();
                            for (int i = 1; i < goal.length(); i++) points.append('.');
                            *//*writer.write(points.append(goal).toString());*//* toFile.append(points.append(goal).toString());
                            tmpDict.add(pPlusC);
                            p = String.valueOf((char) c);
                        }
                        c = reader.read();
                    }
                    String goal = String.valueOf(tmpDict.indexOf(p));
                    StringBuilder points = new StringBuilder();
                    for (int i = 1; i < goal.length(); i++) points.append('.');
                    *//*writer.write(points.append(goal).toString());*//* toFile.append(points.append(goal).toString());

                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(compressedDoc)))) {
                        writer.write(toFile.toString());
                    }
                }
         catch (Exception ex) {
            ex.printStackTrace();
        }
*/
/*
        List<String> tmpDict = new ArrayList<>(dictionary);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputDoc)));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(compressedDoc)))) {
                    String p = String.valueOf((char) reader.read());
                    int c = reader.read();
                    while (c != -1) {
                        String pPlusC = p + "" + (char) c;
                        if (tmpDict.contains(pPlusC)) {
                            p = pPlusC;
                        } else {
                            String goal = String.valueOf(tmpDict.indexOf(p));
                            StringBuilder points = new StringBuilder();
                            for (int i = 1; i < goal.length(); i++) points.append('.');
                            writer.write(points.append(goal).toString());
                            tmpDict.add(pPlusC);
                            p = String.valueOf((char) c);
                        }
                        c = reader.read();
                    }
                    String goal = String.valueOf(tmpDict.indexOf(p));
                    StringBuilder points = new StringBuilder();
                    for (int i = 1; i < goal.length(); i++) points.append('.');
                    writer.write(points.append(goal).toString());
                }
         catch (Exception ex) {
            ex.printStackTrace();
        }*/


        List<String> tmpDict = new ArrayList<>(dictionary);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputDoc)));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(compressedDoc)))) {
                    String p = String.valueOf((char) reader.read());
                    int c = reader.read();
                    while (c != -1) {
                        String pPlusC = p + "" + (char) c;
                        if (tmpDict.contains(pPlusC)) {
                            p = pPlusC;
                        } else {
                            String goal = String.valueOf(tmpDict.indexOf(p));
                            for (int i = 1; i < goal.length(); i++) writer.write('.');
                            writer.write(goal);
                            tmpDict.add(pPlusC);
                            p = String.valueOf((char) c);
                        }
                        c = reader.read();
                    }
                    String goal = String.valueOf(tmpDict.indexOf(p));
                    for (int i = 1; i < goal.length(); i++) writer.write('.');
                    writer.write(goal);
                }
         catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public void decompress() {

            List<String> tmpDict = new ArrayList<>(dictionary);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(compressedDoc),"Cp1252"));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(decompressedDoc),"Cp1252"))) {
                    int old = Character.getNumericValue((char) reader.read());
                    writer.write(String.valueOf(tmpDict.get(old)));
                    int current = reader.read();
                    char c = tmpDict.get(old).charAt(0);
                    while (current != -1) {
                        String chNnew = String.valueOf((char) current);
                        int nnew = 0;
                        int pointsAmount = 0;
                        while (chNnew.equals(".")) {
                            pointsAmount++;
                            chNnew = String.valueOf((char) reader.read());
                        }
                        if (pointsAmount > 0) {
                            while (pointsAmount != -1) {
                                nnew = nnew + Integer.parseInt(chNnew) * (int) Math.pow(10, pointsAmount); // 10
                                current = reader.read();
                                chNnew = String.valueOf((char) current);
                                pointsAmount--;
                            }
                        } else nnew = Integer.parseInt(chNnew);
                        StringBuilder s = new StringBuilder();
                        if (nnew >= tmpDict.size()) {
                            s.append(tmpDict.get(old));
                            s.append(String.valueOf(c));
                        } else {
                            s.append(tmpDict.get(nnew));
                        }
                        writer.write(s.toString());
                        c = s.charAt(0);
                        tmpDict.add(tmpDict.get(old) + String.valueOf(c));
                        old = nnew;
                        if (pointsAmount == 0) current = reader.read();
                    }
                }
         catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
