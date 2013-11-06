package com.postmark;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.MailSendException;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class TestMailSender {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());	

	static String apiKey;
	
	static final String VALID_EMAIL_FROM = "test@exemple.com";
	static final String VALID_EMAIL_TO = "test@exemple.com";
	static final String INVALID_EMAIL = "test-exemple.com";
	
	PostmarkMailSender mailSender;
    
    @Before
    public void setUp() {
        helper.setUp();
        Properties prop = new Properties();
        InputStream source = this.getClass().getResourceAsStream("/api.properties");
        try {
			prop.load(source);
			source.close();
			apiKey = prop.getProperty("TEST_API_KEY");
			mailSender = new PostmarkMailSender(apiKey);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }	
	
    @Test
	public void testSendMail() {
		PostmarkMessage m = new PostmarkMessage();
		m.setFrom(VALID_EMAIL_FROM);
		m.setTo(VALID_EMAIL_TO);
		m.setSubject("Test Mail");
		m.setText("This is the body\n" +
				"these are accents é à è ' etc..");
		m.setTag("test-utf8");
		mailSender.send(m);
	}

	//@Test
	public void testSendMails() {
		PostmarkMessage msg[] = new PostmarkMessage[4];
		for(int i=0; i<msg.length; i++) {
			PostmarkMessage m = msg[i]
					= new PostmarkMessage();
			m.setFrom(VALID_EMAIL_TO);
			m.setTo(VALID_EMAIL_TO);
			m.setSubject("Test multiple mails #" + i);
			m.setText("Testing sending of multiple emails.");
			m.setTag("test-multiple");
		}
		msg[0].setTo(INVALID_EMAIL);
		msg[2].setFrom(INVALID_EMAIL);
		
		try {
			mailSender.send(msg);
			fail("No Exception arose whilst giving incorrect e-mail addresses.");
		
		} catch(MailSendException mse) {
			assertEquals(2, mse.getFailedMessages().size());
		}
	}

	private void l(Object log){
		System.out.print(String.valueOf(log) + "\n"); 
	}	
	
}
