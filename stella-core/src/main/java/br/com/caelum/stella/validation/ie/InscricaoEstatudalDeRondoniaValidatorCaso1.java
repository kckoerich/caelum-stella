package br.com.caelum.stella.validation.ie;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.caelum.stella.MessageProducer;
import br.com.caelum.stella.constraint.IEConstraints;
import br.com.caelum.stella.validation.error.IEError;
import br.com.caelum.stella.validation.*;

class InscricaoEstatudalDeRondoniaValidatorCaso1 extends
        BaseValidator<String> {

    private static final int MOD = 11;

    // TAMANHO = 9;
    private static final String MISSING_LEFT_SIDE_ZEROS = "00000";

    private static final int DVX_POSITION = MISSING_LEFT_SIDE_ZEROS.length() + 9;
    private static final Integer[] DVX_MULTIPLIERS = IEConstraints.P4;

    private static final RotinaDeDigitoVerificador[] rotinas = {
            IEConstraints.Rotina.E, IEConstraints.Rotina.POS_IE };

    private static final DigitoVerificadorInfo DVX_INFO = new DigitoVerificadorInfo(
            1, rotinas, MOD, DVX_MULTIPLIERS, DVX_POSITION);

    private static final ValidadorDeDV DVX_CHECKER = new ValidadorDeDV(DVX_INFO);

    private final boolean isFormatted;

    public static final Pattern FORMATED = Pattern
            .compile("([1-9]\\d{2})[.](\\d{5})[-](\\d{1})");
    public static final Pattern UNFORMATED = Pattern
            .compile("([1-9]\\d{2})(\\d{5})(\\d{1})");

    public InscricaoEstatudalDeRondoniaValidatorCaso1(
            MessageProducer messageProducer, boolean isFormatted) {
        super(messageProducer);
        this.isFormatted = isFormatted;
    }

    @Override
    protected List<InvalidValue> getInvalidValues(String IE) {
        List<InvalidValue> errors = new ArrayList<InvalidValue>();
        errors.clear();
        if (IE != null) {
            String unformatedIE = checkForCorrectFormat(IE, errors);
            if (errors.isEmpty()) {
                if (!hasValidCheckDigits(unformatedIE)) {
                    errors.add(IEError.INVALID_CHECK_DIGITS);
                }
            }
        }
        return errors;
    }

    private String checkForCorrectFormat(String ie, List<InvalidValue> errors) {
        String unformatedIE = null;
        if (isFormatted) {
            if (!(FORMATED.matcher(ie).matches())) {
                errors.add(IEError.INVALID_FORMAT);
            }
            unformatedIE = ie.replaceAll("\\D", "");
        } else {
            if (!UNFORMATED.matcher(ie).matches()) {
                errors.add(IEError.INVALID_DIGITS);
            }
            unformatedIE = ie;
        }
        return unformatedIE;
    }

    private boolean hasValidCheckDigits(String value) {
        String testedValue = MISSING_LEFT_SIDE_ZEROS + value;
        return DVX_CHECKER.isDVValid(testedValue);
    }

}
