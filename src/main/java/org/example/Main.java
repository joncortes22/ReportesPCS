package org.example;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;


public class Main {
    public static List<HashMap<String, String>> reportHashMap = Collections.synchronizedList(new ArrayList<>());

    public static void ordenarReportes() throws ParseException {
        for (int i = 0; i<=reportHashMap.size()-1;i++){
            for (int x = 0; x <= reportHashMap.size()-1;x++){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date fecha1 = formatter.parse((String) reportHashMap.get(i).get("fecha"));
                Date fecha2 = formatter.parse((String) reportHashMap.get(x).get("fecha"));
                if (fecha2.compareTo(fecha1) > 0){
                    HashMap<String, String> temp = reportHashMap.get(i);
                    reportHashMap.set(i, reportHashMap.get(x));
                    reportHashMap.set(x, temp);
                }
            }
        }
    }

    public static void generarReporteFinal() throws IOException, ParseException {
        File reporteFinal = new File("reporte_hallazgos.txt");
        reporteFinal.createNewFile();
        if (reporteFinal.createNewFile()){System.out.println("Final report created:" + reporteFinal.getName());}
        else {System.out.println("Final report file replaced: " + reporteFinal.getName());}

        StringBuilder dataReporte = new StringBuilder();
        String currentMonth = "";
        for (HashMap reporte : reportHashMap){
            String fecha = (String) reporte.get("fecha");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.forLanguageTag("es"));

            Date date = dateFormat.parse(fecha);
            SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es"));
            String mes = formatoMes.format(date);
            currentMonth = currentMonth.toLowerCase();
            if (!mes.equals(currentMonth)){
                currentMonth = mes;
                currentMonth = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1);
                dataReporte.append(currentMonth).append("\n");
            }

            dataReporte.append("# Reporte del ").append(reporte.get("fecha")).append("\n");
            dataReporte.append("# ").append(reporte.get("nombre")).append("\n");
            String strHallazgos = (String) reporte.get("hallazgos");
            String[] hallazgos = strHallazgos.split(",");
            for (String hallazgo : hallazgos){
                hallazgo = hallazgo.replace("[", " ");
                hallazgo = hallazgo.replace("]", "");
                dataReporte.append(hallazgo).append("\n");
            }
            dataReporte.append("\n");
        }
        FileWriter writer = new FileWriter(reporteFinal);
        writer.write(String.valueOf(dataReporte));
        writer.close();
    }

    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        File mainFolder = new File("bitacora");
        File[] fileList = mainFolder.listFiles();
        assert fileList != null;

        int folderCount = fileList.length;
        Folder[] folderThreads = new Folder[folderCount];
        int counter = 0;
        for (File file:fileList){
            System.out.println("Searching through " + file.getName());
            folderThreads[counter] = new Folder(file);
            folderThreads[counter].start();
            counter++;
        }
        for (Folder folder : folderThreads){
            folder.join();
        }
        System.out.println("\nSearch completed.\n");
        ordenarReportes();
        generarReporteFinal();
    }
}