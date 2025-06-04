package programlama_dilleri;

// Token sınıfı, bir parçalanmış (tokenize edilmiş) kelimenin türünü ve değerini saklar
public class Token {
    private TokenType type;  // Token'ın türü (örneğin: KEYWORD, IDENTIFIER, NUMBER...)
    private String value;    // Token'ın gerçek metinsel değeri (örneğin: "if", "x", "123")

    // Yapıcı metod: Token oluştururken tür ve değer atanır
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    // Token türünü döndürür
    public TokenType getType() {
        return type;
    }

    // Token değerini döndürür
    public String getValue() {
        return value;
    }

    // Token'ı yazdırmak için özel format belirlenmiştir (örnek: [KEYWORD: if])
    @Override
    public String toString() {
        return "[" + type + ": " + value + "]";
    }
}
