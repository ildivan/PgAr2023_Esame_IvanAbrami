package module42.person;

import module42.error.InvalidInputException;
import module42.fiscalcode.FiscalCodeGenerator;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents a person, contains all data to work with personal fiscal codes.
 */
public class Person{

    private final String name;
    private final String surname;
    private final Sex sex;
    private final String cityOfBirth;
    private final String dateOfBirth;

    private String fiscalCode;
    private final String dateOfExpiration;

    /**
     * @param name         Name of the person.
     * @param surname      Surname of the person.
     * @param sex          Can be male (Sex.M) or female (Sex.F)
     * @param cityOfBirth  String containing the city of birth of the person.
     * @param dateOfBirth  String representing the person's date of birth formatted this way "YYYY-MM-DD"
     */
    public Person(String name, String surname, Sex sex,
                  String cityOfBirth, String dateOfBirth, String fiscalCode, String dateOfExpiration){
        checkName(name);
        checkName(surname);
        this.name = name;
        this.surname = surname;

        checkSex(sex);
        this.sex = sex;

        //Deletes every character that is not allowed
        this.cityOfBirth = cityOfBirth.replaceAll("[^'\\-\\sa-zA-Z]","");

        checkDateFormat(dateOfBirth);
        String[] dividedBirthDate = dateOfBirth.split("-");
        int yearOfBirth = Integer.parseInt(dividedBirthDate[0]);
        int monthOfBirth = Integer.parseInt(dividedBirthDate[1]);
        int dayOfBirth = Integer.parseInt(dividedBirthDate[2]);

        checkDateValidity(yearOfBirth, monthOfBirth, dayOfBirth);
        this.dateOfBirth = dateOfBirth;

        checkDateFormat(dateOfExpiration);
        this.dateOfExpiration = dateOfExpiration;

        this.fiscalCode = fiscalCode;
    }


    private void checkName(String name){
        //Checks that the name is only letters and at least 2 letters long.
        boolean onlyLetters = name.matches("[A-Za-z][A-Za-z]+");
        //Checks that there are at least two vocals in the name.
        boolean atLeastTwoVocals = name.matches(".*[AEIOUaeiou].*[AEIOUaeiou].*");
        //Checks that there are at least a vocal and a consonant.
        boolean containsVocalAndConsonant =
                name.matches("((.*[AEIOUaeiou].*[^AEIOUaeiou].*)|(.*[^AEIOUaeiou].*[AEIOUaeiou].*))");

        boolean isValid = onlyLetters && (atLeastTwoVocals || containsVocalAndConsonant);

        if(!isValid){
            throw new InvalidInputException(String.format("Name not valid: %s",name));
        }
    }

    private void checkSex(Sex sex){
        if(sex == null) throw new InvalidInputException("Sex not valid, it is null");
    }

    private static void checkDateFormat(String dateOfBirth) {
        boolean validFormat = dateOfBirth.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})");
        if (!validFormat) {
            throw new InvalidInputException(String.format("Date is not formatted correctly: %s", dateOfBirth));
        }
    }

    private void checkDateValidity(int year, int month, int day) {
        LocalDate today = LocalDate.now();

        checkYear(year,today);
        checkMonth(month, year,today);
        checkDay(day, month, year,today);
    }

    private void checkYear(int year, LocalDate today) {
        int currentYear = today.getYear();

        if (year > currentYear) {
            throw new InvalidInputException("Year of birth not valid.");
        }
    }

    private void checkMonth(int month, int year, LocalDate today) {
        boolean isMonthValid;

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        if (year == currentYear) {
            isMonthValid = month <= currentMonth;
        }else{
            isMonthValid = month <= 12;
        }

        if (!isMonthValid) {
            throw new InvalidInputException("Month of birth not valid.");
        }
    }

    private void checkDay(int day, int month, int year, LocalDate today) {
        boolean isDayValid;

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        if (year == currentYear && month == currentMonth) {
            isDayValid = day <= currentDay;
        }else{
            YearMonth yearMonthObject = YearMonth.of(year, month);
            int daysInMonth = yearMonthObject.lengthOfMonth();
            isDayValid = day <= daysInMonth;
        }

        if (!isDayValid) {
            throw new InvalidInputException("Day of birth not valid.");
        }
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Sex getSex() {
        return sex;
    }

    public String getCityOfBirth() {
        return cityOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public String getDateOfExpiration() {return dateOfExpiration;}

    public int getYearOfBirth() {
        return Integer.parseInt(dateOfBirth.substring(0,4));
    }

    public int getMonthOfBirth() {
        return Integer.parseInt(dateOfBirth.substring(5,7));
    }

    public int getDayOfBirth() {
        return Integer.parseInt(dateOfBirth.substring(8,10));
    }

    /**
     * Set fiscalCode to an instance created by the FiscalCodeGenerator passed as argument.
     * @param generator FiscalCodeGenerator that generates the fiscal code ensuring its correctness.
     */
    public void setFiscalCode(FiscalCodeGenerator generator) {
        this.fiscalCode = generator.generateFiscalCode(this);
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

}
