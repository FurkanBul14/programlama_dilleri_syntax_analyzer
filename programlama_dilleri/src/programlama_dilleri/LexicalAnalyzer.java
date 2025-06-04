package programlama_dilleri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LexicalAnalyzer {

    // Daha hızlı arama için keyword listesi Set olarak tutulur
    private static final Set<String> KEYWORDS = Set.of(
        "int", "float", "double", "char", "String",
        "if", "else", "while", "for", "return", "class", "public", "void",
        "break", "continue", "boolean", "true", "false"
    );

    // Ana lexer fonksiyonu (pozisyonsuz)
    public List<Token> analyze(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // WHITESPACE (boşluklar, tab, yeni satır)
            if (Character.isWhitespace(c)) {
                tokens.add(new Token(TokenType.WHITESPACE, String.valueOf(c)));
                i++;
                continue;
            }

            // Yorum: çok satırlı /* ... */
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                int start = i;
                i += 2;
                while (i + 1 < input.length() && !(input.charAt(i) == '*' && input.charAt(i + 1) == '/')) i++;
                if (i + 1 < input.length()) i += 2;
                tokens.add(new Token(TokenType.COMMENT, input.substring(start, i)));
                continue;
            }

            // Yorum: tek satırlı // ...
            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                int start = i;
                while (i < input.length() && input.charAt(i) != '\n') i++;
                tokens.add(new Token(TokenType.COMMENT, input.substring(start, i)));
                continue;
            }

            // String: "..."
            if (c == '"') {
                int start = i++;
                while (i < input.length() && input.charAt(i) != '"') i++;
                if (i < input.length()) i++; // kapanan " dahil
                tokens.add(new Token(TokenType.STRING, input.substring(start, i)));
                continue;
            }

            // Char: 'c'
            if (c == '\'' && i + 2 < input.length() && input.charAt(i + 2) == '\'') {
                tokens.add(new Token(TokenType.CHAR, input.substring(i, i + 3)));
                i += 3;
                continue;
            }

            // Operatörler
            if (isOperator(String.valueOf(c))) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c)));
                i++;
                continue;
            }

            // Parantezler
            if ("(){};".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.PARENTHESIS, String.valueOf(c)));
                i++;
                continue;
            }

            // Köşeli parantezler
            if ("[]".indexOf(c) != -1) {
                tokens.add(new Token(TokenType.BRACKET, String.valueOf(c)));
                i++;
                continue;
            }

            // Sayılar (int, float, bilimsel)
            if (Character.isDigit(c)) {
                int start = i;
                while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                if (i < input.length() && input.charAt(i) == '.') {
                    i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                }
                if (i < input.length() && (input.charAt(i) == 'e' || input.charAt(i) == 'E')) {
                    i++;
                    if (i < input.length() && (input.charAt(i) == '+' || input.charAt(i) == '-')) i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                }
                tokens.add(new Token(TokenType.NUMBER, input.substring(start, i)));
                continue;
            }

            // IDENTIFIER ya da KEYWORD
            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) i++;
                String word = input.substring(start, i);
                TokenType type = isKeyword(word) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new Token(type, word));
                continue;
            }

            // Tanınmayan karakter
            tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c)));
            i++;
        }

        return tokens;
    }

    // Anahtar kelime kontrolü
    private boolean isKeyword(String s) {
        return KEYWORDS.contains(s);
    }

    // Operatör kontrolü
    private boolean isOperator(String s) {
        return s.matches("[+\\-*/=<>!]");
    }

    // Token + pozisyon bilgisi dönen versiyon
    public List<TokenWithPosition> analyzeWithPositions(String input) {
        List<TokenWithPosition> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            int start = i;
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                tokens.add(new TokenWithPosition(new Token(TokenType.WHITESPACE, String.valueOf(c)), start, i + 1));
                i++;
                continue;
            }

            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                int startComment = i;
                i += 2;
                while (i + 1 < input.length() && !(input.charAt(i) == '*' && input.charAt(i + 1) == '/')) i++;
                if (i + 1 < input.length()) i += 2;
                tokens.add(new TokenWithPosition(new Token(TokenType.COMMENT, input.substring(startComment, i)), startComment, i));
                continue;
            }

            if (c == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                int startComment = i;
                while (i < input.length() && input.charAt(i) != '\n') i++;
                tokens.add(new TokenWithPosition(new Token(TokenType.COMMENT, input.substring(startComment, i)), startComment, i));
                continue;
            }

            if (c == '"') {
                i++;
                while (i < input.length() && input.charAt(i) != '"') i++;
                i++;
                tokens.add(new TokenWithPosition(new Token(TokenType.STRING, input.substring(start, i)), start, i));
                continue;
            }

            if (c == '\'' && i + 2 < input.length() && input.charAt(i + 2) == '\'') {
                tokens.add(new TokenWithPosition(new Token(TokenType.CHAR, input.substring(i, i + 3)), i, i + 3));
                i += 3;
                continue;
            }

            if (isOperator(String.valueOf(c))) {
                tokens.add(new TokenWithPosition(new Token(TokenType.OPERATOR, String.valueOf(c)), i, i + 1));
                i++;
                continue;
            }

            if ("(){};".indexOf(c) != -1) {
                tokens.add(new TokenWithPosition(new Token(TokenType.PARENTHESIS, String.valueOf(c)), i, i + 1));
                i++;
                continue;
            }

            if ("[]".indexOf(c) != -1) {
                tokens.add(new TokenWithPosition(new Token(TokenType.BRACKET, String.valueOf(c)), i, i + 1));
                i++;
                continue;
            }

            if (Character.isDigit(c)) {
                while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                if (i < input.length() && input.charAt(i) == '.') {
                    i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                }
                if (i < input.length() && (input.charAt(i) == 'e' || input.charAt(i) == 'E')) {
                    i++;
                    if (i < input.length() && (input.charAt(i) == '+' || input.charAt(i) == '-')) i++;
                    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
                }
                tokens.add(new TokenWithPosition(new Token(TokenType.NUMBER, input.substring(start, i)), start, i));
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) i++;
                String word = input.substring(start, i);
                TokenType type = isKeyword(word) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new TokenWithPosition(new Token(type, word), start, i));
                continue;
            }

            tokens.add(new TokenWithPosition(new Token(TokenType.UNKNOWN, String.valueOf(c)), i, i + 1));
            i++;
        }

        return tokens;
    }

    // İç içe sınıf
    public static class TokenWithPosition {
        public Token token;
        public int startPos;
        public int endPos;

        public TokenWithPosition(Token token, int startPos, int endPos) {
            this.token = token;
            this.startPos = startPos;
            this.endPos = endPos;
        }
    }
}
