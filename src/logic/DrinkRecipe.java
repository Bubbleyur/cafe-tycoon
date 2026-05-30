package logic;

/**
 * Sama persis dengan {@code Table.terjemahkanRacikan} di protojaden.
 */
public final class DrinkRecipe {

    public static final String[] DAFTAR_MENU = {
        "Espresso", "Latte", "Caramel Latte", "Mochaccino", "Boba Coffee Latte", "Con Panna"
    };

    private DrinkRecipe() {}

    public static String terjemahkanRacikan(String racikan) {
        if (racikan.equalsIgnoreCase("Kopi")) return "Espresso";
        if (racikan.equalsIgnoreCase("Kopi + Susu")) return "Latte";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Caramel")) return "Caramel Latte";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Chocolate")) return "Mochaccino";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Boba")) return "Boba Coffee Latte";
        if (racikan.equalsIgnoreCase("Kopi + Whipped Cream")) return "Con Panna";
        return "Gagal";
    }
}
