import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Parses a file into an ArrayList<String> whose elements are the lines of the file.
 * 
 * @author Andrew Bacon, Holly French, Veronica Lynn
 * CS 204, Spring 2013
 */
public class FileParser {
	
	
	/**
	 * Parses the lines of a file into an ArrayList
	 * 
	 * @param filename The file to read from
	 * @return an ArrayList containing the lines of the file as strings
	 */
	public ArrayList<String> getLines(String filename) {
		Scanner infile = null;

		try {
			infile = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Error: file " + filename + "not found.");
			e.printStackTrace();
			System.exit(-1);
		}
		ArrayList<String> values = new ArrayList<String>();
		String currentLine;

		// As long as there are still lines in the file, read in the next line
		// and add it to the list.
		while (infile.hasNextLine()) {
			currentLine = infile.nextLine();
			values.add(currentLine);
		}
		infile.close();
		
		return values;
	}
	
}
