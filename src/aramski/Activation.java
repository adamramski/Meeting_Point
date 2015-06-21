package aramski;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class Activation {

	@ManagedProperty(value="#{param.key}")
	private String key;

	private boolean valid;

	@PostConstruct
	public void init() {
		setValid(check(key)); // And auto-login if valid?

	}

	// [x] dodac do tabeli uzytkownikow w bazie danych pole activationKey oraz isActive
	// [x] sesje z userem odpalac dopiero przy udanym logowaniu
	// [x] w metodzie check ustawiamy isActive = true, dla rekordu z takim samym activationKey
	// [x] w metodzie logowania sprawdzamy czy konto aktywne

	public boolean check(String key) {
		try {
			Tools.updateDatabase("UPDATE mysociety.uzytkownicy SET isActive=1 WHERE activationKey='" + key +"';");
			return true;
		} catch (SQLException e) {
			Tools.printSQLException(e);
			return false;
		} catch (Exception ee) {
			ee.printStackTrace();
			return false;
		}
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
