import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.lang3.ObjectUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.util.Scanner;

public class dataProcess {
	
	// method that create the conclusion csv files 
	public static String createCSV(String party) {
		
		// csv file directory
		String csvFilePath = "E:\\01_NYU\\2023\\2023 Spring Research\\Politics Project data collection\\"+party+" Candidates' Index.csv";
		
		try {
			// create the csv writer
			  FileWriter writer = new FileWriter(csvFilePath, true);
			  CSVWriter csvWriter = new CSVWriter(writer);
			  
			  // title of the csv file
			  String [] columnHeaders = {"Candidate Name", "Index", "Sentiment Score", "Number of mentions"};
			  csvWriter.writeNext(columnHeaders);

			  csvWriter.close();
	          writer.close();
			  
		  }catch (IOException e) {
			  e.printStackTrace();
	      }
		
		// return the file name
		
		return csvFilePath;
	}
	
	
	
	// calculate the Reddit Index
	public static Object[] redditIndex(String name, String party) throws CsvValidationException, IOException {
		
		// create csv reader
		CSVReader reader = new CSVReader(new FileReader("E:\\01_NYU\\2023\\2023 Spring Research\\Politics Project data collection\\"+party+"\\"+name));
		
		// the number of mentions
        int mentionsCount = 0;
        // sentiment score
        float aveSentiment = 0;
        // 
        int sumSentiment = 0;
        // sentiment score * score of post
        int sumScore = 0;
        int temp;
        
        // loop through every score
        String[] row;
        while ((row = reader.readNext()) != null) {
        	// calculate sentiment score * score of post
        	temp = Integer.parseInt(row[3]) * Integer.parseInt(row[5]);
        	
            sumScore += temp;
            
            // record the number of posts
            mentionsCount += 1;
            
            // Accumulate the sentiment score
            System.out.println(row[5]);
            sumSentiment += Integer.parseInt(row[5]);
               
        }
        // the average sentiment score 
        aveSentiment = (float) sumSentiment / mentionsCount;
        
        // reddit index;
        double index = 0.008 * mentionsCount + 0.5 * (sumScore/mentionsCount);
        
		// array of the result
		Object[] indexConlusion = {index, aveSentiment, mentionsCount};
		   
		return indexConlusion;
	}
	
	
	
	// main method
	public static void main(String [] args) throws IOException, CsvValidationException {
		
		Scanner input = new Scanner(System.in);
		
		// ask user for the party to check
		System.out.println("Please enter the party, Republican / Democrates: ");
		String party = input.next();
		
		// enter the time range of the data
		// TODO later
		
		// Define the directory that contains the CSV files
        String demoDirectory = "E:\\01_NYU\\2023\\2023 Spring Research\\politicsProject\\SentimentTest\\"+party;
        
        String newDirectory = "E:\\01_NYU\\2023\\2023 Spring Research\\Politics Project data collection\\"+party;
        
        // create the csv file that concludes the data file directory + CSV file name
        String resultCSV = createCSV(party);
        
        // create file writer for the result csv file
        FileWriter writerNew = new FileWriter(resultCSV, true);
        // create csvWriter
        CSVWriter csvWriter = new CSVWriter(writerNew);
        
        // Get all the files in the directory
        File directory = new File(demoDirectory);
        File[] files = directory.listFiles();
        int sentimentScore = 0;
        
        File newFile = null;
        String candidateName = null;
        int indexCSV = 0;
        String csvFileName = null;
        
        // Loop through each file in the directory
        for (File i : files) {
            // Only process CSV files
            if (i.isFile() && i.getName().endsWith(".csv")) {  	    
            	System.out.println(i.getName());
            	
            	// remove the ".csv" in the name
            	indexCSV = i.getName().lastIndexOf(".csv");
            	candidateName = i.getName().substring(0, indexCSV);
            	
            	newFile = new File(newDirectory, i.getName());
            	
            	
            	// Read the CSV file using a BufferedReader
                BufferedReader reader = new BufferedReader(new FileReader(i));
            	
                // writer 
            	BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            	
            	// skip the first line which is the title
                reader.readLine();
            	
                // read the remaining line and create the new file containing the sentiment socre
                String line = "";
                while ((line = reader.readLine()) != null) {
                	 // Split the line into columns
                    String[] columns = line.split(",");
                    
                    // Only process lines that have at least 3 columns
                    if (columns.length >= 3) {
                        // Extract the third column
                        String textData = columns[2];
                        System.out.println(textData);
                        // process the title
                        nlpPipeline.init();
                 	    sentimentScore = nlpPipeline.estimatingSentiment(textData);
                 	    System.out.println(sentimentScore);
                 	    
                 	    
                 	    // write to the csv file
                 	    // String newColumn = String.valueOf(sentimentScore);
                 	    String newColumn = String.valueOf(sentimentScore);
                 	    String[] newColumns = new String[columns.length + 1];
                 	    
                 	    // add the new column
                 	    System.arraycopy(columns, 0, newColumns, 0, columns.length);
                 	    newColumns[columns.length] = newColumn;
                 	    
                 	    //Write the modified line to the new file
                 	    writer.write(String.join(",", newColumns));
                 	    writer.newLine();
                    }
                    
                    
                }
            
                reader.close();
                writer.close()  ;
                // finish writing the new csv file for one candidate
                
                csvFileName = candidateName + ".csv";
                // store the calculate index, sentiment score, number of mentions
                Object[] candidateResult = redditIndex(csvFileName, party);
                
                // add the calculated data into the result csv file
                String [] dataRow = {candidateName, candidateResult[0].toString(),  candidateResult[1].toString(), candidateResult[2].toString()};
                
                csvWriter.writeNext(dataRow); 

                
                }
            
        }  
        csvWriter.close();
        // close the writer
        writerNew.close();
        System.out.println("Analysis Complete.");
        System.out.println("Candidates' index file is generated in E:\\01_NYU\\2023\\2023 Spring Research\\Politics Project data collection");
        
        	
	}
}


/**{
 String text = "This is an excellent book. I enjoy reading it. I can read on Sundays. Today is only Tuesday. Can't wait for next Sunday. The working week is unbearably long. It's awful.";

  nlpPipeline.init();
  nlpPipeline.estimatingSentiment(text);
  
  System.out.println("Second test: ");
  String text2 = "Joe Biden is Shaking Hands With His Imaginary Friends";
  
  nlpPipeline.estimatingSentiment(text2);
}
* 
*/

//Replace the original file with the temporary file
//new File(i.getParent(), i.getName()).delete();
//new File(i.getParent(), "new_" + i.getName()).renameTo(new File(i.getParent(), i.getName()));

