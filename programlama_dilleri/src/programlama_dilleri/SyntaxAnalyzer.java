package programlama_dilleri;

import java.util.ArrayList;
import java.util.List;

public class SyntaxAnalyzer {

    // Hata ya da başarı bilgisini taşıyan iç sınıf
    public static class SyntaxResult {
        public String message;
        public int startPos;
        public int endPos;

        public SyntaxResult(String msg, int start, int end) {
            this.message = msg;
            this.startPos = start;
            this.endPos = end;
        }
    }

    /**
     * Temel sözdizimi kontrolünü yapan metot.
     * 
     * • tokens: Sadece Token nesnelerini içeren liste.
     * • Döndürülen String, hata mesajı veya “✅ Geçerli …” formatında olabilir.
     */
    public String analyze(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            // 1) if bloğu: “if ( … ) { } (else { })?”
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("if")) {
                int j = i + 1;
                // Boşlukları atla
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
                    return "❌ Hata: if bloğunda '(' eksik";
                j++;

                // Parantez kapanana kadar ilerle
                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0)
                    return "❌ Hata: if bloğunda ')' eksik";

                // “{” beklenecek
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                    return "❌ Hata: if bloğu '{' ile başlamıyor";

                j++;
                // Süslü parantezleri eşleştir
                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0)
                    return "❌ Hata: if bloğu '}' ile kapanmamış";

                // “else” varsa kontrol et
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j < tokens.size()
                    && tokens.get(j).getType() == TokenType.KEYWORD
                    && tokens.get(j).getValue().equals("else")) {
                    j++;
                    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                    if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                        return "❌ Hata: else bloğu '{' ile başlamıyor";
                    j++;
                    braceCount = 1;
                    while (j < tokens.size() && braceCount > 0) {
                        if (tokens.get(j).getValue().equals("{")) braceCount++;
                        else if (tokens.get(j).getValue().equals("}")) braceCount--;
                        j++;
                    }
                    if (braceCount != 0)
                        return "❌ Hata: else bloğu '}' ile kapanmamış";
                }

                return "✅ Geçerli if bloğu";
            }

            // 2) while bloğu: “while ( … ) { … }”
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("while")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
                    return "❌ Hata: while bloğunda '(' eksik";
                j++;

                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0)
                    return "❌ Hata: while bloğunda ')' eksik";

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                    return "❌ Hata: while bloğu '{' ile başlamıyor";
                j++;

                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0)
                    return "❌ Hata: while bloğu '}' ile kapanmamış";

                return "✅ Geçerli while bloğu";
            }

            // 3) for bloğu: “for ( … ; … ; … ) { … }”
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("for")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
                    return "❌ Hata: for döngüsünde '(' eksik";
                j++;

                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0)
                    return "❌ Hata: for döngüsünde ')' eksik";

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                    return "❌ Hata: for döngüsü '{' ile başlamıyor";
                j++;

                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0)
                    return "❌ Hata: for döngüsü '}' ile kapanmamış";

                return "✅ Geçerli for döngüsü";
            }

            // 4) public void metot tanımı: “public void name ( ) { … }”
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("public")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size()
                    || !(tokens.get(j).getType() == TokenType.KEYWORD && tokens.get(j).getValue().equals("void")))
                    continue; // public class veya başka bir şey olabilir, atla
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER)
                    return "❌ Hata: Method ismi (identifier) eksik";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
                    return "❌ Hata: Method ismi sonrası '(' eksik";
                j++;

                if (j >= tokens.size() || !tokens.get(j).getValue().equals(")"))
                    return "❌ Hata: Method parametreleri için ')' eksik";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                    return "❌ Hata: Method bloğu '{' ile başlamalı";
                j++;

                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0)
                    return "❌ Hata: Method bloğu '}' ile kapanmamış";

                return "✅ Geçerli method tanımı";
            }

            // 5) public class tanımı: “public class Name { … }”
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("public")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("class"))
                    continue; // public void olabilir, atla
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER)
                    return "❌ Hata: Sınıf ismi (identifier) eksik";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{"))
                    return "❌ Hata: Sınıf tanımı '{' ile başlamıyor";
                j++;

                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0)
                    return "❌ Hata: Sınıf tanımı '}' ile kapanmamış";

                return "✅ Geçerli sınıf tanımı";
            }

            // 6) return ifadesi: yukarıdaki kısa kuralın dışında da kontrol edilebilir
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("return")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size())
                    return "❌ Hata: return ifadesinden sonra değer eksik";

                Token next = tokens.get(j);
                if (!(next.getType() == TokenType.IDENTIFIER || next.getType() == TokenType.NUMBER))
                    return "❌ Hata: return ifadesinden sonra geçersiz değer";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";"))
                    return "❌ Hata: return ifadesinden sonra ';' eksik";

                return "✅ Geçerli return ifadesi";
            }

         // 7) değişken tanımı: “int x = 5;” veya “private int y;”
            if (t.getType() == TokenType.KEYWORD &&
                (t.getValue().equals("int") ||
                 t.getValue().equals("char") ||
                 t.getValue().equals("String") ||
                 t.getValue().equals("double") ||
                 t.getValue().equals("boolean"))) {

                int j = i + 1;
                // Erişim belirleyicileri (private, public, protected) atla
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.IDENTIFIER &&
                       (tokens.get(j).getValue().equals("private") || 
                        tokens.get(j).getValue().equals("public") || 
                        tokens.get(j).getValue().equals("protected"))) {
                    j++;
                    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                }
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER)
                    return "❌ Hata: Değişken tanımında isim (identifier) eksik";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                // “;” varsa sadece tanım
                if (j < tokens.size() && tokens.get(j).getValue().equals(";"))
                    return "✅ Geçerli değişken tanımı (ilk değer atanmadı)";

                if (j >= tokens.size() || !tokens.get(j).getValue().equals("="))
                    return "❌ Hata: Değişkene değer atamak için '=' eksik";
                j++;

                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size())
                    return "❌ Hata: Değişkene atanacak değer eksik";

                Token valueToken = tokens.get(j);
                // Tür uyumu kontrolleri
                if (t.getValue().equals("int") && valueToken.getType() != TokenType.NUMBER)
                    return "❌ Hata: int değişkene sadece sayı atanabilir";
                if (t.getValue().equals("char") && valueToken.getType() != TokenType.CHAR)
                    return "❌ Hata: char değişkene tek tırnak içinde karakter atanmalı";
                if (t.getValue().equals("String") && valueToken.getType() != TokenType.STRING)
                    return "❌ Hata: String değişkene çift tırnak içinde metin atanmalı";
                if (t.getValue().equals("double") && valueToken.getType() != TokenType.NUMBER)
                    return "❌ Hata: double değişkene sayı atanmalı (örneğin 3.14)";
                if (t.getValue().equals("boolean") &&
                   !(valueToken.getValue().equals("true") || valueToken.getValue().equals("false")))
                    return "❌ Hata: boolean değişkene sadece 'true' veya 'false' atanmalı";

                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";"))
                    return "❌ Hata: Değişken tanımı ';' ile bitmeli";

                return "✅ Geçerli değişken tanımı";
            }

            // 8) break/continue ifadesi: “break;” veya “continue;”
            if (t.getType() == TokenType.KEYWORD &&
                (t.getValue().equals("break") || t.getValue().equals("continue"))) {

                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";"))
                    return "❌ Hata: '" + t.getValue() + "' ifadesi ';' ile bitmeli";

                return "✅ Geçerli '" + t.getValue() + "' ifadesi";
            }

            // 9) System.out.println ifadesi
            if (t.getType() == TokenType.IDENTIFIER && t.getValue().equals("System")) {
                int j = i + 1;
                if (j + 4 < tokens.size() &&
                    tokens.get(j).getValue().equals(".") &&
                    tokens.get(j + 1).getValue().equals("out") &&
                    tokens.get(j + 2).getValue().equals(".") &&
                    tokens.get(j + 3).getValue().equals("println") &&
                    tokens.get(j + 4).getValue().equals("(")) {

                    j += 5;
                    int parenCount = 1;
                    while (j < tokens.size() && parenCount > 0) {
                        if (tokens.get(j).getValue().equals("(")) parenCount++;
                        else if (tokens.get(j).getValue().equals(")")) parenCount--;
                        j++;
                    }
                    if (parenCount != 0)
                        return "❌ Hata: println ifadesinde ')' eksik";

                    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                    if (j >= tokens.size() || !tokens.get(j).getValue().equals(";"))
                        return "❌ Hata: println ifadesi ';' ile bitmeli";

                    return "✅ Geçerli println ifadesi";
                } else {
                    return "❌ Hata: Geçersiz System.out.println ifadesi";
                }
            }
        }

        // Yukarıdaki hiçbir kural eşleşmediyse
        return "❌ Hata: Tanınmayan veya desteklenmeyen yapı";
    }

    /**
     * analyzeWithPositions(...) metodu:
     *  • TokenWithPosition listesi üzerinden çalışır.
     *  • Hata tespit edildiğinde gerçek hatalı token’ın pozisyonunu döner.
     *  • Eğer hata yoksa (başarı durumu) startPos = endPos = -1 döner.
     */
    public SyntaxResult analyzeWithPositions(List<LexicalAnalyzer.TokenWithPosition> tokensWithPos) {
        // Önce sade Token listesini elde et
        List<Token> tokens = new ArrayList<>();
        for (LexicalAnalyzer.TokenWithPosition twp : tokensWithPos) {
            tokens.add(twp.token);
        }

        // 1) “if” bloğu hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("if")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("(")) {
                    int start = (j < tokens.size())
                                ? tokensWithPos.get(j).startPos
                                : tokensWithPos.get(i).endPos;
                    int end   = (j < tokens.size())
                                ? tokensWithPos.get(j).endPos
                                : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: if bloğunda '(' eksik", start, end);
                }
                j++;
                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0) {
                    int missing = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: if bloğunda ')' eksik", missing, missing + 1);
                }
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: if bloğu '{' ile başlamıyor", pos, pos + 1);
                }
                // “}” kontrolü
                int braceCount = 1;
                j++;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: if bloğu '}' ile kapanmamış", pos, pos + 1);
                }
            }
        }

        // 2) “while” bloğu hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("while")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("(")) {
                    int start = (j < tokens.size())
                                ? tokensWithPos.get(j).startPos
                                : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: while bloğunda '(' eksik", start, start + 1);
                }
                j++;
                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: while bloğunda ')' eksik", pos, pos + 1);
                }
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: while bloğu '{' ile başlamıyor", pos, pos + 1);
                }
                j++;
                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: while bloğu '}' ile kapanmamış", pos, pos + 1);
                }
            }
        }

        // 3) “for” bloğu hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("for")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("(")) {
                    int start = (j < tokens.size())
                                ? tokensWithPos.get(j).startPos
                                : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: for döngüsünde '(' eksik", start, start + 1);
                }
                j++;
                int parenCount = 1;
                while (j < tokens.size() && parenCount > 0) {
                    if (tokens.get(j).getValue().equals("(")) parenCount++;
                    else if (tokens.get(j).getValue().equals(")")) parenCount--;
                    j++;
                }
                if (parenCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: for döngüsünde ')' eksik", pos, pos + 1);
                }
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: for döngüsü '{' ile başlamıyor", pos, pos + 1);
                }
                j++;
                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: for döngüsü '}' ile kapanmamış", pos, pos + 1);
                }
            }
        }

        // 4) public void metot hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("public")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !(tokens.get(j).getType() == TokenType.KEYWORD && tokens.get(j).getValue().equals("void")))
                    continue; // public class olabilir, atla
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Method ismi (identifier) eksik", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("(")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Method ismi sonrası '(' eksik", pos, pos + 1);
                }
                j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(")")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Method parametreleri için ')' eksik", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Method bloğu '{' ile başlamalı", pos, pos + 1);
                }
                j++;
                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: Method bloğu '}' ile kapanmamış", pos, pos + 1);
                }
            }
        }

        // 5) public class hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("public")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("class"))
                    continue; // public void olabilir, atla
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Sınıf ismi (identifier) eksik", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("{")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Sınıf tanımı '{' ile başlamıyor", pos, pos + 1);
                }
                j++;
                int braceCount = 1;
                while (j < tokens.size() && braceCount > 0) {
                    if (tokens.get(j).getValue().equals("{")) braceCount++;
                    else if (tokens.get(j).getValue().equals("}")) braceCount--;
                    j++;
                }
                if (braceCount != 0) {
                    int pos = tokensWithPos.get(j - 1).startPos;
                    return new SyntaxResult("❌ Hata: Sınıf tanımı '}' ile kapanmamış", pos, pos + 1);
                }
            }
        }

        // 6) return ifadesi hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD && t.getValue().equals("return")) {
                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size()) {
                    int pos = tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: return ifadesinden sonra değer eksik", pos, pos + 1);
                }
                Token next = tokens.get(j);
                if (!(next.getType() == TokenType.IDENTIFIER || next.getType() == TokenType.NUMBER)) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: return ifadesinden sonra geçersiz değer", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: return ifadesinden sonra ';' eksik", pos, pos + 1);
                }
            }
        }

        // 7) değişken tanımı hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD &&
                (t.getValue().equals("int") ||
                 t.getValue().equals("char") ||
                 t.getValue().equals("String") ||
                 t.getValue().equals("double") ||
                 t.getValue().equals("boolean"))) {

                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || tokens.get(j).getType() != TokenType.IDENTIFIER) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Değişken tanımında isim (identifier) eksik", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j < tokens.size() && tokens.get(j).getValue().equals(";")) {
                    // Sadece tanım (“int x;”) doğru
                    continue;
                }
                if (j >= tokens.size() || !tokens.get(j).getValue().equals("=")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Değişkene değer atamak için '=' eksik", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size()) {
                    int pos = tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Değişkene atanacak değer eksik", pos, pos + 1);
                }
                Token valueToken = tokens.get(j);
                if (t.getValue().equals("int") && valueToken.getType() != TokenType.NUMBER) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: int değişkene sadece sayı atanabilir", pos, pos + 1);
                }
                if (t.getValue().equals("char") && valueToken.getType() != TokenType.CHAR) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: char değişkene tek tırnak içinde karakter atanmalı", pos, pos + 1);
                }
                if (t.getValue().equals("String") && valueToken.getType() != TokenType.STRING) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: String değişkene çift tırnak içinde metin atanmalı", pos, pos + 1);
                }
                if (t.getValue().equals("double") && valueToken.getType() != TokenType.NUMBER) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: double değişkene sayı atanmalı (örn. 3.14)", pos, pos + 1);
                }
                if (t.getValue().equals("boolean")
                    && !(valueToken.getValue().equals("true") || valueToken.getValue().equals("false"))) {
                    int pos = tokensWithPos.get(j).startPos;
                    return new SyntaxResult("❌ Hata: boolean değişkene sadece 'true' veya 'false' atanmalı", pos, pos + 1);
                }
                j++;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: Değişken tanımı ';' ile bitmeli", pos, pos + 1);
                }
            }
        }

        // 8) break/continue hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.KEYWORD &&
                (t.getValue().equals("break") || t.getValue().equals("continue"))) {

                int j = i + 1;
                while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                if (j >= tokens.size() || !tokens.get(j).getValue().equals(";")) {
                    int pos = (j < tokens.size())
                              ? tokensWithPos.get(j).startPos
                              : tokensWithPos.get(i).endPos;
                    return new SyntaxResult("❌ Hata: '" + t.getValue() + "' ifadesi ';' ile bitmeli", pos, pos + 1);
                }
            }
        }

        // 9) System.out.println hata tespiti
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getType() == TokenType.IDENTIFIER && t.getValue().equals("System")) {
                int j = i + 1;
                if (j + 4 < tokens.size()
                    && tokens.get(j).getValue().equals(".")
                    && tokens.get(j + 1).getValue().equals("out")
                    && tokens.get(j + 2).getValue().equals(".")
                    && tokens.get(j + 3).getValue().equals("println")
                    && tokens.get(j + 4).getValue().equals("(")) {

                    j += 5;
                    int parenCount = 1;
                    while (j < tokens.size() && parenCount > 0) {
                        if (tokens.get(j).getValue().equals("(")) parenCount++;
                        else if (tokens.get(j).getValue().equals(")")) parenCount--;
                        j++;
                    }
                    if (parenCount != 0) {
                        int pos = tokensWithPos.get(j - 1).startPos;
                        return new SyntaxResult("❌ Hata: println ifadesinde ')' eksik", pos, pos + 1);
                    }
                    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
                    if (j >= tokens.size() || !tokens.get(j).getValue().equals(";")) {
                        int pos = (j < tokens.size())
                                  ? tokensWithPos.get(j).startPos
                                  : tokensWithPos.get(i).endPos;
                        return new SyntaxResult("❌ Hata: println ifadesi ';' ile bitmeli", pos, pos + 1);
                    }
                } else {
                    int pos = tokensWithPos.get(i).startPos;
                    return new SyntaxResult("❌ Hata: Geçersiz System.out.println ifadesi", pos, pos + 1);
                }
            }
        }

        // Eğer hiçbir kural hataya takılmadıysa, başarılı kabul et
        return new SyntaxResult("✅ Geçerli yapı", -1, -1);
    }

    /**
     * analyzeAll(...) metodu:
     *  • Tüm Token listesini alır, blok blok ayırarak sırayla analyze çağırır.
     *  • Örneğin:
     *    – if/while/for/class gibi “{ … }” bloklarını bir arada ele alır,
     *    – Diğer “;” ile biten satırları yine ayrı ayrı analiz eder.
     *  • Her bir bloğun veya satırın sonucunu listeye ekler.
     */
    public List<String> analyzeAll(List<Token> tokens) {
        List<String> results = new ArrayList<>();
        int i = 0;

        while (i < tokens.size()) {
            // Boşlukları atla
            while (i < tokens.size() && tokens.get(i).getType() == TokenType.WHITESPACE) {
                i++;
            }
            int start = i;
            int end = i;

            if (i < tokens.size() && tokens.get(i).getType() == TokenType.KEYWORD) {
                String val = tokens.get(i).getValue();
                if (val.equals("if") || val.equals("while") || val.equals("for")
                    || val.equals("public") || val.equals("class")) {
                    // Süsülü blokları tümüyle al
                    int braceCount = 0;
                    while (end < tokens.size()) {
                        if (tokens.get(end).getValue().equals("{")) braceCount++;
                        else if (tokens.get(end).getValue().equals("}")) braceCount--;
                        end++;
                        if (braceCount == 0 && tokens.get(end - 1).getValue().equals("}")) {
                            break;
                        }
                    }
                } else {
                    // Diğer satırlar için “;” arayıp al
                    while (end < tokens.size() && !tokens.get(end).getValue().equals(";")) {
                        end++;
                    }
                    if (end < tokens.size()) {
                        end++; // “;”’yi dahil et
                    }
                }
            } else {
                // Keyword değilse, tek token’lık al
                end = i + 1;
            }

            if (start < end && end <= tokens.size()) {
                List<Token> block = tokens.subList(start, end);
                String result = analyze(block);
                results.add(result);
            }
            i = end;
        }

        return results;
    }
}
