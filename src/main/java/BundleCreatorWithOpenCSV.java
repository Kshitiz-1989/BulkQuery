package main.java;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BundleCreatorWithOpenCSV {

    public static void main(String[] args) throws IOException {
        BundleCreatorWithOpenCSV obj = new BundleCreatorWithOpenCSV();
        obj.createNewFolder();
        obj.linkChildRecords();
        obj.renameFiles();
    }

    public void createNewFolder() throws IOException {
        try {
            File mkdir = new File("Asset Bundles");
            System.out.println("Was new Asset Bundle folder created? " + mkdir.mkdirs());
            File outputdir = new File("Output");
            Reader reader = Files.newBufferedReader(Paths.get(outputdir.getAbsolutePath() + "/" + "Asset" + ".csv"));
            CSVReader rdr = new CSVReader(reader);
            String[] headerArray = rdr.readNext();
            List<String> header = Arrays.asList(headerArray);
            System.out.println(headerArray[0]+":"+headerArray[1]);
            int numCols = header.size();
            System.out.println("header-->" + header.toString());
            //List<String> nextLine = rdr.nextRecord();
            //System.out.println("nextLine-->"+nextLine.toString());
            String[] rowArray = null;
            while ((rowArray = rdr.readNext()) != null) {
                List<String> row = Arrays.asList(rowArray);
                Map<String, String> rowInfo = new HashMap<String, String>();
                for (int i = 0; i < numCols; i++) {
                    if (i < row.size()) {
                        rowInfo.put(header.get(i), row.get(i));
                    } else {
                        rowInfo.put(header.get(i), "");
                    }
                }
                File newFile = new File(mkdir.getAbsolutePath() + "/" + rowInfo.get("Id") + ".csv");
                FileWriter csvFile = new FileWriter(newFile);
                CSVWriter csvWriter = new CSVWriter(csvFile);
                csvWriter.writeNext(headerArray);
                csvWriter.writeNext(rowArray);
                csvWriter.close();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void linkChildRecords() throws IOException  {
        File mkdir = new File("Asset Bundles");
        File outputdir = new File("Output");
        Reader reader = Files.newBufferedReader(Paths.get(outputdir.getAbsolutePath() + "/" + "childAsset" + ".csv"));
        CSVReader rdr = new CSVReader(reader);
        String[] headerArray = rdr.readNext();
        List<String> header = Arrays.asList(headerArray);
        int numCols = header.size();
        String[] rowArray = null;
        while ((rowArray = rdr.readNext()) != null) {
            List<String> row = Arrays.asList(rowArray);
            Map<String, String> rowInfo = new HashMap<String, String>();
            for (int i = 0; i < numCols; i++) {
                if (i < row.size()) {
                    rowInfo.put(header.get(i), row.get(i));
                } else {
                    rowInfo.put(header.get(i), "");
                }
            }
            //System.out.println(rowInfo.get("vlocity_cmt__RootItemId__c")+":"+rowInfo.get("Account.Name"));
            if( !rowInfo.get("vlocity_cmt__RootItemId__c").equals("")){
                File newFile = new File(mkdir.getAbsolutePath() + "/" + rowInfo.get("vlocity_cmt__RootItemId__c") + ".csv");
                FileWriter csvFile = new FileWriter(newFile,true);
                CSVWriter csvWriter = new CSVWriter(csvFile);
                csvWriter.writeNext(rowArray);
                csvWriter.close();
                csvFile.close();
                System.out.println(rowInfo.get("vlocity_cmt__RootItemId__c"));
            }
        }
    }
    public void renameFiles() throws IOException {
        File folder = new File("Asset Bundles");
        File[] listOfFiles = folder.listFiles();
        for(int i = 0; i < listOfFiles.length; i++){
            File f = new File(folder.getAbsolutePath() + "/" +listOfFiles[i].getName());
            f.renameTo(new File(folder.getAbsolutePath() + "/" + "Asset_Bundle_"+i+".csv"));
        }
    }
}

