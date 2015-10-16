package com.tdw.auth;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class AuthDAOTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateSalt(){
		String salt = AuthDAO.createSalt();
		for(int i=0;i<1000;i++){
			assertTrue(salt.length() > 100);
			assertTrue(salt.length() < 200);
			String tmp = AuthDAO.createSalt();
			assertNotEquals(salt, tmp);			
		}
	}

	@Test
	public void testHashString(){
		String[] tests = {"I really like tacos","You really don't","Blab blah blah"};
		String[] hashes = new String[]{
			AuthDAO.hashString(tests[0]),	
			AuthDAO.hashString(tests[1]),	
			AuthDAO.hashString(tests[2]),	
		};
		assertNotEquals(hashes[0], hashes[1]);
		assertNotEquals(hashes[0], hashes[2]);
		assertNotEquals(hashes[1], hashes[2]);
		
		assertNotEquals(hashes[0], tests[1]);
		assertNotEquals(hashes[0], tests[2]);
		assertNotEquals(hashes[1], tests[2]);
		
		assertEquals(hashes[0],AuthDAO.hashString(tests[0]));
		assertEquals(hashes[1],AuthDAO.hashString(tests[1]));
		assertEquals(hashes[2],AuthDAO.hashString(tests[2]));
	}

	@Test
	public void testAuthDAO() {
		User user = new User();
        user.setEmail("kecoproductions@gmail.com");
        user.setPasswordHash("asdfasfd");
        user.setSalt("boogers");
        user.setUserName("kdavies");
        
        
	}

	@Test
	public void testAddUser() throws UserException {
		User user = new User();
        user.setEmail("kecoproductions@gmail.com");
        user.setUserName("kdavies");
        
        AuthDAO authDAO = new AuthDAO();
        authDAO.addUser(user, "Foobar1");
	}

	@Test
	public void testGetUserByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

}
