package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * 
 * @author kamil
 *
 */
public class Validation {
	
	public static boolean validateEmail(String email) {
		return Pattern.matches("^([a-zA-Z0-9])+([a-zA-Z0-9\\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\\._-]+)+$", email);
	}

	/**
	 * Date validation function
	 * @param dateString
	 * @return parsed Date or null if cannot parse, or date greater than now
	 */
	public static Date validateDate(String dateString) {
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date date;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
		Date now = new Date();
		if (date.after(now))
			return null;
		
		return date;
	}

	public static boolean validateMandatoryWord(String word) {
		return Pattern.matches("^[a-zA-Z]+$", word);
	}
	
	public static boolean validateOptionalWord(String word) {
		return (word.length() == 0) || validateMandatoryWord(word);
	}
}
