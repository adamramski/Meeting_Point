package aramski;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PostOffice {
	
	public PostOffice() {}

	public static String send(){
		System.out.println("Before1");
		final String username = "adamramski@gmail.com";
		final String password = "frotto11";

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
			message.setFrom(new InternetAddress("adamramski@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("aramski@wi.zut.edu.pl"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
					+ "\n\n No spam to my email, please!");
			
			System.out.println("Before");
			Transport.send(message);

			System.out.println("Done");
			return "show_profile";

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
