public class ToppingStation extends Station{
    private String[] daftarTopping;

    public ToppingStation(int x, int y){
        super(x, y, "Topping Counter");
        this.daftarTopping = new String[]{"Caramel", "Chocolate", "Whipped Cream", "Boba"};
    }

    @Override
    public void interact(Player player, String subChoice) {
        InventoryManager inventory = InventoryManager.getInstance();
        String itemTangan = player.getItemOnHand();
        boolean toppingValid = false;

        for(String t : this.daftarTopping){
            if(t.equalsIgnoreCase(subChoice)){
                toppingValid = true;
                break;
            }
        }

        if(toppingValid){
            if(itemTangan.equalsIgnoreCase("None")){
                System.out.println("Hand is empty");
            }
            else if(itemTangan.equalsIgnoreCase("Kopi") || itemTangan.equalsIgnoreCase("Kopi + Susu")){
                if(inventory.getStokTopping() > 0){
                    inventory.kurangiStokTopping(1);
                    player.setItemOnHand(itemTangan + " + " + subChoice);
                    System.out.println("Berhasil menambahkan topping: " + subChoice);
                }
                else{
                    System.out.println("Stok topping sudah habis");
                }
            }
            else{
                System.out.println("Kopi sudah ada topping");
            }
        }
        else{
            System.out.println("Topping doesn't exist");
        }
    }   
}
