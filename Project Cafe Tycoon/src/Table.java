public class Table {
    int idMeja;
    int kapasitasKursi;
    String status;
    Customer currentCustomer;

    public Table(int idMeja, int kapasitasKursi) {
        this.idMeja = idMeja;
        this.kapasitasKursi = kapasitasKursi;
        this.status = "Free";
        this.currentCustomer = null;
    }

    public boolean occupyMeja(Customer c){
        if(!this.status.equalsIgnoreCase("Free")){
            System.out.println("Meja tidak tersedia!");
        }

        if(c.getJumlahOrang() > this.kapasitasKursi){
            System.out.println("Terlalu banyak orang!");
            return false;
        }

        this.currentCustomer = c;
        this.status = "Occupied";
        return true;
    }

    public void sajikanKopiKeMeja(Player player, GameEngine engine){
        if(!this.status.equalsIgnoreCase("Occupied") || currentCustomer == null){
            System.out.println("No customer on this table!");
            return;
        }

        String kopiDiTangan = player.getItemOnHand();
        String namaMenuRacikan = terjemahkanRacikan(kopiDiTangan);

        if(namaMenuRacikan.equalsIgnoreCase(currentCustomer.getJenisPesanan())){
            currentCustomer.kurangiPorsi();
            player.clearHand();

            System.out.println("1 Porsi " + currentCustomer.getJenisPesanan() + " berhasil disajikan!");
            System.out.println("Sisa porsi yang harus dibuat untuk meja ini: " + currentCustomer.getPorsiTersisa());

            if(currentCustomer.getPorsiTersisa() == 0){
                System.out.println("Semua pesanan di Meja " + idMeja + " selesai! Pelanggan mulai membayar");
                bayarPesanan(engine);
                kosongkanMeja();
            }
        }
        else{
            System.out.println("Salah kopi!, customer minta " + currentCustomer.getJenisPesanan());
        }
    }

    private String terjemahkanRacikan(String racikan) {
        if (racikan.equalsIgnoreCase("Kopi")) return "Espresso";
        if (racikan.equalsIgnoreCase("Kopi + Susu")) return "Latte";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Caramel")) return "Caramel Latte";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Chocolate")) return "Mochaccino";
        if (racikan.equalsIgnoreCase("Kopi + Susu + Boba")) return "Boba Coffee Latte";
        if (racikan.equalsIgnoreCase("Kopi + Whipped Cream")) return "Con Panna";
        return "Gagal";
    }

    public void kosongkanMeja(){
        this.currentCustomer = null;
        this.status = "Dirty";
    }

    public void bersihkanMeja(){
        this.status = "Free";
    }

    public void bayarPesanan(GameEngine engine){
        if(this.currentCustomer != null){
            int jumlahOrang = this.currentCustomer.getJumlahOrang();

            int totalUang = jumlahOrang * 50;
            int totalPoin = jumlahOrang * 50;

            InventoryManager.getInstance().tambahUang(totalUang);

            engine.tambahPoinLevel(totalPoin);
        }
    }
    
    public int getIdMeja() {
        return idMeja;
    }

    public void setIdMeja(int idMeja) {
        this.idMeja = idMeja;
    }

    public int getKapasitasKursi() {
        return kapasitasKursi;
    }

    public void setKapasitasKursi(int kapasitasKursi) {
        this.kapasitasKursi = kapasitasKursi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

}
