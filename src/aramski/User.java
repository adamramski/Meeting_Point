package aramski;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean( name = "user")
@SessionScoped
public class User {
	private String username;
	private String password, password2;
	private String name;
	private String surname;
	private String email;
	private String activationLink;
	private String activationKey;


	public String getActivationKey() {
		return activationKey;
	}

	private List<String> users;
	private List<Message> inbox;

	public User() {
		users = new ArrayList<String>();
		inbox = new ArrayList<Message>();
		try {
			ResultSet rs = Tools.fetchDatabase("SELECT login FROM uzytkownicy");
			while(rs.next()) {
				users.add(rs.getString("login"));	
				//System.out.println("pobieram uzytkownikow");
			}

		} catch (SQLException e) {
			System.out.println("nie mozna pobrac uzytkownikow");
			Tools.printSQLException(e);
		} catch (Exception ee) {
			System.out.println("nie mozna pobrac uzytkownikow");
			ee.printStackTrace();
		}
	}
	
	// mapowanie obiektowo relacyjne (hybernate)
	// przerobic wiadomosci na czat (jak na fb)
	// dodawanie zdjec publicznych i niepublicznych
	// mozliwosc przegladania galerii innych uzytkownikow
	

	//@PostConstruct
	public void retrieveData() {
		try {
			inbox.clear();
			System.out.println("przed pobraniem wiadomosci");
			ResultSet rs = Tools.fetchDatabase("SELECT nadawca, odbiorca, tresc FROM wiadomosci");// WHERE odbiorca = '" + username + "'" + " AND nadawca = 'admin'");
			//System.out.println("przed pobraniem wiadomosci");
			while(rs.next()) {
				inbox.add(new Message(rs.getString("nadawca"), 
						rs.getString("odbiorca"), 
						rs.getString("tresc")));
				//System.out.println("pobieram wiadomosci");
			}

		}  catch (SQLException e) {
			System.out.println("nie mozna pobrac wiadomosci");
			Tools.printSQLException(e);
		} catch (Exception ee) {
			System.out.println("nie mozna pobrac wiadomosci");
			ee.printStackTrace();
		}
	}

	public String login(){
		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			password = bytesToHex(md.digest());

			ResultSet rs = Tools.fetchDatabase("SELECT imie, nazwisko, login, isActive FROM uzytkownicy "
											+ "WHERE login = '" + username + "' AND haslo1 = '" + password + "'");
			
			if(rs.next()) {
				setName(rs.getString("imie"));				// 103 wi2 313 wi1
				setSurname(rs.getString("nazwisko"));
				setUsername(rs.getString("login"));
				
				if(rs.getInt("isActive") == 0) {
					FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
					return "failed";
				}
				
				retrieveData();
				
				return "show_profile";
			} else {
				return "failed";
			}
		} catch (SQLException e) {
			Tools.printSQLException(e);
			return "failed";
		} catch (Exception ee) {
			ee.printStackTrace();
			return "failed";
		} 
		  
	}

	public String register() {
		try {
			
//			ResultSet rs = Tools.fetchDatabase("SELECT username FROM uzytkownicy WHERE login = '" + username +"'");
//			if(rs.next()){
//				FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//				return "start_page";
//			}
			
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			password = bytesToHex(md.digest());
			md.update(password2.getBytes());
			password2 = bytesToHex(md.digest());
			
			activationKey = UUID.randomUUID().toString();
			activationLink = "https://localhost:8443/Meeting_Point/activate.jsf?key=" + activationKey;

			Tools.updateDatabase("INSERT INTO mysociety.uzytkownicy (imie, nazwisko, login, haslo1, haslo2, email, "
								+ "activationKey, isActive) VALUES "
								+ "('" + name + "','" + surname + "','" + username + "','" + password 
								+ "','" + password2 + "','" + email + "','" + activationKey +"', '0')");
			
			PostOffice.send(email, activationLink);
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			
			System.out.println("registration done, account not active, session off");
			
			return "successful_registration";
		} catch (SQLException e) {
			Tools.printSQLException(e);
			return "start_page";
		} catch (Exception ee) {
			ee.printStackTrace();
			return "start_page";
		}
	}
	
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "start_page";
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public List<String> getUsers() {
		return users;
	}

	public List<Message> getInbox() {
		return inbox;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password) {
		this.password2 = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
