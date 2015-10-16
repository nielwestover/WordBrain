package com.tdw.auth;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class AuthDAO {

	static DynamoDBMapper mapper;
	static AmazonDynamoDBClient client;

	static {
		client = new AmazonDynamoDBClient();
		client.setEndpoint("http://localhost:8000");
		mapper = new DynamoDBMapper(client);
		if(!Tables.doesTableExist(client, "Users")){
			CreateTableRequest request = mapper.generateCreateTableRequest(User.class);
			request.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
			client.createTable(request);
		}
	}

	public User googleAuthenticateUser(String idTokenString) throws GeneralSecurityException, IOException{
		NetHttpTransport transport = new NetHttpTransport();
		GsonFactory gson = new GsonFactory();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gson)
		.setAudience(Arrays.asList("1096714292477-vvneb64mkjhsqah59aujbvkggqlpgfei.apps.googleusercontent.com"))
		.build();

		// (Receive idTokenString by HTTPS POST)

		GoogleIdToken idToken = verifier.verify(idTokenString);
		if (idToken == null) {
			throw new IOException("Google Authentication Failed");
		}
		Payload payload = idToken.getPayload();
		String email = payload.getEmail();
		User user = getUserByEmail(email);
		if(user!=null){
			return user;
		}
		
		user = new User();
		user.setEmail(email);
		user.setUserName((String)payload.get("name"));
		
		AuthDAO.mapper.save(user);
		return user;
	}

	public AuthDAO(){
	}

	public static String hashString(String text){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		md.update(text.getBytes()); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest);

	}

	public static String createSalt(){
		SecureRandom random = new SecureRandom();
		int count = (Math.abs(random.nextInt()) % 50)+50; // salt will be between 50 and 100 characters
		byte bytes[] = new byte[count];
		random.nextBytes(bytes);
		return DatatypeConverter.printHexBinary(bytes);
	}

	public User addUser(User user, String password) throws UserException {
		if(empty(user.getUserName())){
			throw new UserException("User name must be specified when adding user");
		}

		if(empty(user.getEmail())){
			throw new UserException("User email must be specified when adding user");
		}

		if(empty(password)){
			throw new UserException("User password must be specified when adding user");
		}
		emailCheck(user.getEmail());
		passwordCheck(password);

		User already = getUserByEmail(user.getEmail());

		if(already!=null){
			throw new UserException("User with email'"+user.getEmail()+"' already exists");
		}

		user.setId(null);
		String salt = createSalt();
		String hash = hashString(salt+password);
		user.setSalt(salt);
		user.setPasswordHash(hash);
		AuthDAO.mapper.save(user);

		return user;
	}

	private void emailCheck(String email) throws UserException {
		// superficial valid email checking
		int at = email.indexOf("@");
		if(at<1){
			throw new UserException("Invalid email address");
		}
		int dot = email.lastIndexOf(".");
		if(dot < at){
			throw new UserException("Invalid email address");
		}
	}

	public User authenticateUser(String email, String password) throws UserException{
		User user = getUserByEmail(email);
		if(user==null){
			throw new UserException("Invalid user name and password combination");
		}
		//System.out.println(user.getSalt()+" "+user.getPasswordHash());
		String hash = hashString(user.getSalt()+password);
		//System.out.println(hash);
		if(hash.equals(user.getPasswordHash())){
			return user;
		}
		else {
			throw new UserException("Invalid user name and password combination");
		}

	}

	public List<User> getAllUsers(){
		return mapper.scan(User.class, new DynamoDBScanExpression());
	}

	public void deleteUser(User user){
		AuthDAO.mapper.delete(user);
	}

	public static void passwordCheck(String password) throws UserException {
		String passwordReq = "Password must contain at least one capital letter, one lower case letter, and one non alphabetic character";
		if(!password.matches(".*[A-Z].*")){
			throw new UserException(passwordReq);
		}
		if(!password.matches(".*[a-z].*")){
			throw new UserException(passwordReq);
		}
		if(!password.matches(".*[^A-Za-z].*")){
			throw new UserException(passwordReq);
		}
	}

	private boolean empty(String string){
		return string==null || string.isEmpty();
	}

	public User getUserByName(String user){
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition scanCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue(user));

		scanFilter.put("UserName", scanCondition);

		scanExpression.setScanFilter(scanFilter);

		List<User> tmp = mapper.scan(User.class, scanExpression);
		if(tmp.size()>0){
			return tmp.get(0);
		}
		return null;
	}

	public User getUserByEmail(String email){
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		Map<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition scanCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue(email));

		scanFilter.put("Email", scanCondition);

		scanExpression.setScanFilter(scanFilter);

		List<User> tmp = mapper.scan(User.class, scanExpression);
		if(tmp.size()>0){
			return tmp.get(0);
		}
		return null;
	}

}