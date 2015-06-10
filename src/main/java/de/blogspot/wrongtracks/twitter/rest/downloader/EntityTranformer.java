package de.blogspot.wrongtracks.twitter.rest.downloader;

import java.io.File;
import java.util.Scanner;

import twitter4j.TwitterObjectFactory;
import twitter4j.User;


public class EntityTranformer {

	public static void main(String[] args) throws Exception {
		File file = new File("/Users/ronny/Entwicklung/workspace/AIM_workspace/Graphs/rbraeunlich");
		try(Scanner scanner = new Scanner(file)){
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.equals("[")){
					continue;
				}
				else{
					User user = TwitterObjectFactory.createUser(line.substring(line.indexOf('{'), line.lastIndexOf(',')));
					System.out.println(user);
				}
			}
		}
	}
}
