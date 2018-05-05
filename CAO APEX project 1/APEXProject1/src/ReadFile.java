/*import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ReadFile {
	String a;
	String b;
	String c;

public ReadFile(String a, String b, String c) {
	this.a = a;
	this.b = b;
	this.c = c;
}

public static void main(String args[]){
	try{
		  FileInputStream fstream = new FileInputStream("E://Develop//HelloWorld//src//textfile.txt");
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String strLine;
          while ((strLine = br.readLine()) != null)   {
          String[] tokens = strLine.split(" ");
          ReadFile record = new ReadFile(tokens[0],tokens[1],tokens[2]);
          System.out.println(record.a);
          System.out.println(record.b);
          System.out.println(record.c);
          //ArrayList<String> fileInstructions = new ArrayList<>();
          
          }
          in.close();
		initialize a = new initialize();
		a.init();
		Scanner s = new Scanner(new File("G://BU//Fall 2016//CAO//SimpleTestInput1.txt"));
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNextLine()){
		    list.add(s.nextLine());
		    //System.out.println("++"+s.next());
		}
		s.close();
		Iterator<String> instrIterator = list.iterator();
		while (instrIterator.hasNext()) {
			System.out.println(instrIterator.next());
		}
	   }catch (Exception e){
	     System.err.println("Error: " + e.getMessage());
	   }
}
}
*/