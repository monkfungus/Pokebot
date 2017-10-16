/* 
 * Simple GUI - program keeps running in a loop,
 * printing to terminal. Absolutely almost positively zero handling 
 * of anything abnormal - if something goes wrong gluck
 * In deets.txt, format is as follows:
 * <roomID> : <botID> : <otherBotName>
 */

import java.util.Scanner;
import java.io.*;

public class Pokebot {


	private static Spark spark;
	private static boolean isPoked;
	private static String botID;
	private static String roomID;
	private static String otherBotName;

	
	public static void main( String args[] ) {

		boolean validIn;
		Scanner scan = new Scanner(System.in);
		String input;


		// initialise IDs and otherBotName from deets.txt
		System.out.println("Getting le deets ..");
		try 
		{
			getDeets();		
		} 
		catch (Exception e) 
		{
			System.out.println("getDeets threw exception: " + e.getMessage());
			System.out.println("re-start me when u fix deets ..");
			System.exit(1);
		}	
		

		// initialise Spark
		System.out.println("Prepping messages in bottles ..");
		spark = new Spark( roomID, botID );

		// getting status
		getStatus();

		System.out.println();
		System.out.println("Beginning the never-ending poking!");
		System.out.println();
		// while loop that just keeps on going..
		// - this is kinda like an embedded yoke isn't it?
		while ( true ) 
		{

			if ( isPoked == true ) 
			{
				System.out.printf("%nYou is poked - poke back? ");

				validIn = false;
				while ( validIn == false ) 
				{
					input = scan.nextLine();

					if ( (input.equals("y") == true) | (input.equals("Y") == true) ) 
					{
						sendPoke();
						
						System.out.println("You poked back!");

						isPoked = false; // update status
						validIn = true; // exits input while
					}
					else if ( (input.equals("n") == true) | (input.equals("N") == true) )
					{
						System.out.println("Don't be such a dry shite");
						System.out.print("TYPE Y TO POKE BACK U TOE ");
					}
					else 
					{
						System.out.println("try maybe i dunno like type something THIS CODE CAN UNDERSTAND U SPANNER");
						System.out.print("like maybe say y or n or some such YANO LIKE");
					}
				}// end input while
			}
			else // isPoked must be false => am poking 
			{
				try // this is for the Thread.sleep() yoke
				{
					System.out.println("You is poking - not for long ..");
					System.out.printf("Refreshing ");
					Thread.sleep(500);
					System.out.printf(". ");
					Thread.sleep(500);
					System.out.printf(". ");
					Thread.sleep(500);
					System.out.printf(". %n");
					Thread.sleep(500);
				}
				catch ( InterruptedException e ) 
				{
					System.out.println("Sleeping interrupted ..");
					System.out.println(e.getMessage());
					System.out.println("Exiting ..");
					System.exit(1);
				}
			}

		}// end embedded style while		

	}// end main


	/*
	 * getStatus checks status of poking and updates isPoked accordingly
	 * Calls getLastMessage from spark and parses last message
	 * to get the actual text of the last message this bot was tagged in.
	 * Returns true if poked, false if not poked. Gets a response in JSON 
	 * format, I just parse it using text methods
	 */
	private static void getStatus() {

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
				isPoked = true;
			}
			else if (command.equals("get poking")) 
			{
				isPoked = false;
			}
			else 
			{
				// again, my lazy but super smarts handling of stuff
				System.out.println("getStatus can't actually do anything smart ..");
				System.out.println("last message: ");
				for ( String pair : pairs ) 
				{
					System.out.println(pair);
				}
				System.out.println("exiting ..");
				System.exit(1);
			}
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			System.out.println("exiting ..");
			System.exit(1); // heh
		}
		
	}// end getStatus


	/*
	 * sendPoke will send a poke message to Cisco Spark
	 * Sends it in markdown so the tagging yoke works
	 */
	private static void sendPoke() {
		
		String otherBotEmail = otherBotName  + "@sparkbot.io";
		String msgMarkdown = "<@personEmail:" + otherBotEmail + "|" 
							+ otherBotName + "> get poked";
		
		try 
		{
			spark.sendMessage(msgMarkdown);
		} 
		catch (Exception e) 
		{
			System.out.println("sendPoke failed :");
			System.out.println(e.getMessage());
			System.out.println("Exiting ..");
			System.exit(1);
		}

	}// end sendPoke


	/*
	 * getDeets reads details from deets.txt. deets.txt is assumed to
	 * be of structure "<roomID> , <botID>, <otherBotName>"
	 */
	private static void getDeets() throws FileNotFoundException, IOException {

		String filename = "deets.txt";

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		String[] chunks = br.readLine().split(" : ");

		System.out.println("chunks length: " + chunks.length );

		if ( chunks.length != 3 ) 
		{
			System.out.println("Something has gone horribly astray");
			System.out.println("deets.txt makes no sense, restart me when it does make sense");
			System.out.println("throwing in the towel ..");
			System.exit(1);
		}
		
		roomID = chunks[0];
		botID = chunks[1];
		otherBotName = chunks[2];

		br.close();
		fr.close();

	}// end getDeets

}// end everything
