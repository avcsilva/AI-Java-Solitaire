package solitaire.model;

/** The four suits of the deck (RF04) and their colors (RF06). */
public enum Suit {
    HEARTS('H', "♥", SuitColor.RED),
    DIAMONDS('D', "♦", SuitColor.RED),
    CLUBS('C', "♣", SuitColor.BLACK),
    SPADES('S', "♠", SuitColor.BLACK);

    private final char code;
    private final String symbol;
    private final SuitColor color;

    Suit(char code, String symbol, SuitColor color) {
        this.code = code;
        this.symbol = symbol;
        this.color = color;
    }

    public SuitColor color() {
        return color;
    }

    public boolean hasSameColorAs(Suit other) {
        return color == other.color;
    }

    /** Symbol used to draw the suit on a card. */
    public String symbol() {
        return symbol;
    }

    char code() {
        return code;
    }

    static Suit fromCode(char code) {
        for (Suit suit : values()) {
            if (suit.code == code) {
                return suit;
            }
        }
        throw new IllegalArgumentException("Unknown suit code: " + code);
    }
}
