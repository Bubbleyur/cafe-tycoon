package logic;

public final class DrinkRecipe {

    public static final String[] DAFTAR_MENU = {
        "Espresso", "Latte", "Caramel Latte", "Mochaccino", "Boba Coffee Latte", "Con Panna"
    };

    private DrinkRecipe() {}

    public static String terjemahkanRacikan(String racikan) {
        if (racikan == null) return "Gagal";
        
        boolean isIced = false;
        String clean = racikan;
        
        // Check if recipe contains Ice (case-insensitive)
        if (racikan.toLowerCase().contains("ice")) {
            isIced = true;
            // Remove " + Ice", "Ice + ", or just "Ice"
            clean = racikan.replaceAll("(?i)\\s*\\+\\s*ice", "")
                           .replaceAll("(?i)ice\\s*\\+\\s*", "")
                           .replaceAll("(?i)ice", "")
                           .trim();
        }
        
        String baseResult = "Gagal";
        if (clean.equalsIgnoreCase("Kopi")) baseResult = "Espresso";
        else if (clean.equalsIgnoreCase("Kopi + Susu")) baseResult = "Latte";
        else if (clean.equalsIgnoreCase("Kopi + Susu + Caramel")) baseResult = "Caramel Latte";
        else if (clean.equalsIgnoreCase("Kopi + Susu + Chocolate")) baseResult = "Mochaccino";
        else if (clean.equalsIgnoreCase("Kopi + Susu + Boba")) baseResult = "Boba Coffee Latte";
        else if (clean.equalsIgnoreCase("Kopi + Whipped Cream")) baseResult = "Con Panna";
        
        if (baseResult.equals("Gagal")) {
            return "Gagal";
        }
        
        return isIced ? "Iced " + baseResult : baseResult;
    }
}
