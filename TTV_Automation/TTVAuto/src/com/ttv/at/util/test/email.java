package com.ttv.at.util.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class email {

}

class receiveEmail {
	
}

class sendMail extends Thread {

	String toEmail;
	String subject;
	String content;
	public sendMail (String toEmail, String subject, String content) {
		this.toEmail = toEmail;
		this.subject = subject;
		this.content = content;
	}
	
	static private final String GMAIL_SMTP_HOST_NAME = "smtp.office365.com";
	static private final int GMAIL_SMTP_HOST_PORT = 587;//25;//25;//465;
	static private final String GMAIL_SMTP_AUTH_USER = "duy.tnt@trans-tech.vn";
	static private final String GMAIL_SMTP_AUTH_PWD  = "Longan$#@!";//"";
	static private final String SMTP_TYPE = "smtp";//"smtp";//"smtps";
	static Properties props = new Properties();
	
	static boolean initted_connect = false;
	static void init_connection () {
		initted_connect = true;
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtps.host", GMAIL_SMTP_HOST_NAME);
		props.put("mail.smtp.port", GMAIL_SMTP_HOST_PORT);
		props.put("mail.smtps.auth", "true");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.starttls.required","false");
//		properties.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.protocols","TLSv1.2");

		props.put("mail.smtp.socketFactory.port", GMAIL_SMTP_HOST_PORT);
//		Removed this property for non secured connections and now my non-secured SMTP connection is working in integration with Web Application.
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
	}
	
	static private void send_tester_email (String toEmails, String subject, String content) {
		if (toEmails != null && toEmails.length() > 0) {
			if (!initted_connect)
				init_connection ();
			try {
				Session mailSession = Session.getDefaultInstance(props);
				Transport transport = mailSession.getTransport(SMTP_TYPE);
	
				MimeMessage message = new MimeMessage(mailSession);
				message.setSubject(subject);
				if (content.startsWith("<HTML>"))
					message.setContent(content, "text/html");
				else
					message.setText(content);
				message.setFrom(new InternetAddress(GMAIL_SMTP_AUTH_USER));
	
				// Add Recipient
				ArrayList<String> mail_list = get_mail_list (toEmails);
				for (String toEmail : mail_list)
					message.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(toEmail));
	
				// Transport.send(message
				transport.connect
				  (GMAIL_SMTP_HOST_NAME, GMAIL_SMTP_HOST_PORT, GMAIL_SMTP_AUTH_USER, GMAIL_SMTP_AUTH_PWD);
	
				transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	static private void send_tester_email (String toEmails, String subject, String content, String attachment) {
		if (toEmails != null && toEmails.length() > 0) {
			if (!initted_connect)
				init_connection ();
			try {
				Session mailSession = Session.getDefaultInstance(props);
				Transport transport = mailSession.getTransport(SMTP_TYPE);
	
				MimeMessage message = new MimeMessage(mailSession);
				message.setSubject(subject);
				message.setFrom(new InternetAddress(GMAIL_SMTP_AUTH_USER));
	
				// Add Recipient
				ArrayList<String> mail_list = get_mail_list (toEmails);
				for (String toEmail : mail_list)
					message.addRecipient(Message.RecipientType.TO,
						 new InternetAddress(toEmail));
				
				// Add Message Content
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText(content);
				
				// Add Attachment
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				FileDataSource source = new FileDataSource(attachment);
				attachmentBodyPart.setDataHandler(new DataHandler(source));
				// attachmentBodyPart.setContent(new DataHandler(source), "text/html");
				String file_name = source.getName();
				messageBodyPart.setFileName("filename");
				
				// Process content
				Multipart content_body = new MimeMultipart ();
				content_body.addBodyPart(messageBodyPart);
				content_body.addBodyPart(attachmentBodyPart);
				message.setContent(content_body);
				message.setSentDate(new Date());
	
				// Transport.send(message
				transport.connect
				  (GMAIL_SMTP_HOST_NAME, GMAIL_SMTP_HOST_PORT, GMAIL_SMTP_AUTH_USER, GMAIL_SMTP_AUTH_PWD);
	
				transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
	
	static private ArrayList<String> get_mail_list (String all_email) {
		ArrayList<String> emails = new  ArrayList<String>();
		if (all_email.indexOf(',') > 0){
			String[] plit_mails = all_email.split(",");
			for (String plit_mail : plit_mails)
				if (plit_mail.indexOf(';') > 0) {
					String[] plit_emails = plit_mail.split(";");
					for (String plit_email : plit_emails)
						emails.add(plit_email);
				}
				else
					emails.add(plit_mail);
		}
		else if (all_email.indexOf(';') > 0) {
			String[] plit_emails = all_email.split(";");
			for (String plit_email : plit_emails)
				emails.add(plit_email);
		}
		else
			emails.add(all_email);
		return emails;
	}
	
	public void run() {
		send_tester_email (toEmail, subject, content);
	}
}