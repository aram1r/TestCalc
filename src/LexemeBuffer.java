import java.util.ArrayList;
import java.util.List;

public class LexemeBuffer {
    private boolean hasRoman;
    private int pos;
    private List<Lexeme> lexemes;

    public LexemeBuffer(int pos, List<Lexeme> lexemes) {
        this.pos = pos;
        this.lexemes = lexemes;
    }

    public LexemeBuffer(List<Lexeme> lexemes) {
        this.pos = 0;
        this.lexemes = lexemes;
    }

    public LexemeBuffer(String input) {
        this.pos = 0;
        this.lexemes = lexAnalyze(input);
    }

    public Lexeme next() {
        return lexemes.get(pos++);
    }

    public void back() {
        pos--;
    }

    public int getPos() {
        return pos;
    }

    public List<Lexeme> getLexemes() {
        return lexemes;
    }

    public boolean isRoman() {
        return hasRoman;
    }

    public List<Lexeme> lexAnalyze (String input) {
        boolean hasArabic = false;
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        StringBuilder sb;
        int pos = 0;
        while(pos<input.length()) {
            char c = input.charAt(pos);
            if (isRomanNumber(c)) {
                hasRoman = true;
                if (hasArabic) {
                    throw new IllegalArgumentException("Unexpected character: " + c);
                }
                sb = new StringBuilder();
                do {
                    sb.append(c);
                    pos++;
                    if (pos>= input.length()) {
                        break;
                    }
                    c = input.charAt(pos);
                } while (isRomanNumber(c));
                lexemes.add(new Lexeme(LexemeType.ROMAN_NUMBER, sb.toString()));
            } else {
                    switch (c) {
                        case '(':
                            lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                            pos++;
                            continue;
                        case ')':
                            lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                            pos++;
                            continue;
                        case '+':
                            lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                            pos++;
                            continue;
                        case '-':
                            lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                            pos++;
                            continue;
                        case '*':
                            lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                            pos++;
                            continue;
                        case '/':
                            lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                            pos++;
                            continue;

                        default:
                            if (c<='9' && c>='0') {
                                hasArabic = true;
                                if (hasRoman) {
                                    throw new RuntimeException("Unexpected character: " + c);
                                }
                                sb = new StringBuilder();
                                do {
                                    sb.append(c);
                                    pos++;
                                    if (pos>= input.length()) {
                                        break;
                                    }
                                    c = input.charAt(pos);
                                } while (c<='9' && c>='0');
                                lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                            } else {
                                if (c !=' ') {
                                    throw new IllegalArgumentException("Unexpected character: " + c);
                                }
                                pos++;
                            }
                    }
                }
            }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    static boolean isRomanNumber (Character c) {
        for (int i =0; i<RomanNumbers.values().length; i++) {
            if (c.toString().equals(RomanNumbers.values()[i].toString())) {
                return true;
            }
        }
        return false;
    }


}
