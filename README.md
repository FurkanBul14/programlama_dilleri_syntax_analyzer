# programlama_dilleri_syntax_analyzer
Real-Time Grammar-Based Syntax Highlighter with GUI
# Giriş
Modern kod editörleri, yalnızca sözdizimi renklendirmesi (syntax highlighting) yapmakla kalmaz, aynı zamanda gramer tabanlı analiz yaparak hatalı kodları anında kullanıcıya bildirir. Bu proje, Java ve Swing kullanılarak "Real-Time Grammar-Based Syntax Highlighter with GUI" geliştirilmesini hedefliyor. Kullanıcı kod yazarken, metin önce lexical analiz (tokenlaştırma) ile token'lara ayrılıyor, ardından sözdizimi analizi (parser) ile gramer kurallarına uygunluğu kontrol ediliyor. En az beş farklı token türü gerçek zamanlı olarak renklendiriliyor ve hatalı kısımlar kırmızı alt çizgiyle işaretleniyor. Ayrıca, durum çubuğunda kodun geçerli olup olmadığına dair anlık geri bildirim (✅ Geçerli yapı veya ❌ Hata: ...) sağlanıyor.
Bu makalede:

Dil ve gramer seçimimizin nedenlerini,
Leksik analiz sürecimizin tasarımını,
Sözdizimi analiz metodolojimizi,
Gerçek zamanlı renklendirme şemamızı,
GUI bileşenlerimizi,

adım adım açıklayacağım. Ayrıca, karşılaştığım zorluklar ve çözümlerimi de paylaşacağım.
#1. Programlama Dili ve Gramer Seçimi
#1.1 Neden Java ve Swing?

Swing Kütüphanesi: Java'nın standart GUI aracı Swing, JTextPane ve StyledDocument ile her karakter aralığına kolayca stil (renk, alt çizgi vb.) uygulayabilmemizi sağlıyor. Özellikle hata vurgulama için alt çizgi (StyleConstants.Underline) özelliği Swing'de oldukça pratik.
Platform Bağımsızlığı: Java ile derlenen JAR dosyası, farklı işletim sistemlerinde (Windows, macOS, Linux) sorunsuz çalışabiliyor.
Kolay Implementasyon: Java'nın zengin API'leri ve nesne yönelimli yapısı, lexical ve syntax analiz için modüler bir tasarım yapmamızı kolaylaştırdı.

#1.2 Desteklenen Gramer
Projenin amacı, Java dilinin bir alt kümesini (subset) desteklemek ve temel yapıların gerçek zamanlı analizini yapmaktır. Aşağıdaki yapılar destekleniyor:

Değişken Tanımlama:
int x = 10;

Burada int bir anahtar kelime (KEYWORD), x bir tanımlayıcı (IDENTIFIER), = bir operatör (OPERATOR), 10 bir sayı (NUMBER) ve ; bir ayırıcı (PARENTHESIS) olarak tanınıyor.

Kontrol Yapıları (if, while, for):
if (x > 5) {
    System.out.println("Merhaba");
}

while (true) {
    x++;
}

for (int i = 0; i < 5; i++) {
    System.out.println("Döngü");
}

Bu yapılar iç içe olabilir (örneğin, if içinde while).

Metot ve Sınıf Tanımları:
public class MyClass {
    public void myMethod() {
        int sum = 0;
    }
}


Atama ve println İfadeleri:
x = x + 1;
System.out.println("Sonuç: " + x);



Bu gramer, else, switch, lambda ifadeleri gibi daha karmaşık yapıları desteklemiyor. Ancak, temel kontrol yapıları, metotlar ve sınıflar için geniş bir destek sunuyor. İleride daha fazla yapı eklenebilir.
#2.  Lexical Analiz
#2.1 State Diagram & Program Implementation Yaklaşımı
Amaç: Girdi metnini en küçük anlamsal birimlere (token'lara) ayırmak.
Lexical analiz için State Diagram & Program Implementation yöntemini seçtik:

State Diagram: Her karakter okunduğunda bir duruma geçiş yapılır. Örneğin, bir rakam okunduğunda "sayı durumu"na geçilir ve rakamlar bitene kadar bu durum devam eder.
Program Implementation: Bu durumlar, doğrudan Java koduyla (if-else bloklarıyla) uygulanır; bir tablo kullanılmaz.

LexicalAnalyzer sınıfında, analyzeWithPositions metodu bu yaklaşımı kullanır. Karakterler tek tek okunur ve duruma göre token'lar oluşturulur.
#2.2 Token Sınıfı
enum TokenType {
    KEYWORD, NUMBER, STRING, OPERATOR, IDENTIFIER, PARENTHESIS, COMMENT
}

class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
}

class TokenWithPosition extends Token {
    private int startPos;
    private int endPos;

    public TokenWithPosition(TokenType type, String value, int startPos, int endPos) {
        super(type, value);
        this.startPos = startPos;
        this.endPos = endPos;
    }
}


type: Token'ın türü (enum).
value: Token'ın metinsel değeri (örneğin, if veya 123).
startPos, endPos: Metin içindeki başlangıç ve bitiş indeksleri (renklendirme ve hata işaretleme için).

#2.3 analyzeWithPositions Metodu
LexicalAnalyzer sınıfındaki analyzeWithPositions metodu, metni tarar ve token'ları oluşturur. İşte bir örnek:
if (Character.isDigit(c)) {
    int start = i;
    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
    if (i < input.length() && input.charAt(i) == '.') {
        i++;
        while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
    }
    tokens.add(new TokenWithPosition(TokenType.NUMBER, input.substring(start, i), start, i));
    continue;
}


State Diagram Mantığı:
Başlangıç durumu: Karakter bir rakam mı (Character.isDigit)?
Sayı durumu: Rakamlar devam ederse oku, nokta (.) gelirse ondalık sayıya geç.
Bitiş durumu: Sayı tamamlanınca NUMBER token'ı oluştur ve pozisyonlarını kaydet.



Bu yöntemle, KEYWORD, IDENTIFIER, OPERATOR, STRING, PARENTHESIS ve COMMENT token'ları ayrıştırılır. Örneğin:

"int x = 5;" → KEYWORD("int"), IDENTIFIER("x"), OPERATOR("="), NUMBER("5"), PARENTHESIS(";").

#3. Parser (Sözdizimi Analizörü)
#3.1 Genel Yapı
Sözdizimi analizi için Top-Down Parsing yöntemini seçtik. Bu yaklaşımda, parse tree yukarıdan aşağıya (preorder) izlenir:

Önce üst seviye yapılar (class, method, if, while, for) kontrol edilir.
Ardından içteki ifadeler (koşullar, gövde) analiz edilir.

SyntaxAnalyzer sınıfı bu mantığı uygular. analyzeWithPositions metodu, token listesini tarar ve gramer kurallarına uygunluğu kontrol eder.
#3.2 Hata Tespiti: startPos ve endPos
Hatalı bir yapı tespit edildiğinde, ilgili token'ın başlangıç ve bitiş pozisyonları (startPos, endPos) kaydedilir. Örneğin:

"if (x > 0 {" → Eksik ), hata startPos ve endPos ile { token'ında işaretlenir.

Bu pozisyonlar, GUI'de kırmızı alt çizgiyle hata vurgusu yapmak için kullanılır.
#3.3 Detaylı Kod Örneği
SyntaxAnalyzer sınıfından if bloğu kontrolü:
if (t.getType() == TokenType.KEYWORD && t.getValue().equals("if")) {
    int j = i + 1;
    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
    if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
        return new SyntaxResult("❌ Hata: if bloğunda '(' eksik", tokens.get(j-1).startPos, tokens.get(j-1).endPos);
    j++;
    int parenCount = 1;
    while (j < tokens.size() && parenCount > 0) {
        if (tokens.get(j).getValue().equals("(")) parenCount++;
        else if (tokens.get(j).getValue().equals(")")) parenCount--;
        j++;
    }
    if (parenCount != 0)
        return new SyntaxResult("❌ Hata: if bloğunda ')' eksik", tokens.get(j-1).startPos, tokens.get(j-1).endPos);
}


Top-Down Mantığı:
if anahtar kelimesi kontrol edilir.
Koşul parantezleri ((, )) ve süslü parantezler ({, }) kontrol edilir.
Hata varsa, ilgili token'ın pozisyonları ile birlikte hata mesajı döndürülür.



#4. Gerçek-Zamanlı Renklendirme (Highlighting) Şeması
#4.1 Stil (Renk) Tanımları
EditorPanel sınıfında, JTextPane için renkler tanımlanır:
Style keywordStyle = doc.addStyle("KEYWORD", null);
StyleConstants.setForeground(keywordStyle, Color.BLUE); // #0000FF

Style numberStyle = doc.addStyle("NUMBER", null);
StyleConstants.setForeground(numberStyle, Color.GREEN); // #008000

Style stringStyle = doc.addStyle("STRING", null);
StyleConstants.setForeground(stringStyle, Color.ORANGE); // #FFA500

Style operatorStyle = doc.addStyle("OPERATOR", null);
StyleConstants.setForeground(operatorStyle, Color.RED); // #FF0000

Style identifierStyle = doc.addStyle("IDENTIFIER", null);
StyleConstants.setForeground(identifierStyle, Color.BLACK); // #000000

Style parenStyle = doc.addStyle("PARENTHESIS", null);
StyleConstants.setForeground(parenStyle, Color.GRAY); // #808080

Style errorStyle = doc.addStyle("ERROR", null);
StyleConstants.setForeground(errorStyle, Color.RED);
StyleConstants.setUnderline(errorStyle, true);


Renk Seçimi:
KEYWORD: Mavi (#0000FF) – Anahtar kelimeler öne çıksın.
NUMBER: Yeşil (#008000) – Sayılar belirgin olsun.
STRING: Turuncu (#FFA500) – String'ler dikkat çeksin.
OPERATOR: Kırmızı (#FF0000) – Operatörler vurgulu.
IDENTIFIER: Siyah (#000000) – Standart görünüm.
PARENTHESIS: Gri (#808080) – Parantezler ve ayırıcılar nötr.
ERROR: Kırmızı alt çizgi – Hatalar net görülsün.



#4.2 Metni Renklendirme (highlight)
EditorPanel sınıfındaki highlight metodu:
private void highlight() {
    String text = textPane.getText();
    StyledDocument doc = textPane.getStyledDocument();

    // Tüm metni sıfırla
    doc.setCharacterAttributes(0, text.length(), doc.getStyle("DEFAULT"), true);

    // Token'ları al ve renklendir
    List<LexicalAnalyzer.TokenWithPosition> tokens = analyzer.analyzeWithPositions(text);
    for (LexicalAnalyzer.TokenWithPosition token : tokens) {
        Style style = doc.getStyle(token.getType().name());
        doc.setCharacterAttributes(token.getStartPos(), token.getEndPos() - token.getStartPos(), style, true);
    }

    // Sözdizimi kontrolü ve hata vurgusu
    List<SyntaxAnalyzer.SyntaxResult> results = syntaxAnalyzer.analyzeAll(tokens, text);
    statusLabel.setText(results.isEmpty() ? "✅ Geçerli yapı" : results.get(0).getMessage());
    for (SyntaxAnalyzer.SyntaxResult result : results) {
        if (result.getMessage().startsWith("❌")) {
            Style errorStyle = doc.getStyle("ERROR");
            doc.setCharacterAttributes(result.getStartPos(), result.getEndPos() - result.getStartPos(), errorStyle, true);
        }
    }
}


Tüm metin önce varsayılan stile sıfırlanır.
LexicalAnalyzer ile token'lar alınır ve her token türüne uygun renk uygulanır.
SyntaxAnalyzer ile sözdizimi kontrol edilir; hata varsa ilgili aralık kırmızı alt çizgiyle işaretlenir.
Durum çubuğunda (statusLabel) sonuç gösterilir.

#4.3 Performans Optimizasyonu: DocumentListener ve Timer
highlightTimer = new Timer(300, e -> {
    highlight();
    highlightTimer.stop();
});
highlightTimer.setRepeats(false);

textPane.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { highlightTimer.restart(); }
    public void removeUpdate(DocumentEvent e) { highlightTimer.restart(); }
    public void changedUpdate(DocumentEvent e) { highlightTimer.restart(); }
});


Her metin değiştiğinde (insertUpdate, removeUpdate, changedUpdate), highlightTimer yeniden başlatılır.
300 ms içinde başka bir değişiklik olmazsa highlight() çalışır.
Bu, hızlı yazma sırasında GUI'nin kilitlenmesini önler ve performansı artırır.

#5. GUI (Grafiksel Kullanıcı Arayüzü) Uygulaması
EditorPanel sınıfı, Swing tabanlı GUI'nin temelini oluşturur:
public class EditorPanel extends JFrame {
    private JTextPane textPane;
    private JLabel statusLabel;
    private Timer highlightTimer;
    private LexicalAnalyzer analyzer;
    private SyntaxAnalyzer syntaxAnalyzer;

    public EditorPanel() {
        setTitle("Real-Time Syntax Highlighter");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textPane = new JTextPane();
        textPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Hazır");
        add(statusLabel, BorderLayout.SOUTH);

        analyzer = new LexicalAnalyzer();
        syntaxAnalyzer = new SyntaxAnalyzer();
        setupStyles();
        setupListeners();

        setVisible(true);
    }
}


Ana Pencere: JFrame.
Kod Yazma Alanı: JTextPane, Consolas fontu ile monospace bir görünüm.
Durum Çubuğu: JLabel ile ✅ Geçerli yapı veya ❌ Hata: ... mesajları.
Gecikmeli Renklendirme: Timer ve DocumentListener kombinasyonu.

#6. Karşılaşılan Zorluklar ve Çözümler
#6.1 Renk Kaymaları
Sorun: Bazen token'ların renklendirme aralıkları kayıyordu (örneğin, bir KEYWORD'ün rengi yanındaki token'a taşıyordu).Çözüm: LexicalAnalyzer'ın pozisyon hesaplamalarını (startPos, endPos) daha dikkatli kontrol ettik. Her token'ın aralığının doğru hesaplandığından emin olduk.
#6.2 İç İçe Yapılar
Sorun: İç içe yapılar (örneğin, if içinde while) ilk başta doğru analiz edilmiyordu.Çözüm: SyntaxAnalyzer'da analyzeAll metodu ile her bloğu ayrı ayrı kontrol eden bir yapı kurduk. Parantez ve süslü parantez eşleşmelerini takip ederek iç içe yapıların doğruluğunu sağladık.
#6.3 Performans Sorunları
Sorun: Hızlı yazma sırasında highlight() metodunun her karakterde çalışması GUI'de gecikmelere neden oluyordu.Çözüm: 300 ms gecikmeli bir Timer ve DocumentListener kombinasyonu kullandık. Böylece yalnızca yazma durduğunda renklendirme tetikleniyor.
#6.4 Hatalı Token Konumlandırma
Sorun: Eksik parantez veya ayırıcı (;) gibi durumlarda hata vurgusu yanlış yerde gösteriliyordu.Çözüm: Hatalı token'ın startPos ve endPos değerlerini bir önceki token'a atayarak, hatanın daha mantıklı bir yerde (örneğin, eksik ) yerine önceki token) gösterilmesini sağladık.
#6.5 Karmaşık Yapılar
Sorun: for döngüsü gibi karmaşık yapılar (örneğin, int i = 0; i < 5; i++) ilk başta doğru parse edilmiyordu.Çözüm: SyntaxAnalyzer'da for döngüsünün iç yapısını (başlatma, koşul, artırma) ayrı ayrı kontrol eden bir kural ekledik.
#7. Sonuç ve Gelecek Geliştirmeler
#7.1 Proje Sonucu
Bu proje ile:

Leksiksel analizde 7 farklı token türü (KEYWORD, NUMBER, STRING, OPERATOR, IDENTIFIER, PARENTHESIS, COMMENT) başarıyla ayrıştırıldı.
Sözdizimi analizi, Top-Down yaklaşımıyla temel Java yapılarını (if, while, for, class, method, değişken tanımları, println) ve iç içe yapıları eksiksiz parse etti.
Hatalı kısımlar kırmızı alt çizgiyle işaretlendi ve durum çubuğunda anlık geri bildirim sağlandı.
Gerçek zamanlı renklendirme, Timer ve DocumentListener ile performanslı bir şekilde uygulandı.
GUI, renklendirme, hata vurgulama ve durum mesajlarını içeren işlevsel bir mini-IDE deneyimi sundu.

Bu adımlar, ödevin tüm gereksinimlerini (kaynak kod, dökümantasyon, demo video ve makale) karşıladı.
#7.2 Gelecek Geliştirmeler

Daha Fazla Yapı Desteği: else, switch, lambda ifadeleri gibi yapılar eklenebilir.
Semantik Analiz: Değişkenlerin tanımlı olup olmadığını kontrol eden bir sembol tablosu eklenebilir.
Yorum ve String Desteği: Yorum satırlarının (//, /* */) ve string literal'ların daha iyi ayrıştırılması sağlanabilir.
Kod Temizliği: Kullanılmayan metodlar (örneğin, LexicalAnalyzer'da analyze) ve token türleri (UNKNOWN) kaldırılabilir.
Satır Numaraları: GUI'ye satır numaraları eklenerek hata ayıklama kolaylaştırılabilir.
Tema Desteği: Açık/koyu tema seçenekleri eklenebilir.

#8. Kısa Kod Parçacığı Örnekleri
#8.1 Token Türleri
enum TokenType {
    KEYWORD, NUMBER, STRING, OPERATOR, IDENTIFIER, PARENTHESIS, COMMENT
}

#8.2 Lexical Analiz: Sayı Token'ı
if (Character.isDigit(c)) {
    int start = i;
    while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
    if (i < input.length() && input.charAt(i) == '.') {
        i++;
        while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
    }
    tokens.add(new TokenWithPosition(TokenType.NUMBER, input.substring(start, i), start, i));
    continue;
}

#8.3 Syntax Analiz: if Bloğu
if (t.getType() == TokenType.KEYWORD && t.getValue().equals("if")) {
    int j = i + 1;
    while (j < tokens.size() && tokens.get(j).getType() == TokenType.WHITESPACE) j++;
    if (j >= tokens.size() || !tokens.get(j).getValue().equals("("))
        return new SyntaxResult("❌ Hata: if bloğunda '(' eksik", tokens.get(j-1).startPos, tokens.get(j-1).endPos);
}

#8.4 Renklendirme
for (LexicalAnalyzer.TokenWithPosition token : tokens) {
    Style style = doc.getStyle(token.getType().name());
    doc.setCharacterAttributes(token.getStartPos(), token.getEndPos() - token.getStartPos(), style, true);
}

#8.5 Hata Vurgulama
for (SyntaxAnalyzer.SyntaxResult result : results) {
    if (result.getMessage().startsWith("❌")) {
        Style errorStyle = doc.getStyle("ERROR");
        doc.setCharacterAttributes(result.getStartPos(), result.getEndPos() - result.getStartPos(), errorStyle, true);
    }
}

#9. Demo ve Sonraki Adımlar
Bu makale, GitHub Pages üzerinde yayımlanacak.Demo video şu örnekleri gösterecek:

"int x = 5;" → Renklendirme ve ✅ Geçerli yapı.
"if (x > 0) { x++; }" → Renklendirme ve ✅ Geçerli yapı.
"for (int i = 0; i < 5; i++) { ... }" → Renklendirme ve ✅ Geçerli yapı.
"if (x > 0 {" → { altında kırmızı çizgi, ❌ Hata: if bloğunda ')' eksik.
"int x = 5" → 5 altında kırmızı çizgi, ❌ Hata: for döngüsünde ')' eksik.

Video, YouTube'a yüklenip linki paylaşılacak.
#10. Sonuç
Bu proje, Java ve Swing kullanılarak gerçek zamanlı bir sözdizimi renklendirici ve hata vurgulayıcı mini-IDE geliştirdi. Temel Java yapılarını destekleyen, iç içe yapıları analiz eden ve kullanıcıya anlık geri bildirim sunan bir uygulama ortaya çıktı. LexicalAnalyzer ve SyntaxAnalyzer sınıfları, sırasıyla State Diagram & Program Implementation ve Top-Down Parsing yöntemleriyle ödevin gerekliliklerini karşıladı. Gelecekte daha fazla yapı ve semantik analiz desteği eklenerek bu uygulama daha güçlü bir hale getirilebilir.
#Kaynakça & Ekler  

Java Platform SE 11 – Swing Documentation (Oracle)  
"Programlama Dilleri Projesi" PDF (Ders Koordinatörü)  
Visual Studio Code Documentation

