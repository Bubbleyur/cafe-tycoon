public class Player {
    private int x, y; 
    private String itemOnHand;

    public Player(int x, int y, String itemOnHand) {
        this.x = x;
        this.y = y;
        this.itemOnHand = "None";
    }

    public void moveTo(int targetX, int targetY){
        this.x = targetX;
        this.y = targetY;
    }

    public void clearHand(){
        itemOnHand = "None";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getItemOnHand() {
        return itemOnHand;
    }

    public String translateItemOnHand(String itemOnHand){
        if(itemOnHand == null) return "Kosong";

        String item = itemOnHand.trim();

        if(itemOnHand.equalsIgnoreCase("None")) return "Kosong";

        if (item.equalsIgnoreCase("Kopi")) return "Espresso";
        if (item.equalsIgnoreCase("Kopi + Susu")) return "Latte";
        if (item.equalsIgnoreCase("Kopi + Susu + Caramel")) return "Caramel Latte";
        if (item.equalsIgnoreCase("Kopi + Susu + Chocolate")) return "Mochaccino";
        if (item.equalsIgnoreCase("Kopi + Susu + Boba")) return "Boba Coffee Latte";
        if (item.equalsIgnoreCase("Kopi + Whipped Cream")) return "Con Panna";
        
        // Jika tidak ada yang cocok, tampilkan string aslinya untuk debugging
        return itemOnHand;
    }

    public void setItemOnHand(String itemOnHand) {
        this.itemOnHand = itemOnHand;
    }

}
