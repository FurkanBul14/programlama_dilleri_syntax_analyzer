package programlama_dilleri;

import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        SyntaxAnalyzer syntax = new SyntaxAnalyzer();

        String code = "for (int i = 0; i < 5; i++) { if (i == 3) { return; } }";
        List<LexicalAnalyzer.TokenWithPosition> tokensWithPos = lexer.analyzeWithPositions(code); // ✅


        // Token'ları yazdır
        for (LexicalAnalyzer.TokenWithPosition tokenWithPos : tokensWithPos) {
            System.out.println(tokenWithPos.token + " [Pos: " + tokenWithPos.startPos + "-" + tokenWithPos.endPos + "]");
        }

        // Syntax kontrolü (sadece token'ları kullan)
        List<Token> tokens = tokensWithPos.stream().map(t -> t.token).collect(Collectors.toList());
        boolean isValid = syntax.analyze(tokens);
        System.out.println("\nGeçerli yapıda mı? => " + isValid);
    }
}