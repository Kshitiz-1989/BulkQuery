package main.java;

import com.sforce.async.CSVReader;
import com.sforce.bulk.CsvWriter;
import java.io.*;
import java.util.*;
import java.io.IOException;


public class BundleCreator {
    public static void main(String[] args) throws IOException {
        BundleCreator obj = new BundleCreator();
        obj.createNewFolder();
    }

    public void createNewFolder() throws IOException {
        File mkdir = new File("Asset Bundles");
        System.out.println("Was new Asset Bundle folder created? "+mkdir.mkdirs());
        File outputdir = new File("Output");
        CSVReader rdr = new CSVReader(new InputStreamReader(new FileInputStream(outputdir.getAbsolutePath()+"/"+"Asset"+".csv")));
        List<String> header = rdr.nextRecord();
        int numCols = header.size();
        System.out.println("header-->"+header.toString());
        //List<String> nextLine = rdr.nextRecord();
        //System.out.println("nextLine-->"+nextLine.toString());
        List<String> row;
        while ((row = rdr.nextRecord()) != null) {
            Map<String, String> rowInfo = new HashMap<String, String>();
            for (int i = 0; i < numCols; i++) {
                if(i< row.size()) {
                    rowInfo.put(header.get(i), row.get(i));
                }else{
                    rowInfo.put(header.get(i), "");
                }
            }
            //File newFile = new File(outputdir.getAbsolutePath()+"/"+rowInfo.get("Id")+".csv");
            Writer csvFile = new FileWriter(mkdir.getAbsolutePath()+"/"+rowInfo.get("Id")+".csv");
            String[] headerArray = new String[numCols];
            for(int j = 0; j<numCols; j++){
                headerArray[j] = header.get(j);
            }
            CsvWriter writer = new CsvWriter(headerArray,csvFile);
            String[] rowArray = new String[row.size()];
            for(int k = 0; k<row.size(); k++){
                rowArray[k] = row.get(k);
            };
            writer.writeRecord(rowArray);
        }

    }
}

