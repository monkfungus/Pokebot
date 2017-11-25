/* 
 * Simple GUI - program keeps running in a loop,
 * printing to terminal. Absolutely almost positively zero handling 
 * of anything abnormal - if something goes wrong gluck
 * In deets.txt, format is as follows:
 * <roomID> : <botID> : <botName> : <otherBotName>
 */

import java.util.Scanner;
import java.io.*;

public class Pokebot implements Runnable {

	private Spark spark;
	private boolean isPoked;
	private String botID;
	private String botName;
	private String botEmail;
	private String roomID;
	private String otherBotName;
	private String filename;

	// constructor
	public Pokebot() {
		filename = "deets.txt";
		try {
			getDeets();
		} catch (IOException e) {
			System.out.printf("Unable to load file %s: IOException\n", filename);
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Details of this bot:");
		System.out.printf("roomID: %S\nbotID: %s\nbotName: %s\notherBotName: %s\n"
							,roomID, botID, botName, otherBotName);
		System.out.printf("Initialising veractor modulation feeds ... ");
		spark = new Spark(roomID, botID);
		System.out.println("done");
		System.out.println();
	}

	// also constructor
	public Pokebot(String filename) {
		this.filename = filename;
		try {
			getDeets();
		} catch (IOException e) {
			System.out.printf("Unable to load file %s: IOException\n", filename);
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Details of this bot:");
		System.out.printf("roomID: %S\nbotID: %s\nbotName: %s\notherBotName: %s\n"
							,roomID, botID, botName, otherBotName);
		System.out.printf("Initialising veractor modulation feeds ... ");
		spark = new Spark(roomID, botID);
		System.out.println("done");
		System.out.println();
	}

	// also also constructor
	public Pokebot(String roomID, String botID, String botName, String otherBotName) {
		this.roomID = roomID;
		this.botID = botID;
		this.botName = botName;
		this.otherBotName = otherBotName;

		System.out.println("Details of this bot:");
		System.out.printf("roomID: %S\n botID: %s\n botName: %s\n otherBotName: %s\n"
							,roomID, botID, botName, otherBotName);
		System.out.printf("Initialising veractor modulation feeds ... ");
		spark = new Spark(roomID, botID);
		System.out.println("done");
		System.out.println();
	}

	
	public void run() {
		boolean validIn;
		Scanner scan = new Scanner(System.in);
		String input;

		// infinite while loop
		while (true) {	
			getStatus();
			if (isPoked == true) {
				System.out.println("You is poked - poke back? ");
				validIn = false;
				while (validIn == false) {
					input = scan.nextLine();

					if ((input.equals("y") == true) || (input.equals("Y") == true)) {
						sendPoke();
						System.out.println("You poked back!");
						getStatus();
						validIn = true; // exits input while
					}
					else if ((input.equals("n") == true) || (input.equals("N") == true)) {
						System.out.println("Don't be such a dry shite");
						System.out.print("TYPE Y TO POKE BACK U TOE ");
					}
					else {
						System.out.println("try maybe i dunno like type something THIS CODE CAN UNDERSTAND U SPANNER");
						System.out.print("like maybe say y or n or some such YANO LIKE");
					}
				}// end input while
			}
			else { // isPoked must be false => am poking 
					System.out.println("You is poking");
					// another possibly infinite while - for to keep checking status until poked
					System.out.printf("Refreshing  ");
					while (!isPoked) {
						try {
							for (int count = 0; count < 4; count++) {
								System.out.printf("\b|");
								Thread.sleep(200);
								System.out.printf("\b/");
								Thread.sleep(200);
								System.out.printf("\b-");
								Thread.sleep(200);
								System.out.printf("\b\\");
								Thread.sleep(200);
								System.out.printf("\b|");
								Thread.sleep(200);
								System.out.printf("\b/");
								Thread.sleep(200);
								System.out.printf("\b-");
								Thread.sleep(200);
								System.out.printf("\b\\");
								Thread.sleep(200);
							}
						}
						catch ( InterruptedException e ) {
							System.out.println("Sleeping interrupted");
							System.out.println(e.getMessage());
							System.out.println("Exiting ..");
							scan.close();
							System.exit(1);
						}
						getStatus();
					}
			}
		}// end embedded style while		
	}


	public static void main(String args[]) {
		if (args.length == 0) {
			Pokebot bot = new Pokebot();
			bot.run();
		}
		else if (args.length == 1) {
			Pokebot bot = new Pokebot(args[0]);
			bot.run();
		}
		else {
			System.out.printf("Usage: \"java Pokebot\" OR \"java Pokebot <deets file>\"\n");
		}
	}


	/*
	 * getStatus checks status of poking and updates isPoked accordingly
	 * Calls getLastMessage from spark and parses last message
	 * to get the actual text of the last message this bot was tagged in.
	 * Returns true if poked, false if not poked. Gets a response in JSON 
	 * format, I just parse it using text methods
	 */
	private void getStatus() {

		try {
			String pairs[] = spark.getLastMessage().split(",");

			// get message pair - assuming it'll always be 3 heh
			String message = pairs[3];
			
			// remove excess shtuff from message pair
			message = message.replace(":", "");
			message = message.replace("text", "");
			message = message.replace("\"", "");
			
			// split message into three two parts expected
			// should be of the format:
			// - [@thisBot, command, @sendingBot]
			// e.g. "@thisBotName get poked @sendingBotName"
			String msgBits[] = message.split(" ");
			
			// checking for unexpected message
			if (msgBits.length != 4) {
				System.out.println("Unexpected message length of "
							+ msgBits.length + " received.");
				System.out.println("Message received: " + message);
				System.exit(1);
			}
			else if (!(msgBits[0].equals(botName)) && !(msgBits[0].equals(otherBotName))) {
				System.out.println("Unexpected bot tagged at start of message");
				System.out.println("Expected " + botName + " or " + otherBotName);
				System.out.println("Message received: " + message);
				System.exit(1);
			}
			else if (!msgBits[3].equals(botName) && !msgBits[3].equals(otherBotName)) {
				System.out.println("Unexpected bot name at end of message");
				System.out.println("Expected " + botName + " or " + otherBotName);
				System.out.println("Message received: " + message);
				System.exit(1);
			}

			// re-assemble command from two middle words
			String command = msgBits[1] + " " + msgBits[2];

			if (msgBits[0].equals(botName) && msgBits[3].equals(otherBotName)) {

				if (command.equals("get poked")) {
					isPoked = true;
				}
				else if (command.equals("get poking")) {
					isPoked = false;
				}
				else {
					// again, my lazy but super smarts handling of stuff
					System.out.println("getStatus can't actually do anything smart ..");
					System.out.println("last message: ");
					for ( String pair : pairs ) {
						System.out.printf("%s ", pair);
					}
					System.out.println();
					System.out.println("exiting ..");
					System.exit(1);
				}
			}
			else if (msgBits[0].equals(otherBotName) && msgBits[3].equals(botName)) {

				if (command.equals("get poked")) {
					isPoked = false;
				}
				else if (command.equals("get poking")) {
					isPoked = true;
				}
				else {
					// again, my lazy but super smarts handling of stuff
					System.out.println("getStatus can't actually do anything smart ..");
					System.out.printf("last message: ");
					for ( String pair : pairs ) {
						System.out.printf("%s ", pair);
					}
					System.out.println();
					System.out.println("exiting ..");
					System.exit(1);
				}
			}
			System.out.printf("Last message received: \"%s\"\n", message);
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			//System.exit(1); // heh
		}
	}// end getStatus


	/*
	 * sendPoke will send a poke message to Cisco Spark
	 * Sends it in markdown so the tagging yoke works
	 */
	private void sendPoke() {

		String otherBotEmail = otherBotName  + "@sparkbot.io";
		String msgMarkdown = "<@personEmail:" + otherBotEmail 
							+ "> get poked " + "<@personEmail:" 
							+ botEmail + ">" ;
		System.out.println("Markdown message to be sent: " 
							+ msgMarkdown);
		try {
			spark.sendMessage(msgMarkdown);
		} 
		catch (Exception e) {
			System.out.println("sendPoke failed :");
			System.out.println(e.getMessage());
			System.out.println("Exiting ..");
			System.exit(1);
		}
	}// end sendPoke


	/*
	 * getDeets reads details from deets.txt. deets.txt is assumed to
	 * be of structure "<roomID> : <botID> : <botName> : <otherBotName>"
	 */
	private void getDeets() throws FileNotFoundException, IOException {

		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);

		String[] chunks = br.readLine().split(" : ");

		//System.out.println("chunks length: " + chunks.length );

		if ( chunks.length != 4 ) {
			System.out.println("Something has gone horribly astray");
			System.out.println("deets.txt makes no sense, restart me when it does make sense");
			System.out.println("throwing in the towel ..");
			System.exit(1);
		}
		
		roomID = chunks[0];
		botID = chunks[1];
		botName = chunks[2];
		otherBotName = chunks[3];

		botEmail = botName + "@sparkbot.io";
		br.close();
		fr.close();
	}// end getDeets
}// end everything