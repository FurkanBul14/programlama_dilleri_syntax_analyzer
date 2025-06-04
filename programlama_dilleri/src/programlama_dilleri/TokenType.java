package programlama_dilleri;

// TokenType enum'u, her bir token'ın türünü belirtmek için kullanılır
public enum TokenType {
    BRACKET,        // Köşeli parantezler: [ ]
    KEYWORD,        // Java anahtar kelimeleri: if, for, while, int, etc.
    IDENTIFIER,     // Değişken veya fonksiyon adları (x, toplam, myMethod, ...)
    NUMBER,         // Sayısal değerler (123, 3.14, 5e+2, ...)
    STRING,         // String sabitleri: "metin"
    CHAR,           // Karakter sabitleri: 'a'
    OPERATOR,       // Operatörler: +, -, *, /, =, <, >, ...
    PARENTHESIS,    // Normal parantezler ve süslü parantezler: (), {}, ;
    COMMENT,        // Tek satır veya çok satırlı yorumlar: // ... veya /* ... */
    WHITESPACE,     // Boşluk, tab, yeni satır gibi boş karakterler
    UNKNOWN         // Tanınmayan veya geçersiz karakterler
}
