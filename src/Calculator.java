import java.util.List;

public class Calculator {
    static boolean isRoman;

    public static String getResult(LexemeBuffer lexemes) {
        if (lexemes.isRoman()) {
            isRoman = true;
            for (Lexeme lexeme : lexemes.getLexemes()) {
                if (lexeme.getType().equals(LexemeType.ROMAN_NUMBER)) {
                    lexeme.setValue(romanToArabic(lexeme.getValue()));
                }
            }
        }
        if (!isMathematicalOperation(lexemes)) {
            throw new IllegalArgumentException("Не является математической операцией");
        }
        int result = expr(lexemes);
        if (isRoman && result<1) {
            throw new IllegalArgumentException("В римской системе нету отрицательных чисел");
        }
        return isRoman ? arabicToRoman(result) : String.valueOf(result);
    }

    public static int expr (LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.getType() == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusMinus(lexemes);
        }
    }

    public static int plusMinus (LexemeBuffer lexemes) {
        int value = multDiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS -> value += multDiv(lexemes);
                case OP_MINUS -> value -= multDiv(lexemes);
                case EOF, RIGHT_BRACKET -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new IllegalArgumentException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    public static int multDiv (LexemeBuffer lexemes) {
        int value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL -> value *= factor(lexemes);
                case OP_DIV -> value /= factor(lexemes);
                case EOF, RIGHT_BRACKET, OP_PLUS, OP_MINUS -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new IllegalArgumentException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    public static int factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.getType()) {
            case NUMBER:
            case ROMAN_NUMBER:
                return Integer.parseInt(lexeme.getValue());
            case LEFT_BRACKET:
                int value = expr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.getType() != LexemeType.RIGHT_BRACKET) {
                    throw new IllegalArgumentException("Unexpected token: " + lexeme.getValue() + " at position" + lexemes.getPos());
                }
                return value;
            default:
                throw new IllegalArgumentException("Unexpected token: " + lexeme.getValue() + " at position" + lexemes.getPos());
        }
    }

    public static String romanToArabic(String input) {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumbers> romanNumerals = RomanNumbers.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumbers symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
        }

        return String.valueOf(result);
    }

    public static String arabicToRoman(int result) {
        if ((result <= 0) || (result > 4000)) {
            throw new IllegalArgumentException(result + " is not in range (0,4000]");
        }

        List<RomanNumbers> romanNumerals = RomanNumbers.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((result > 0) && (i < romanNumerals.size())) {
            RomanNumbers currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= result) {
                sb.append(currentSymbol.name());
                result -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }

    public static boolean isMathematicalOperation (LexemeBuffer lexemes) {
        int digitCount = 0;
        boolean hasOperator = false;
        for (Lexeme lexeme : lexemes.getLexemes()) {
            if (lexeme.getType().equals(LexemeType.NUMBER) || lexeme.getType().equals(LexemeType.ROMAN_NUMBER)) {
                digitCount++;
            } else if (lexeme.getType().equals(LexemeType.OP_MINUS) ||
                    lexeme.getType().equals(LexemeType.OP_PLUS) ||
                    lexeme.getType().equals(LexemeType.OP_DIV) ||
                    lexeme.getType().equals(LexemeType.OP_MUL)) {
                hasOperator = true;
            }
        }
        return hasOperator && digitCount == 2;
    }
}
