package org.gym;

public enum MembershipDuration {
    ONE_MONTH("1 month", 30),
    THREE_MONTHS("3 months", 90),
    SIX_MONTHS("6 months", 180),
    YEAR("1 year", 365);

    private final String label;
    private final int durationDays;

    MembershipDuration(String label, int durationDays) {
        this.label = label;
        this.durationDays = durationDays;
    }

    public String getLabel() {
        return label;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public static MembershipDuration fromLabel(String input) {
        for (MembershipDuration type : values()) {
            if (type.label.equalsIgnoreCase(input.trim()) ||
                    type.name().equalsIgnoreCase(input.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unexpected membership duration: " + input);
    }
}
