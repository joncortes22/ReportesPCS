package org.example;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.*;
import java.util.HashMap;

public class Folder extends Thread{
    private File path;

    public Folder(File path) {
        this.path = path;
    }

    public void getFileInfo(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);

        String pattern = "reporte_(\\d{2}_\\d{2}_\\d{4})\\.txt";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(file.getName());
        String fecha = "";
        matcher.matches();
        if (matcher.matches()){fecha = matcher.group(1);}
        fecha = fecha.replace("_", "-");

        String nombre = "";
        String[] hallazgos = new String[3];
        while (reader.hasNextLine()){
            String data = reader.nextLine();
            if (data.startsWith("# Topico del Reporte")){
                nombre = reader.nextLine();
                nombre = nombre.replace("- ", "");
            } else if (data.startsWith("# Hallazgos")) {
                hallazgos[0] = reader.nextLine();
                hallazgos[1] = reader.nextLine();
                hallazgos[2] = reader.nextLine();

                HashMap<String, String> newReport = new HashMap<>();
                newReport.put("fecha", fecha);
                newReport.put("nombre", nombre);
                newReport.put("hallazgos", Arrays.toString(hallazgos));
                Main.reportHashMap.add(newReport);
                return;
            }
        }
    }

    public void checkFolder(File path) throws FileNotFoundException {
        File[] fileList = path.listFiles();
        assert fileList != null;
        String pattern = "reporte_\\d{2}_\\d{2}_\\d{4}.txt";
        Pattern compiledPattern = Pattern.compile(pattern);

        for (File file : fileList) {
            if (file.isDirectory()) {
                checkFolder(file);
            } else {
                Matcher matcher = compiledPattern.matcher(file.getName());
                if (matcher.matches()){
                    System.out.println("Report found: " + file.getName());
                    getFileInfo(file);
                }
            }
        }

    }
    @Override
    public void run() {
        try {
            checkFolder(this.path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
