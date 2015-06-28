package aramski;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean( name = "message")
public class Message {
	private String usernameFrom;
	private String usernameTo;
	private String message;
	
	private Statement statement;
	private String query;
	
	@ManagedProperty(value = "#{user}")
	private User user;
	
	public Message() { }

	public Message(String odbiorca, String nadawca, String tresc) {
		usernameTo = odbiorca;
		usernameFrom = nadawca;
		message = tresc;
	}
	
	public String send (String username) {
		try {

			Tools.updateDatabase("INSERT INTO wiadomosci (nadawca, odbiorca, tresc) VALUES ('" + username + "', '" + getUsernameTo() + "', '" + getMessage() + "')");

			System.out.println("wiadomosc wyslana");
			user.retrieveData();
			
			return "show_profile";

		} catch (SQLException e) {
			System.out.println("wiadomosc NIE wyslana");
			Tools.printSQLException(e);
			return "show_profile";
		} catch (Exception ee) {
			System.out.println("wiadomosc NIE wyslana");
			ee.printStackTrace();
			return "show_profile";} 
	}

	public String read (String username) {
		try {
			int idTo;

			query = "SELECT iduzytkownicy FROM uzytkownicy WHERE login = '" + username + "'";
			ResultSet rs = statement.executeQuery(query);
			if(rs.next()) 
				idTo = rs.getInt("iduzytkownicy");
			else
				return "failed";

			query = "SELECT tresc FROM wiadomosci WHERE odbiorca = '" + idTo + "'";
			rs = statement.executeQuery(query);

			if(rs.next()) {
				setMessage(rs.getString("tresc"));

				System.out.println(getMessage());
				return message;
			} else {
				System.out.println("brak wiadomosci");
				return "empty";
			}

		} catch (SQLException e) {
			Tools.printSQLException(e);
		} catch (Exception ee) {
			ee.printStackTrace();
		} 
		return "failed";
	}

	public String getUsernameFrom() {
		return usernameFrom;
	}
	public void setUsernameFrom(String usernameFrom) {
		this.usernameFrom = usernameFrom;
	}
	public String getUsernameTo() {
		return usernameTo;
	}
	public void setUsernameTo(String usernameTo) {
		this.usernameTo = usernameTo;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
