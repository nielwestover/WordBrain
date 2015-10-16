package com.tdw.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.gson.Gson;
import com.tdw.auth.UserException;
import com.tdw.auth.UserSummary;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticationRest {
	static {
		System.out.println("Starting up auth/rest baby!!");
		init();
	}
	@Context
	private HttpServletRequest request;

	Gson gson = new Gson();

	@GET
	@Path("/hello/{name}")
	public String sayHello(@PathParam("name") String name) throws UserException {
		UserSummary summary = new UserSummary();
		summary.email = "my email yo";
		summary.userName = name;
		String j = gson.toJson(summary);
		return j;
	}

	static AmazonDynamoDBClient dynamoDBClient;
	static DynamoDB dynamoDB;

	@GET
	@Path("/hint/{pack}/{level}/{wordNumber}")
	public String getHint(@PathParam("pack") String pack,
			@PathParam("level") String level,
			@PathParam("wordNumber") int wordNumber) {

		Table table = dynamoDB.getTable("packLevels");
		Item item = table.getItem("puzzleID", pack + "_" + level);
		List<String> answers = item.getList("answers");
		String answer = answers.get(wordNumber);
		return "\"" + answer + "\"";

	}

	@GET
	@Path("/packInfo")
	public String getPackInfo() {

		List<Pair> list = new ArrayList<Pair>();
		list.add(new Pair("Ant", 10));
		list.add(new Pair("Spider", 10));
		list.add(new Pair("Snail", 20));
		list.add(new Pair("Crab", 20));
		list.add(new Pair("Frog", 20));
		list.add(new Pair("Turtle", 20));
		list.add(new Pair("Penguin", 20));
		list.add(new Pair("Snake", 20));
		list.add(new Pair("Rat", 20));
		list.add(new Pair("Sheep", 20));
		list.add(new Pair("Shark", 20));
		list.add(new Pair("Cat", 20));
		list.add(new Pair("Elephant", 20));
		list.add(new Pair("Whale", 20));
		list.add(new Pair("Octopus", 20));
		list.add(new Pair("Pig", 20));
		list.add(new Pair("Lion", 20));
		list.add(new Pair("Squirrel", 20));
		list.add(new Pair("Owl", 20));
		list.add(new Pair("Monkey", 20));
		list.add(new Pair("Student", 20));
		list.add(new Pair("Clown", 20));
		list.add(new Pair("Waiter", 20));
		list.add(new Pair("Policemen", 20));
		list.add(new Pair("Chef", 20));
		list.add(new Pair("Teacher", 20));
		list.add(new Pair("Doctor", 20));
		list.add(new Pair("Astronaut", 20));
		list.add(new Pair("Scientist", 20));
		list.add(new Pair("Alien", 20));
		String result = gson.toJson(list);
		return result;
	}

	class Pair {
		public String pack;
		public int count;

		public Pair(String pack, int count) {
			this.pack = pack;
			this.count = count;
		}
	}

	private static void init() {
		System.out.println("Initalizing DynamoDB");
		AWSCredentialsProvider provider = null;
		try {
			// provider = new InstanceProfileCredentialsProvider();
			provider = new ProfileCredentialsProvider("nielwestover");
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (C:\\Users\\a2558\\.aws\\credentials), and is in valid format.",
					e);
		}
		dynamoDBClient = new AmazonDynamoDBClient(provider);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		dynamoDBClient.setRegion(usWest2);
		dynamoDB = new DynamoDB(dynamoDBClient);
	}
}
