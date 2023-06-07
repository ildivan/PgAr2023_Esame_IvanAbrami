package module42.fiscalcode;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class used to verify the validity of fiscal codes,
 * based on italian fiscal code rules and a map containing city names and codes.
 */
public class FiscalCodeChecker {
    private final Map<String, String> cityCodes;

    /**
     * @param cityCodes <p> Maps city names to city codes,
     *                  passed to the constructor to let the user check on different maps with different checkers.</p>
     */
    public FiscalCodeChecker(Map<String, String> cityCodes) {
        this.cityCodes = cityCodes;
    }

    /**
     * @param fiscalCode A string representing a fiscal code.
     * @return True if the fiscal code is valid, false otherwise.
     */
    public boolean isValid(String fiscalCode){
        return isFormatValid(fiscalCode) && areNameAndSurnameValid(fiscalCode)
                && checkMonth(fiscalCode) && checkDay(fiscalCode)
                && checkCityCode(fiscalCode) && checkControlCharacter(fiscalCode);
    }



    //Returns true if the letters and the digits are in the right place, also checks for length.
    private boolean isFormatValid(String fiscalCode){
        return fiscalCode.matches("[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]");
    }

    //Extracts name and surname codes from the fiscal code and returns true if both are correct.
    private boolean areNameAndSurnameValid(String fiscalCode) {
        String surname = fiscalCode.substring(0,3);
        String name = fiscalCode.substring(3,6);
        return checkName(name) && checkName(surname);
    }

    //Checks the validity of name or surname, uses two regexes for the cases without the trailing 'X'
    //and for the cases with it.
    private boolean checkName(String name){
        //Checks cases without trailing X at the end.
        String withoutXRegex = "([^AEIOUX]{3}|[^AEIOUX]{2}[AEIOU]|[^AEIOUX][AEIOU]{2}|[AEIOU]{3})";
        //Checks cases with trailing X at the end.
        String withXRegex = "([AEIOUX][AEIOU]X|[AEIOU][AEIOU]X)";
        //Returns true if the name matches either of the regular expressions.
        return name.matches(withoutXRegex) || name.matches(withXRegex);
    }

    private boolean checkMonth(String fiscalCode){
        //Checks if the ninth character is one of the allowed characters for the month.
        return fiscalCode.matches(".{8}[ABCDEHLMPRST].{7}");
    }

    //Checks if the day of birth is in the accepted bounds and if it exists in the month of birth.
    private boolean checkDay(String fiscalCode) {
        int day = Integer.parseInt(fiscalCode.substring(9,11));
        int adjustedDay;
        if(day >= 41) {
            adjustedDay = day - 40;
        }else{
            adjustedDay = day;
        }
        Character month = fiscalCode.charAt(8);
        return isDayInBounds(day) && doesDayExistsInMonth(adjustedDay,month);
    }

    //Checks if the given day is in the accepted bounds (1 to 31 for male and 41 to 71 for female)
    private boolean isDayInBounds(int day) {
        return (day >= 1 && day <= 31) || (day >= 41 && day <= 71 );
    }

    //Checks if the day of birth exists in the month of birth,
    //since the year of birth is not determined uniquely, february is considered
    //to always have 28 days.
    private boolean doesDayExistsInMonth(int day, Character month) {
        int daysInTheMonth; //The number of days in the month of birth.
        switch (month) {
            case 'A', 'C', 'E', 'L','R', 'M', 'T' -> daysInTheMonth = 31;
            case 'D', 'H', 'P',  'S' -> daysInTheMonth = 30;
            case 'B' -> daysInTheMonth = 28;
            default -> daysInTheMonth = 0;
        }

        return day <= daysInTheMonth;
    }

    //Checks if the cityCodes map contains the city code in the fiscal code.
    //A fiscal code could be valid to a FiscalCodeChecker that has a particular city and non-valid
    //by another FiscalCodeChecker that does not.
    private boolean checkCityCode(String fiscalCode) {
        String city = fiscalCode.substring(11,15);
        return cityCodes.containsValue(city);
    }

    //Calculates the control character based on the first fifteen digits of the fiscal code and checks if
    //it is the equal to the fiscal code's control character.
    private boolean checkControlCharacter(String fiscalCode) {
        char calculatedControlChar = calculateControlCharacter(fiscalCode.substring(0,fiscalCode.length()-1));
        char realControlChar = fiscalCode.charAt(15);
        return calculatedControlChar == realControlChar;
    }

    //Calculates the control character given the first 15 digits of a fiscal code.
    //Made protected in order to be used in the FiscalCodeGenerator class.
    protected Character calculateControlCharacter(String incompleteFiscalCode) {
        ArrayList<Integer> numericFiscalCode = convertFiscalCodeToNumeric(incompleteFiscalCode);
        int sum = numericFiscalCode.stream().reduce(0, Integer::sum); //sum of numericFiscalCode's elements
        int numericControlChar = sum%26;
        return (char) (numericControlChar + (int)'A'); //Returns a Character from 'A' to 'Z'
    }

    //Transform the fiscal code in a list of integer used to calculate the control character,
    //Each character in the fiscal code is transformed into its value according to the conversion tables.
    private ArrayList<Integer> convertFiscalCodeToNumeric(String fiscalCode) {
        ArrayList<Integer> numericFiscalCode = new ArrayList<>();

        for (int i = 0; i < fiscalCode.length(); i++) {
            int toAdd;
            //The character is treated differently based on wether its position in the fiscal code is even or odd.
            if((i+1)%2 == 0){
                toAdd = convertEvenOrderCharacter(fiscalCode.charAt(i));
            }else{
                toAdd = convertOddOrderCharacter(fiscalCode.charAt(i));
            }
            numericFiscalCode.add(toAdd);
        }

        return numericFiscalCode;
    }

    //Converts a character that is in an even position
    private int convertEvenOrderCharacter(char c) {
        if(Character.isDigit(c)){
            return Character.getNumericValue(c);
        }
        return (int)c - (int)'A';
    }

    //Converts a character that is in an odd position
    private int convertOddOrderCharacter(char c) {
        int value;
        switch (c) {
            case 'A', '0' -> value = 1;
            case 'B', '1' -> value = 0;
            case 'C', '2' -> value = 5;
            case 'D', '3' -> value = 7;
            case 'E', '4' -> value = 9;
            case 'F', '5' -> value = 13;
            case 'G', '6' -> value = 15;
            case 'H', '7' -> value = 17;
            case 'I', '8' -> value = 19;
            case 'J', '9' -> value = 21;
            case 'K' -> value = 2;
            case 'L' -> value = 4;
            case 'M' -> value = 18;
            case 'N' -> value = 20;
            case 'O' -> value = 11;
            case 'P' -> value = 3;
            case 'Q' -> value = 6;
            case 'R' -> value = 8;
            case 'S' -> value = 12;
            case 'T' -> value = 14;
            case 'U' -> value = 16;
            case 'V' -> value = 10;
            case 'W' -> value = 22;
            case 'X' -> value = 25;
            case 'Y' -> value = 24;
            case 'Z' -> value = 23;
            default -> value = -1;
        }
        return value;
    }

}
