package jASUtils;

import java.sql.*;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jASUtils.MyDBConnector;

public class Mailer {

	public static String mailAuth() {
		final String getAuthSQL = "SELECT Value FROM TableWithGmailPasswordEncrypted WHERE Item='ColumnWithEncryptedGmailPassword' LIMIT 1;";
		String password = null;
		try (
			Connection conn = MyDBConnector.getMyConnection();
			Statement stmt = conn.createStatement();
			ResultSet resultSetGetAuth = stmt.executeQuery(getAuthSQL)
		) {
			while (resultSetGetAuth.next()) { password = resultSetGetAuth.getString("Value"); }
		} catch (Exception e) { e.printStackTrace(); }
		return password;
	}


	public static void sendMail(String sendTo, String messageSubject, String messageContent) {

		final String username = "Email@Gmail.com";
		final String password = mailAuth();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
			message.setSubject(messageSubject);
			message.setText(messageContent);
			Transport.send(message);
			System.out.println(" -> Mail sent!");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String args[]) {

		System.out.println("Send mail class!");

	}

}
