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
	private String baseURL = "https://api.ciscospark.com/v1/messages";
	private String charset = "utf-8";

	// constructor 
	public Spark(String rID, String bID) {
		roomID = rID;
		botID = bID;
		auth = "Bearer " + botID;
	}
	

	/*
	 * Returns last(most recent) message the bot was tagged in - bots can 
	 * only see messages they are tagged in. 
	 * Calls getLastItem() to parse response from https connection
	 */
	public String getLastMessage() throws Exception {
		String lastMessage;

		// will get the last message from the roomID that the bot is mentioned in
		String urlString = baseURL + "?" + "roomId=" + roomID + "&mentionedPeople=me"
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
	 * Sends given message (passed in markdown) to room through
	 * a HTTP POST request
	 */
	public void sendMessage(String msg) throws Exception {
		
		// manually encode message
		msg = msg.replace(" ", "%20");
		msg = msg.replace("<", "%3C");
		msg = msg.replace(">", "%3E");
		msg = msg.replace("@", "%40");
		msg = msg.replace(":", "%3A");

		// build query
		String query = "roomId=" + roomID + "&markdown=" + msg;
		
		URL urlObj = new URL(baseURL + "?" + query);
		
		HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();

		con.setDoOutput(true); // sets as POST
		con.setRequestProperty("content-type", 
							"application/x-www-form-urlencoded;charset=" 
							+ charset);

		con.setRequestProperty("authorization", auth);

		try (OutputStream out = con.getOutputStream()) {
			out.write(query.getBytes(charset));
		}
		System.out.println("Respone code: " + con.getResponseCode());
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
