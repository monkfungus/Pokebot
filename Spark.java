/*
 * Spark class provides access to Spark client through getting last message
 * and sending a message to Spark
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class Spark {

	private String botID;
	private String roomID;
	private String auth;

	// base url GET and POST requests build on
	private String baseURL = "https://api.ciscospark.com/v1/messages";

	// constructor
	public Spark(String ids[]) {

		roomID = ids[0];
		botID = ids[1];

		auth = "Bearer " + botID;
	}

	
	/*
	 * Returns last(most recent) message the bot was tagged in - bots can 
	 * only see messages they are tagged in. 
	 * Calls getLastItem() to parse response from https connection
	 */
	public String getLastMessage() throws MalformedURLException, IOException {

		String lastMessage;

		// will get the last message from the roomID that the bot is mentioned in
		String urlString = baseURL + "?" + "roomId=" + roomID + "&mentionedPeoplpe=me"
						 + "&max=1";

		URL urlObj = new URL(urlString);
		HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("content-type", "application/json");
		con.setRequestProperty("authorization", auth);

		InputStreamReader isr = new InputStreamReader(con.getInputStream());
		BufferedReader br = new BufferedReader(isr);

		lastMessage = br.readLine();

		return lastMessage;

	}

	/*
	 * Sends a given message to set spark room
	 */
	public void sendMessage(String msg) {
		// send some message using HTTP POST
	}
	
	/*
	 * Parses last item in json file and returns
	 * Most receent message is at the top, so return
	 */
	public String getLastItem(String in) {

		// splitting input into 2 items, with the most recent
		// message being itsms[0] and everything else being items[1]
		String items[] = in.split(Pattern.quote("},{"), 2);

		return items[0];

	}
}	
