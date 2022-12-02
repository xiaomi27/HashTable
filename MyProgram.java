
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class MyProgram {

	public static void main(String[] args) throws IOException {
		
		boolean loadFactor=false;
		boolean hashType=false;
		boolean handlingType=false;    // these are parameters to create desired hashTable 
		
		Dictionary<String,Integer> stopWords=new Dictionary<String,Integer>();     // for stopwords
		
		String DELIMITERS = "[-+=" +

		        " " +        //space

		        "\r\n " +    //carriage return line fit

				"1234567890" + //numbers

				"’'\"`" +       // apostrophe

				"(){}<>\\[\\]" + // brackets

				":" +        // colon

				"," +        // comma

				"‒–—―" +     // dashes

				"…" +        // ellipsis

				"!" +        // exclamation mark

				"." +        // full stop/period

				"«»" +       // guillemets

				"-‐" +       // hyphen

				"?" +        // question mark

				"‘’“”" +     // quotation marks

				";" +        // semicolon

				"/" +        // slash/stroke

				"⁄" +        // solidus

				"␠" +        // space?   

				"·" +        // interpunct

				"&" +        // ampersand

				"@" +        // at sign

				"*" +        // asterisk

				"\\" +       // backslash

				"•" +        // bullet

				"^" +        // caret

				"¤¢$€£¥₩₪" + // currency

				"†‡" +       // dagger

				"°" +        // degree

				"¡" +        // inverted exclamation point

				"¿" +        // inverted question mark

				"¬" +        // negation

				"#" +        // number sign (hashtag)

				"№" +        // numero sign ()

				"%‰‱" +      // percent and related signs

				"¶" +        // pilcrow

				"′" +        // prime

				"§" +        // section sign

				"~" +        // tilde/swung dash

				"¨" +        // umlaut/diaeresis

				"_" +        // underscore/understrike

				"|¦" +       // vertical/pipe/broken bar

				"⁂" +        // asterism

				"☞" +        // index/fist

				"∴" +        // therefore sign

				"‽" +        // interrobang

				"※" +          // reference mark

		        "]";
		
		File file1 = new File("stop_words_en.txt");                 // reading and inserting stop words to dictionary
		Scanner scanner1 = new Scanner(file1,"UTF-8");
		
		while (scanner1.hasNextLine()) {
			   String line = scanner1.nextLine();
			   line=line.toLowerCase(Locale.ENGLISH).trim();
			   stopWords.put(line, 0);
			}
		scanner1.close();
		
		
		Scanner scan =new Scanner(System.in);              // getting inputs from user   
		
		System.out.println("Please select Load-Factor :     A)0.5    B)0.8  ");
		String load=scan.nextLine();
		if(load.equalsIgnoreCase("A")) {loadFactor=true; }
		
		
		System.out.println("Please select Hash Function:    A)SSF    B)PAF  ");
		String hash=scan.nextLine();
		if(hash.equalsIgnoreCase("A")) {hashType=true;}
		
		System.out.println("Please select Collision Handling:    A)LP    B)DH  ");
		String hand=scan.nextLine();	
		if(hand.equalsIgnoreCase("A")) {handlingType=true;}
		
		HashedDictionary<String,String> myHash=new HashedDictionary<>(loadFactor,hashType,handlingType);   // creating hashtable for user
		
		
		// Starting reading txts and adding hashTable
		
		long startTime = System.currentTimeMillis();   // starting indexing time
		
		File folder = new File("bbc");
		File[] listOfFolders = folder.listFiles();
		
		for (int i = 0; i < listOfFolders.length; i++) {

			File[] listOfFiles = listOfFolders[i].listFiles();
			
			for (int k = 0; k < listOfFiles.length; k++) {    // all txt files

				File file = listOfFiles[k];
				
				
				Scanner scanner = new Scanner(file, "UTF-8");

				while (scanner.hasNextLine()) {

					String line = scanner.nextLine();

					String[] splitted = line.split(DELIMITERS);

					for (int j = 0; j < splitted.length; j++) {

						String word = splitted[j].toLowerCase(Locale.ENGLISH).trim();

						if (!word.equals("") && !word.equals(" ") && !stopWords.contains(word)) {
							myHash.put(word, listOfFolders[i].getName()+"/"+listOfFiles[k].getName());
						}
					}

				} 
				scanner.close();
			} 
			
		} 
		// end of reading and indexing	
		
		
		
		long estimatedTime = System.currentTimeMillis() - startTime;   // indexing time
		
		
		long min=99999999;
		long max=0;
		
		File file3 = new File("search.txt");             // searching 
		Scanner scanner3 = new Scanner(file3,"UTF-8");
		
		long startTotal = System.nanoTime();
		
		while (scanner3.hasNextLine()) {
			   boolean flag=true;
			
			   String word = scanner3.nextLine();
			   word=word.toLowerCase(Locale.ENGLISH).trim();
			   
			   long start = System.nanoTime();
			   
			   if(myHash.get(word)==null) {
				   flag=false;
			   }
			   long resultTime = System.nanoTime() - start;
			   
			   if(flag) {
				   
				   if(resultTime>max) {
					   max=resultTime;
				   }
				   
				   if(resultTime<min) {
					   min=resultTime;
				   }
			   }
			   
			}
		long resultTotal = System.nanoTime() - startTotal;    // total search time 
		
		scanner3.close();
		
			
		 
		 // here is for searching desired word in hashtable
		boolean flag=true;
		
		while(flag) {
			
			System.out.print(">Search:" );
			System.out.println();
			Scanner scan4 =new Scanner(System.in);
			String word=scan.nextLine();
			word=word.toLowerCase(Locale.ENGLISH).trim();
			System.out.println();
			myHash.search(word);
			
			System.out.println();
			System.out.println("Again search ??   Yes (press Y)   No (press N) ");      // if press N , program is over
			if(scan.nextLine().equalsIgnoreCase("N")) {
				flag=false;
			}
			
			
		}
			
		
		

		
		
	
		
		
	}

}
