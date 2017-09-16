/* 
 *
 *
 *
 */

import java.util.Scanner;

public class Pokebot {

	private static boolean getStatusSuccess;
	private static boolean updateStatusSiccess;

	private static String userInput;
	private static String status;

	public static void main(String args[]) {

		userInput = new String();
		boolean validInput;
		Scanner scan = new Scanner(System.in);

		System.out.println("=== Pokebot ===");

		System.out.println();

		System.out.println("Getting status ...");

		getStatus();

		if (getStatusSuccess == true) {
			System.out.println("You are " + status);
		}
		else  {
			System.out.println("Failed to update status");
		}
		System.out.println();

		// set validInput to false so while look will work
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
	 *  - modifies class variables getStatusSuccess and status
	 *
	 */
	private static void getStatus() {
		
		getStatusSuccess = true;

		status = "poked";
	}


	/*
	 *	sendPoke 
	 *	in future, will return a boolean and have exception handling etc
	 */
	private static void sendPoke() {
		System.out.println("Poke sent.");
	}
































}