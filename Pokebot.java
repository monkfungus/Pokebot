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

	private static String botID;
	private static String roomID;

	private static boolean getStatusSuccess;
	private static String status;


	public static void main(String args[]) {

		
		boolean updateStatusSiccess;
		boolean validInput;

		String userInput;
		
	
		// read botID and roomID from deets.txt
		try {
			getIDs("deets.txt");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Scanner scan = new Scanner(System.in);


		System.out.println("=== Pokebot ===");
		System.out.println();
		System.out.println("Your room ID: " + roomID);
		System.out.println("Your bot ID: " + botID);
		System.out.println("Getting status ...");

		getStatus();

		if (getStatusSuccess == true) {
			System.out.println("You are " + status);
		}
		else  {
			System.out.println("Failed to update status");
		}
		System.out.println();

		// set validInput to false so while loop will start
		validInput = false;

		while (validInput == false) {

			System.out.println("Poke back? (y/n)");
			userInput = scan.nextLine();

			if ( userInput.equals("y") == true) {
				System.out.println("sending poke ...");
				sendPoke();
				validInput = true;

			}
			else if (userInput.equals("n") == true) {
				System.out.println("doing absolutely nothing about being poked");
				validInput = true;
			}
			else {
				System.out.println("press \"y\" or \"n\" or leave u goat");
				validInput = false;
			}
		}


	}// end main

	/*
	 * getStatus checks and updates status
	 * Modifies class variables getStatusSuccess and status
	 * In future, will actually send a HTTP GET request to Cisco Spark
	 * checking for @mentions in Spark room. Will update getStatus and
	 * status accordingly. Will return a boolean and have exception handling.
	 */
	private static void getStatus() {
		
		// assign getStatusSuccess as default
		getStatusSuccess = true;

		status = "poked";
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
	private static void getIDs(String filename) throws Exception{

		String line;
		String arrIn[];

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		line = br.readLine();

		arrIn = line.split(" , ");
		
		// assign IDs from read in array
		roomID = arrIn[0];
		botID = arrIn[1];
	}
}