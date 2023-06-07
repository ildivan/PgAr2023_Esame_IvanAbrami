package module42.fiscalcode;

import CodiceFiscale.error.InvalidInputException;

/**
 * Wrapper for a string representing a fiscal code, ensures the user the validity of the code
 * by being instantiable by the package either from a FiscalCodeGenerator or from a string
 * and a FiscalCodeChecker that will throw an exception if it is not valid.
 */
public class FiscalCode {
    private final String code;
    //Static instance representing that the code is absent.
    public static final FiscalCode ABSENT = new FiscalCode();

    private FiscalCode(){
        code = "ASSENTE";
    }

    //Lets you create a fiscal code instance,
    // but a FiscalCodeChecker is needed to ensure the correctness of the code.
    public FiscalCode(String fiscalCode, FiscalCodeChecker checker){
        if(checker.isValid(fiscalCode)){
            code = fiscalCode;
            return;
        }
        throw new InvalidInputException("Codice fiscale non valido.");
    }

    //Protected constructor, intended to be used ONLY in FiscalCodeGenerator.
    protected FiscalCode(String generatedFiscalCode){
        code = generatedFiscalCode;
    }

    /**
     * @return The fiscal code.
     */
    public String getCode() {
        return code;
    }
}
