package main.java;

import java.io.*;
import java.util.*;
import com.sforce.async.*;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class BulkQueryClass {

    private BulkConnection bulkConnection = null;
    private static String object = "";
    private static String query = "";
    private String userId = "abanerjee@vlocity.com.vusrvo-000366"; //change to alter login
    private String passwd = "Training1";

    public static void main(String[] args)
            throws AsyncApiException, ConnectionException, IOException {
        //for loop to take in input from text file
        BulkQueryClass obj = new BulkQueryClass();
        InputHandler input = new InputHandler();
        ArrayList<String>[] jobList= input.getObjectsandQueries();
        for(int i = 0; i < jobList[0].size();i++){
            object = jobList[0].get(i);
            query = jobList[1].get(i);
            int numRecords = obj.doBulkQuery();
            if(numRecords == 0){
                //bulk api will also throw more extensive log on what was wrong
                System.out.println("Query was: "+query);
                System.out.println("Object was: "+object);
                throw new IllegalArgumentException("Malformed Query or no Data found. Please check console output.");
            }
            System.out.println(numRecords+" results returned.");
        }
    }

    public boolean login() {
        boolean success = false;
        //change from login to test to move to sandbox and last number is API version being used
        String soapAuthEndPoint = "https://login.salesforce.com/services/Soap/u/44.0";
        //first part is the header on the url of the org you are pointing this tool at
        String bulkAuthEndPoint = "https://ap4.salesforce.com/services/async/44.0";
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setUsername(userId);
            config.setPassword(passwd);
            config.setAuthEndpoint(soapAuthEndPoint);
            config.setCompression(true);
            config.setTraceFile("traceLogs.txt");
            config.setTraceMessage(false);
            config.setPrettyPrintXml(true);
            config.setRestEndpoint(bulkAuthEndPoint);
            System.out.println("AuthEndpoint: " +
                    config.getRestEndpoint());
            PartnerConnection connection = new PartnerConnection(config);
            System.out.println("SessionID: " + config.getSessionId());
            bulkConnection = new BulkConnection(config);
            success = true;

            //generates query from all objects, if query generation requirements change, uncomment and it will run
            //DescribeSObjectResult info = connection.describeSObject(object);
            //Field[] fields = info.getFields();
            //QueryHandler qh = new QueryHandler();
            //query = qh.generateQuery(fields, object);

        } catch (AsyncApiException aae) {
            aae.printStackTrace();
        } catch (ConnectionException ce) {
            ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        return success;
    }

    public int doBulkQuery() throws IOException{
        if ( ! login() ) {
            throw new IllegalArgumentException("Invalid Login");
        }
        try {
            JobInfo job = new JobInfo();
            job.setObject(object);
            job.setOperation(OperationEnum.query);
            job.setConcurrencyMode(ConcurrencyMode.Parallel);
            job.setContentType(ContentType.CSV);

            job = bulkConnection.createJob(job);
            assert job.getId() != null;

            job = bulkConnection.getJobStatus(job.getId());

            Calendar time = Calendar.getInstance();
            System.out.println("Query started on object "+object+" at time: "+time.get(Calendar.HOUR_OF_DAY)
                    + ":" + time.get(Calendar.MINUTE)+":"+time.getGreatestMinimum(Calendar.SECOND)+".");

            BatchInfo info = null;
            ByteArrayInputStream bout =
                    new ByteArrayInputStream(query.getBytes());
            info = bulkConnection.createBatchFromStream(job, bout);

            String[] queryResults = null;

            for(int i=0; i<10000; i++) {
                Thread.sleep(i==0 ? 30 * 1000 : 30 * 1000); //30 sec
                info = bulkConnection.getBatchInfo(job.getId(),
                        info.getId());

                if (info.getState() == BatchStateEnum.Completed) {
                    QueryResultList list =
                            bulkConnection.getQueryResultList(job.getId(),
                                    info.getId());
                    queryResults = list.getResult();
                    break;
                } else if (info.getState() == BatchStateEnum.Failed) {
                    System.out.println("-------------- failed ----------"
                            + info);
                    bulkConnection.closeJob(job.getId());
                    break;
                } else {
                    System.out.println("-------------- waiting ----------"
                            + info);
                }
            }

            if (queryResults != null) {
                for (String resultId : queryResults) {
                    //grabs result stream and passes it to csv writer
                    FileHandler.writeCSVFromStream(bulkConnection.getQueryResultStream(job.getId(),
                            info.getId(), resultId),object);
                    //grabs results to ensure integrity
                    bulkConnection.getQueryResultList(job.getId(), info.getId()).getResult();
                }
                //notify user of job complete
                System.out.println("Output complete on object "+object+" at time: "+time.get(Calendar.HOUR_OF_DAY)
                        + ":" + time.get(Calendar.MINUTE)+":"+time.getGreatestMinimum(Calendar.SECOND)+", processung results.");
                //return number of records complete for data check and close job
                int out = info.getNumberRecordsProcessed();
                bulkConnection.closeJob(job.getId());
                return out;
            }
        } catch (AsyncApiException aae) {
            aae.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        //something went wrong here, return 0 to catch an error back in main
        return 0;
    }
}