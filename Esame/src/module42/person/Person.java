package module42.person;

import CodiceFiscale.error.InvalidInputException;
import CodiceFiscale.fiscalcode.FiscalCode;
import CodiceFiscale.fiscalcode.FiscalCodeGenerator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents a person, contains all data to work with personal fiscal codes.
 */
public class Person{

    @SerializedName("nome")
    private final String name;
    @SerializedName("cognome")
    private final String surname;
    @SerializedName("sesso")
    private final Sex sex;
    @SerializedName("comune_nascita")
    private final String cityOfBirth;
    @SerializedName("data_nascita")
    private final String dateOfBirth;

    @Expose(deserialize = false)
    @SerializedName("codice_fiscale")
    private FiscalCode fiscalCode;

    /**
     * @param name         Name of the person.
     * @param surname      Surname of the person.
     * @param sex          Can be male (Sex.M) or female (Sex.F)
     * @param cityOfBirth  String containing the city of birth of the person.
     * @param dateOfBirth  String representing the person's date of birth formatted this way "YYYY-MM-DD"
     */
    public Person(String name, String surname, Sex sex,
                  String cityOfBirth, String dateOfBirth){
        checkName(name);
        checkName(surname);
        this.name = name;
        this.surname = surname;

        checkSex(sex);
        this.sex = sex;

        //Deletes every character that is not allowed
        this.cityOfBirth = cityOfBirth.replaceAll("[^'\\-\\sa-zA-Z]","");

        checkDateFormat(dateOfBirth);
        String[] dividedDate = dateOfBirth.split("-");
        int yearOfBirth = Integer.parseInt(dividedDate[0]);
        int monthOfBirth = Integer.parseInt(dividedDate[1]);
        int dayOfBirth = Integer.parseInt(dividedDate[2]);

        checkDateValidity(yearOfBirth, monthOfBirth, dayOfBirth);
        this.dateOfBirth = dateOfBirth;

        fiscalCode = FiscalCode.ABSENT;
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

    public FiscalCode getFiscalCode() {
        return fiscalCode;
    }

    /**
     * Resets the person's fiscal code to an instance containing the default value.
     */
    public void resetFiscalCode() {
        fiscalCode = FiscalCode.ABSENT;
    }
}
