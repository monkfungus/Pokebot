/* 
 * Basic skeleton of functionality. 
 * Reads details of the bot it controls and the room the bot
 * lives in from deets.txt
 * In deets.txt, format is as follows:
 * <roomID> , <botID>
 */

import java.util.Scanner;
import java.io.*;

public class Pokebot {

	private Spark spark;
	
	public static void main(String args[]) {

		boolean validInput;
		String userInput;
		String ids[];
		boolean isPoked;

		// read botID and roomID from deets.txt
		try {
			ids = getIDs("deets.txt");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ids = new String[2];
			ids[0] = "deets.txt did not do a load";
			ids[1] = "this bot is not the exist";
		}

		// initialise spark with ids
		spark = new Spark(ids);

		Scanner scan = new Scanner(System.in);


		System.out.println("=== Pokebot ===");
		System.out.println();
		System.out.println("Your room ID: " + ids[0]);
		System.out.println("Your bot ID: " + ids[1]);
		System.out.println("Getting status ...");

		isPoked = getStatus();

		if (isPoked == true) 
		{
			System.out.println("You are poked");
			validInput = false; // force while to start

			while (validInput == false) 
			{
				System.out.println("Poke back? (y/n)");
				userInput = scan.nextLine();

				if ( userInput.equals("y") == true ) 
				{
					System.out.println("sending poke ...");
					sendPoke();
					validInput = true;
				}
				else if ( userInput.equals("n") == true ) 
				{
					System.out.println("doing absolutely nothing about being poked");
					validInput = true;
				}
				else {
					System.out.println("press \"y\" or \"n\" or leave u goat");
					validInput = false;
				}
			}
		}
		else  {
			System.out.println("You are poking");
		}
		System.out.println();		

	}// end main


	/*
	 * getStatus gets and updates Status
	 * Actually calls getLastMessage from spark and parses last message
	 * to get the actual text of the last message this bot was tagged in.
	 * Returns true if poked, false if not poked
	 */
	private static boolean getStatus() {

		String last = spark.getLastMessage();
		// temporary default
		return true;
	}


	/*
	 *	sendPoke will send a poke message to Cisco Spark
	 * 	will return true if successful, false if not
	 */
	private static void sendPoke() {
		System.out.println("Poke sent.");
	}


	/*
	 * getIDs reads from given file, parses text and updates roomID and botID
	 * variables. File is assumed to be of structure "<roomID> , <botID>"
	 */
	private static String[] getIDs(String filename) throws Exception{

		String[] toReturn;

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		//line = br.readLine();

		toReturn = br.readLine().split(" , ");

		return toReturn;
		
	}
}