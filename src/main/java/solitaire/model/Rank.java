package solitaire.model;

/** Card ranks in their total order, from Ace to King (RF05). */
public enum Rank {
    ACE("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");

    private final String label;

    Rank(String label) {
        this.label = label;
    }

    /** Short label used to draw the rank on a card. */
    public String label() {
        return label;
    }

    /** True when this rank comes right after {@code other} (e.g. 7 is immediately above 6). */
    public boolean isImmediatelyAbove(Rank other) {
        return ordinal() == other.ordinal() + 1;
    }

    /** True when this rank comes right before {@code other} (e.g. 6 is immediately below 7). */
    public boolean isImmediatelyBelow(Rank other) {
        return other.isImmediatelyAbove(this);
    }

    static Rank fromLabel(String label) {
        for (Rank rank : values()) {
            if (rank.label.equals(label)) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Unknown rank label: " + label);
    }
}
