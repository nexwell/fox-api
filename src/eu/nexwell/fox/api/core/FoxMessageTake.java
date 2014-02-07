package eu.nexwell.fox.api.core;

import java.util.Scanner;

class FoxMessageTake extends FoxMessage {

	private int slotIndex;
	private String takeArgs;
	
	@Override
	protected void prepareMessage() {
		
	}

	@Override
	protected void interpretMessage() {
		slotIndex = -1;
		takeArgs = "";
		if (message.matches("take .+ .+")) {
			Scanner scanner = new Scanner (message);
			scanner.next();
			slotIndex = scanner.nextInt();
			takeArgs = scanner.next();
			scanner.close();
		}
	}
	
	int getIndex() {
		return slotIndex;
	}
	
	String getArgs() {
		return takeArgs;
	}

}
