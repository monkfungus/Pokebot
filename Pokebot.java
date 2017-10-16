/* 
 * Basic skeleton of functionality. 
 * Reads details of the bot it controls and the room the bot
 * lives in from deets.txt
 * In deets.txt, format is as follows:
 * <roomID> : <botID> : <otherBotName>
 */

import java.util.Scanner;
import java.io.*;

public class Pokebot {

	private static Spark spark;
	
	public static void main(String args[]) {

		boolean validInput;
		String userInput;
		String deets[];
		boolean isPoked;
		String botID;
		String roomID;
		String otherBotName;


		// read botID and roomID from deets.txt
		try {
			deets = getDeets("deets.txt");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
			// lazy exception handling heh
			System.out.println("exiting ..");
			System.exit(1);

			// initialising deets to get javac to shut up
			deets = new String[3];
		}

		// assign deet info to variables
		roomID = deets[0];
		botID = deets[1];
		otherBotName = deets[2];


		// initialise spark with ids
		spark = new Spark(roomID, botID);

		Scanner scan = new Scanner(System.in);


		//System.out.println("Getting status ...");

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
					System.out.println("sending poke ..");
					sendPoke(otherBotName);// pass otherBotName
					System.out.println("poke sent");
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
	 * Calls getLastMessage from spark and parses last message
	 * to get the actual text of the last message this bot was tagged in.
	 * Returns true if poked, false if not poked. Gets a response in JSON 
	 * format, I just parse it using text methods
	 */
	private static boolean getStatus() {

		try {
			
			String pairs[] = spark.getLastMessage().split(",");

			// get message pair - assuming it'll always be 3 heh
			String message = pairs[3];
			
			// remove excess shtuff from message pair
			message = message.replace(":", "");
			message = message.replace("text", "");
			message = message.replace("\"", "");
			
			// split message into the two parts expected
			// - [mentionedBot, command]
			String msgBits[] = message.split(" ", 2);
			

			// set command to last bit of message
			// should be "get poked" or "get poking"
			String command = msgBits[1];

			if (command.equals("get poked")) 
			{
				return true;
			}
			else if (command.equals("get poking")) 
			{
				return false;
			}
			else 
			{
				// again, my lazy but super smarts handling of stuff
				System.out.println("invalid message");
				System.out.println("exiting ..");
				System.exit(1);
			}


		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("exiting ..");
			System.exit(1); // heh
		}

		// if method gets to here just return false - I need to 
		// do solid handling of unexpected stuff in future version
		System.out.println("setting default status as false");
		return false;
	}


	/*
	 *	sendPoke will send a poke message to Cisco Spark
	 * 	will return true if successful, false if not
	 */
	private static void sendPoke(String to) {
		
		String otherBotEmail = to  + "@sparkbot.io";
		String msgMarkdown = "<@personEmail:" + otherBotEmail + "|" + to + "> get poked";
		
		try {

			spark.sendMessage(msgMarkdown);

		} catch (Exception e) {

			System.out.println("sendPoke failed :");
			System.out.println(e.getMessage());

		}
	}	


	/*
	 * getIDs reads from given file, parses text and updates roomID and botID
	 * variables. File is assumed to be of structure "<roomID> , <botID>, <otherBotName>"
	 */
	private static String[] getDeets(String filename) throws Exception{

		String[] toReturn;

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		toReturn = br.readLine().split(" : ");

		return toReturn;
		
	}
}