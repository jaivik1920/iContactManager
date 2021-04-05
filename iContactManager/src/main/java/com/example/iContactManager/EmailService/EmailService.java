package com.example.iContactManager.EmailService;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject,String message,String to) {
		
		boolean send=false;
		try {
		Properties properties=new Properties();
		properties.put("mail.smtp.host","smtp.gmail.com");
		properties.put("mail.smtp.auth","true");
		properties.put("mail.debug","true");
        properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.socketFactory.port","465");
		properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback","false");
        properties.setProperty("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.user", "appleindialtd.id@gmail.com");

		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			 protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication("appleindialtd.id@gmail.com", "Angel@1920");
	            }
	        });
	    session.setDebug(true);
        MimeMessage message1 = new MimeMessage(session);
        InternetAddress addressform=new InternetAddress("appleindialtd.id@gmail.com");
        message1.setFrom(addressform);
        InternetAddress addressto=new InternetAddress(to);
        message1.addRecipient(Message.RecipientType.TO, addressto);
        
        message1.setSubject(subject);
        message1.setContent(message,"text/html");
        Transport.send(message1);
        send=true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return send;
		
		
		
	}
	

}
