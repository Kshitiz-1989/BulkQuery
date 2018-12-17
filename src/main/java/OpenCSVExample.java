package main.java;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;

import java.io.IOException;

public class OpenCSVExample {
    public static void main(String[] args) throws IOException {
        File outputdir = new File("Output");
        CSVReader reader = new CSVReader(new FileReader(outputdir.getAbsolutePath()+"/"+"Asset"+".csv"));
        String [] firstLine = reader.readNext();
        String [] nextLine = null;
        int c = 0;
        while ((nextLine = reader.readNext()) != null) {
            c++;
            System.out.println(nextLine[0] + ':'+ nextLine[1] + ':' + nextLine[2]);
            //System.out.println(nextLine[0] + ':'+ nextLine[1] + ':' + nextLine[2]+ ':' + nextLine[3] + ':'+ nextLine[4] + ':' + nextLine[5]+ ':' + nextLine[6] + ':'+ nextLine[7] + ':' + nextLine[8]+ ':' + nextLine[9] + ':'+ nextLine[10] + ':' + nextLine[11]+ ':' + nextLine[12] + ':'+ nextLine[13] + ':' + nextLine[14]+ ':' + nextLine[15] + ':'+ nextLine[16] + ':' + nextLine[17]+ ':' + nextLine[18]);
        }
        System.out.println(c);
        System.out.println(firstLine[0] + ':'+ firstLine[1] + ':' + firstLine[2]);
    }
}
