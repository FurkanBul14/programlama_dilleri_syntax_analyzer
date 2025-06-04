package programlama_dilleri;

import java.util.HashMap;
import java.util.Map;

public class Highlighter {

    // Her TokenType'a karşılık gelen renkleri tutan harita
    private Map<TokenType, String> colorMap;

    public Highlighter() {
        colorMap = new HashMap<>();

        // Anahtar kelimeler (örneğin if, for...) mavi renkle gösterilir
        colorMap.put(TokenType.KEYWORD, "#0000FF"); // mavi

        // Sayılar yeşil renkle gösterilir
        colorMap.put(TokenType.NUMBER, "#008000"); // yeşil

        // String ifadeler (örneğin "merhaba") turuncu renkle gösterilir
        colorMap.put(TokenType.STRING, "#FFA500"); // turuncu

        // Tek karakterli char ifadeler mor renkle gösterilir
        colorMap.put(TokenType.CHAR, "#800080"); // mor

        // Operatörler (+, -, =, >, <) kırmızı ile gösterilir
        colorMap.put(TokenType.OPERATOR, "#FF0000"); // kırmızı

        // Değişken isimleri gibi tanımlayıcılar siyah renktedir
        colorMap.put(TokenType.IDENTIFIER, "#000000"); // siyah

        // Parantez ve süslü parantezler gri
        colorMap.put(TokenType.PARENTHESIS, "#808080"); // gri
        colorMap.put(TokenType.BRACKET, "#808080"); // gri

        // Yorum satırları koyu yeşil renkle
        colorMap.put(TokenType.COMMENT, "#006400"); // koyu yeşil

        // Boşluklar açık gri (görsel olarak fark edilsin diye)
        colorMap.put(TokenType.WHITESPACE, "#D3D3D3"); // açık gri

        // Tanımlanamayan karakterler pembe renkle (hata gibi gözüksün)
        colorMap.put(TokenType.UNKNOWN, "#FF69B4"); // pembe
    }

    // Token tipine karşılık gelen rengi döner; eğer tip bilinmiyorsa siyah (#000000) döner
    public String getColor(TokenType type) {
        return colorMap.getOrDefault(type, "#000000");
    }
}
