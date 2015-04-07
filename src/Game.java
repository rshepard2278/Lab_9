import java.util.ArrayList;

public class Game {
	private Parser parser;
	private Player player1;
	private ArrayList<Room> rooms;
	private Room entranceHall, muggleClassroom, greatHall, astronomyTower,
			dumbledoresOffice, forbiddenForest;

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		player1 = new Player("Rick");
		rooms = new ArrayList<>();
		createRooms();
		parser = new Parser();
	}

	/**
	 * Create all the rooms and link their exits together.
	 */
	private void createRooms() {

		// create the rooms
		entranceHall = new Room("outside the main entrance of Hogwarts");
		muggleClassroom = new Room("in the Muggle Classroom");
		greatHall = new Room("in the Great Hall");
		astronomyTower = new Room("in the Astronomy Tower");
		dumbledoresOffice = new Room("in Dumbledore's office");
		forbiddenForest = new Room("in the Forbidden Forest....your trapped");

		// initialise room exits
		entranceHall.setExits("east", muggleClassroom);
		entranceHall.setExits("south", astronomyTower);
		entranceHall.setExits("west", greatHall);
		entranceHall.setExits("north", forbiddenForest);
		entranceHall.setItems("Rock", 15);
		muggleClassroom.setExits("west", entranceHall);
		greatHall.setExits("east", entranceHall);
		astronomyTower.setExits("north", entranceHall);
		astronomyTower.setExits("east", dumbledoresOffice);
		astronomyTower.setItems("Telescope", 15);
		dumbledoresOffice.setExits("west", astronomyTower);
		dumbledoresOffice.setItems("Dumbledore", 100);

		rooms.add(entranceHall);
		rooms.add(muggleClassroom);
		rooms.add(greatHall);
		rooms.add(astronomyTower);
		rooms.add(dumbledoresOffice);
		rooms.add(forbiddenForest);

		player1.setCurrentRoom(entranceHall); // start game outside

	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();

		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
			if (player1.getMoves() < 1) {
				System.out.println("You ran out of Moves");
				finished = true;
			}
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Hogwarts!");
		System.out.println("Hogwarts is where you'll learn to be the dork you always dreamed you could be.");
		System.out.println("Don't run out of moves or you will DIE");
		System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
		System.out.println();
		player1.printLocationInfo();
	}

	/**
	 * Given a command, process (that is: execute) the command.
	 * 
	 * @param command
	 *            The command to be processed.
	 * @return true If the command ends the game, false otherwise.
	 */
	private boolean processCommand(Command command) {
		boolean wantToQuit = false;

		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}

		String commandWord = command.getCommandWord();
		if (commandWord.equals("help")) {
			printHelp();
		} else if (commandWord.equals("go")) {
			goRoom(command);
		} else if (commandWord.equals("quit")) {
			wantToQuit = quit(command);
		} else if (commandWord.equals("look") && !command.hasSecondWord()) {
			player1.look();
		} else if (commandWord.equals("swim")) {
			player1.swim();
		} else if (commandWord.equals("back")) {
			player1.back(command);
		} else if (commandWord.equals("pickup")) {
			player1.pickup(command);
		} else if (commandWord.equals("drop")) {
			player1.drop(command);
		} else if (commandWord.equals("check")) {
			player1.lookInSatchel();
		} else if (commandWord.equals("eat")) {
			player1.eatCookie(command);
		}
		return wantToQuit;
	}

	// implementations of user commands:

	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at the university.");
		parser.showCommands();
	}

	/**
	 * Try to go in one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = null;
		player1.pushRoom(player1.getCurrentRoom());
		if (Math.random() > .35) {
			nextRoom = player1.getCurrentRoom().getExit(direction);
		} else {
			nextRoom = getRandomRoom();
		}

		if (nextRoom == null) {
			System.out.println("There is no door!");
		} else {
			player1.setCurrentRoom(nextRoom);
			player1.addMove();
			player1.printLocationInfo();
		}
	}

	private Room getRandomRoom() {
		Room room = null;
		System.out.println("You have been transported to a random room");
		int roomNum = (int) (Math.random() * 5 + 1);
		room = rooms.get(roomNum);
		return room;
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we
	 * really quit the game.
	 * 
	 * @return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) {
		if (command.hasSecondWord()) {
			System.out.println("Quit what?");
			return false;
		} else {
			return true; // signal that we want to quit
		}
	}
}
