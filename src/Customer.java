import java.util.Random;

public class Customer {
    private String jenisPesanan;
    private int jumlahOrang;
    private int porsiTersisa;
    // private int tingkatSabar;

    private static final String[] DAFTAR_MENU = {
        "Espresso", "Latte", "Caramel Latte", "Mochaccino", "Boba Coffee Latte", "Con Panna"
    };

    public Customer() {
        Random rand = new Random();
        this.jumlahOrang = rand.nextInt(4) + 1;
        this.porsiTersisa = this.jumlahOrang;
        
        acakPesanan();
    }

    public void acakPesanan(){
        Random rand = new Random();
        int indeksAcak = rand.nextInt(DAFTAR_MENU.length);
        this.jenisPesanan = DAFTAR_MENU[indeksAcak];
    }

    public void rerollJumlah(){
        Random rand = new Random();
        this.jumlahOrang = rand.nextInt(4) + 1;
        this.porsiTersisa = this.jumlahOrang;
    }

    public void kurangiPorsi(){
        if(this.porsiTersisa > 0){
            this.porsiTersisa--;
        }
    }

    public int getPorsiTersisa() {
        return porsiTersisa;
    }

    public boolean isMarah(){
        return true;
    }

    public String getJenisPesanan() {
        return jenisPesanan;
    }

    public void setJenisPesanan(String jenisPesanan) {
        this.jenisPesanan = jenisPesanan;
    }

    public int getJumlahOrang() {
        return jumlahOrang;
    }

    public void setJumlahOrang(int jumlahOrang) {
        this.jumlahOrang = jumlahOrang;
    }

}
