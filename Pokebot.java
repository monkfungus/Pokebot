/* 
 * Simple GUI - program keeps running in a loop,
 * printing to terminal. Absolutely almost positively zero handling 
 * of anything abnormal - if something goes wrong gluck
 * In deets.txt, format is as follows:
 * <roomID> : <botID> : <botName> : <otherBotName>
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import javax.swing.*;

public class Pokebot extends JFrame implements Runnable, ActionListener {

	private Spark spark;
	private boolean isPoked;
	private String botID;
	private String botName;
	private String botEmail;
	private String roomID;
	private String otherBotName;
	private String filename;
	private JPanel displayPanel;
	private CardLayout displayPanelLayout;
	private JButton pokeButton;
	private Timer timer;
	private ArrayList<String> pokingImages;
	private ArrayList<String> pokedImages;
	private int currentImage;

	// constructor
	public Pokebot() {
		super("Pokebot");
		filename = "deets.txt";
		init();
	}


	// also constructor
	public Pokebot(String filename) {
		super("Pokebot");
		this.filename = filename;
		init();
	}

	private void init() {
		getDeets();
		System.out.println("Details of this bot:");
		System.out.printf("roomID: %S\nbotID: %s\nbotName: %s\notherBotName: %s\n"
							,roomID, botID, botName, otherBotName);
		System.out.printf("Initialising veractor modulation feeds ... ");

		spark = new Spark(roomID, botID);

		pokedImages = new ArrayList<String>();
		for (int count = 11; count <= 20; count++) {
			pokedImages.add("image" + count);
		}
		pokingImages = new ArrayList<String>();
		for (int count = 3; count <= 10; count++) {
			pokingImages.add("image" + count);
		}
		currentImage = 0;

		initGUI();

		timer = new Timer(200, this);

		System.out.printf("done%n");
	}

	private void initGUI() {
		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		GridBagConstraints constraints;

		displayPanel = new JPanel();
		displayPanelLayout = new CardLayout();
		displayPanel.setLayout(displayPanelLayout);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		//constraints.ipadx = 400;
		//constraints.ipady = 400;
		container.add(displayPanel, constraints);

		Icon tempIcon;
		JPanel tempPanel;

		for (String imageName : pokingImages) {
			tempIcon = new ImageIcon("pokeImages/"+imageName+".png");
            tempPanel = new JPanel();
            tempPanel.add(new JLabel(tempIcon));
            displayPanel.add(tempPanel, imageName);
        }

		for (String imageName : pokedImages) {
			tempIcon = new ImageIcon("pokeImages/"+imageName+".png");
            tempPanel = new JPanel();
            tempPanel.add(new JLabel(tempIcon));
            displayPanel.add(tempPanel, imageName);
        }

		pokeButton = new JButton("Poke");
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		container.add(pokeButton, constraints);

		pokeButton.addActionListener(this);
		pokeButton.setEnabled(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	// runs foreverish
	public void run() {
		while (true) {	
			getStatus();
			timer.restart();
			if (isPoked == true) {
				pokeButton.setEnabled(true);
			}
			else {
				pokeButton.setEnabled(false);
			}
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				System.out.println("run()'s sleeping interrupted:");
				e.printStackTrace();
			}
		}	
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
	private void getDeets() {
		try (
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
		) {
			String[] chunks = br.readLine().split(" : ");

			if ( chunks.length != 4 ) {
				System.out.println("Something has gone horribly astray");
				System.out.println("deets.txt makes no sense, restart me when it does make sense");
				System.out.println("throwing in the towel ..");
				System.exit(-11);
			}
			
			roomID = chunks[0];
			botID = chunks[1];
			botName = chunks[2];
			otherBotName = chunks[3];

			botEmail = botName + "@sparkbot.io";
		} catch (IOException e) {
			System.out.printf("Unable to load file %s: IOException\n", filename);
			e.printStackTrace();
			System.exit(-1);
		}
	}// end getDeets


	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == pokeButton) {
			sendPoke();
			// need to make sure there is a pause between sending poke and getting status
		} 
		else if (source == timer) {
			// check the status of isPoked, to decide which set of images to be displaying
			if (isPoked) {// show "poked" sequence
				if (currentImage < pokedImages.size()) {
					displayPanelLayout.show(displayPanel, pokedImages.get(currentImage));
					currentImage++;
				}
				else {
					timer.stop();
					currentImage = 0;
				}
			}
			else {// show "poking" sequence
				if (currentImage < pokingImages.size()) {
					displayPanelLayout.show(displayPanel, pokingImages.get(currentImage));
					currentImage++;
				}
				else {
					timer.stop();
					currentImage = 0;
				}
			}	
		}
	}
}// end everything