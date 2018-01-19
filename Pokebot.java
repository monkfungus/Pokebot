import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import javax.swing.*;
import com.pubnub.api.*;
import com.pubnub.api.callbacks.*;
import com.pubnub.api.enums.PNStatusCategory;
//import com.pubnub.api.models.consumer.*;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/* 
 * Simple GUI - program keeps running in a loop,
 * printing to terminal. Absolutely almost positively zero handling 
 * of anything abnormal - if something goes wrong gluck
 * In deets.txt, format is as follows:
 * 		channel : subscribeKey : publishKey
 */

public class Pokebot extends JFrame implements Runnable, ActionListener {

	private boolean isPoked;
	
	private String subKey;
	private String pubKey;
	private String channel;
	private PubNub pubnub;
	
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
		System.out.println("Key details:");
		System.out.printf("channel: %S\nsubscribe key: %s\npublish key: %s\n"
							,channel, subKey, pubKey);
		System.out.printf("Initialising veractor modulation feeds ... ");

		// set up pubnub
		PNConfiguration pnConfig = new PNConfiguration();
		pnConfig.setSubscribeKey(subKey);
		pnConfig.setPublishKey(pubKey);
		pnConfig.setSecure(false);
		pubnub = new PubNub(pnConfig);

		// add listener for to hear messages - updates isPoked if necessary
		pubnub.addListener(new SubscribeCallback() {
			@Override
			public void message(PubNub pubernuber, PNMessageResult pnMessageResult) {
				String received = pnMessageResult.getMessage().toString();
				if (received.equals("poke")) {
					isPoked = true;
				} else {
					System.out.println("Received a message not understood: " + received);
				}
			}
			@Override
			public void presence(PubNub pubernuber, PNPresenceEventResult pnPresenceResult) {}
			@Override
			public void status(PubNub pubernuber, PNStatus pnStatus) {}
		});
		
		// subscribe to channel
		pubnub.subscribe()
			.channels(Arrays.asList(channel))
			.execute();

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
		isPoked = true;
		while (true) {	
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
	 * publishes a poke message
	 */
	private void sendPoke() {
		pubnub.publish()
			.channel(channel)
			.message("poke")
			.async(new PNCallback<PNPublishResult>() {
	            @Override
	            public void onResponse(PNPublishResult result, PNStatus status) {
	                //System.out.println("Result of publish: " + result);//
	                //System.out.println("Status of publish: " + status);
	            }
			});
		isPoked = false;
		System.out.println("Poke sent");
	}// end sendPoke


	/*
	 * reads details from deets.txt, or given file
	 */
	private void getDeets() {
		try (
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
		) {
			String[] chunks = br.readLine().split(" : ");

			if ( chunks.length != 3 ) {
				System.out.println(filename + " is of unexpected format.");
				System.out.println("Format should be as follows: ");
				System.out.println("channel : subscribe key : publish key");
				System.out.println("check deets.txt looks like this");
				System.exit(-1);
			}
			
			channel = chunks[0];
			subKey = chunks[1];
			pubKey = chunks[2];

		} catch (IOException e) {
			System.out.printf("Unable to load file %s: IOException\n", filename);
			System.out.println("File may not be in the correct folder, may be named differently or corrupted or some such");
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