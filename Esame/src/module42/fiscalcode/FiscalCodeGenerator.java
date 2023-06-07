package module42.fiscalcode;

import module42.error.InvalidInputException;
import module42.person.Person;
import module42.person.Sex;

import java.util.Map;

public class FiscalCodeGenerator {
    private final Map<String, String> cityCodes;

    public FiscalCodeGenerator(Map<String, String> cityCodes) {
        this.cityCodes = cityCodes;
    }


    public String generateFiscalCode(Person person){
        StringBuilder fiscalCode = new StringBuilder();
        fiscalCode.append(generateCodeForSurname(person.getSurname().toUpperCase()));
        fiscalCode.append(generateCodeForName(person.getName().toUpperCase()));
        int yearNumber = person.getYearOfBirth()%100;
        fiscalCode.append((yearNumber < 10 ? String.format("0%d",yearNumber) : yearNumber));
        fiscalCode.append(getMonthCode(person.getMonthOfBirth()));
        int month = (person.getSex() == Sex.M ? person.getDayOfBirth() : person.getDayOfBirth() + 40);
        fiscalCode.append((month < 10 ? "0" + month : month));
        fiscalCode.append(getPersonCityCode(person.getCityOfBirth()));
        fiscalCode.append(getControlCharacter(fiscalCode.toString()));

        return fiscalCode.toString();
    }

    private char getControlCharacter(String incompleteFiscalCode) {
        FiscalCodeChecker checker = new FiscalCodeChecker(cityCodes);
        return checker.calculateControlCharacter(incompleteFiscalCode);
    }

    private String generateCodeForName(String name) {

        char[] vocals = getVocals(name);
        char[] consonants = getConsonants(name);
        StringBuilder code = new StringBuilder();

        if(consonants.length >= 4){
            code.append(consonants[0]);
            code.append(consonants[2]);
            code.append(consonants[3]);
        } else if(consonants.length == 3){
            code.append(consonants[0]);
            code.append(consonants[1]);
            code.append(consonants[2]);
        } else if (consonants.length == 2) {
            code.append(consonants[0]);
            code.append(consonants[1]);
            code.append(vocals[0]);
        }else if (consonants.length == 1) {
            code.append(consonants[0]);
            if(vocals.length >= 2) {
                code.append(vocals[0]);
                code.append(vocals[1]);
            }else{
                code.append(vocals[0]);
                code.append('X');
            }
        }else{
            code.append(vocals[0]);
            code.append(vocals[1]);
            code.append('X');
        }
        
        return code.toString();
    }

    private String generateCodeForSurname(String surname) {

        char[] vocals = getVocals(surname);
        char[] consonants = getConsonants(surname);
        StringBuilder code = new StringBuilder();

        if(consonants.length >= 3){
            code.append(consonants[0]);
            code.append(consonants[1]);
            code.append(consonants[2]);
        }else if (consonants.length == 2) {
            code.append(consonants[0]);
            code.append(consonants[1]);
            code.append(vocals[0]);
        }else if (consonants.length == 1) {
            code.append(consonants[0]);
            if(vocals.length >= 2) {
                code.append(vocals[0]);
                code.append(vocals[1]);
            }else{
                code.append(vocals[0]);
                code.append('X');
            }
        }else{
            code.append(vocals[0]);
            code.append(vocals[1]);
            code.append('X');
        }

        return code.toString();
    }

    private char[] getConsonants(String name) {
        return name.replaceAll("[AEIOUaeiou]", "").toCharArray();
    }

    private char[] getVocals(String name) {
        return name.replaceAll("[^AEIOUaeiou]","").toCharArray();
    }

    private char getMonthCode(int monthOfBirth) {
        switch(monthOfBirth){
            case 1 -> {return 'A';}
            case 2 -> {return 'B';}
            case 3 -> {return 'C';}
            case 4 -> {return 'D';}
            case 5 -> {return 'E';}
            case 6 -> {return 'H';}
            case 7 -> {return 'L';}
            case 8 -> {return 'M';}
            case 9 -> {return 'P';}
            case 10 -> {return 'R';}
            case 11 -> {return 'S';}
            case 12 -> {return 'T';}
            default -> {return ' ';}
        }
    }

    private String getPersonCityCode(String cityOfBirth) {
        if(cityCodes.containsKey(cityOfBirth)){
            return cityCodes.get(cityOfBirth);
        }
        throw new InvalidInputException(String.format("City does not exist %s",cityOfBirth));
    }
}
