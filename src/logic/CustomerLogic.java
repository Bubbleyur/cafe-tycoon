package logic;

import entity.Customer;
import entity.TableEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerLogic {

    private final List<Customer> customers = new ArrayList<>();
    private int nextCustomerId = 1;
    private final Random random = new Random();
    private int spawnCooldown = 120;

    private final String[] customerNames = {
        "Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah"
    };

    public void update(GameEngine engine, DifficultyManager difficulty) {
        List<TableEntity> tables = engine.getDaftarMeja();

        if (spawnCooldown > 0) {
            spawnCooldown--;
        } else if (customers.size() < 8 && engine.getPelangganTersisaLevel() > 0) {
            spawnCustomer(40, 550);
            spawnCooldown = difficulty.getCustomerSpawnInterval();
        }

        List<Customer> toRemove = new ArrayList<>();

        for (Customer c : customers) {
            c.setBobOffset((int) (Math.sin(System.currentTimeMillis() * 0.005 + c.getId()) * 3));

            switch (c.getState()) {
                case SPAWNED:
                    c.setState(Customer.CustomerState.WAITING_FOR_TABLE);
                    break;

                case WAITING_FOR_TABLE:
                    TableEntity seatedAt = findTableForCustomer(tables, c);
                    if (seatedAt == null) {
                        engine.pemicuPelangganBaru(c);
                        seatedAt = findTableForCustomer(tables, c);
                    }
                    if (seatedAt != null) {
                        c.setAssignedTableId(seatedAt.getIdMeja());
                        c.setX(seatedAt.getSeatX());
                        c.setY(seatedAt.getSeatY());
                        c.setState(Customer.CustomerState.WAITING_FOR_ORDER);
                        c.setPatience(100.0);
                        c.setPatienceDecreaseRate(difficulty.getCustomerPatienceDecayRate());
                    } else {
                        int queueIndex = getQueueIndex(c);
                        c.setX(40);
                        c.setY(150 + (queueIndex * 70));
                        c.setPatience(c.getPatience() - difficulty.getCustomerPatienceDecayRate() * 0.3);
                        if (c.getPatience() <= 0) {
                            c.setState(Customer.CustomerState.LEAVING_ANGRY);
                        }
                    }
                    break;

                case WAITING_FOR_ORDER:
                    c.setPatience(c.getPatience() - c.getPatienceDecreaseRate());
                    if (c.getPatience() <= 0) {
                        c.setState(Customer.CustomerState.LEAVING_ANGRY);
                        releaseTable(tables, c);
                    }
                    break;

                case LEAVING_HAPPY:
                    c.setY(c.getY() + 4);
                    if (c.getY() > 620) {
                        toRemove.add(c);
                    }
                    break;

                case LEAVING_ANGRY:
                    c.setY(c.getY() + 5);
                    if (c.getY() > 620) {
                        toRemove.add(c);
                    }
                    break;

                default:
                    break;
            }
        }

        customers.removeAll(toRemove);
    }

    private TableEntity findTableForCustomer(List<TableEntity> tables, Customer c) {
        for (TableEntity t : tables) {
            if (t.getCurrentCustomer() == c) {
                return t;
            }
        }
        return null;
    }

    private void releaseTable(List<TableEntity> tables, Customer c) {
        for (TableEntity t : tables) {
            if (t.getCurrentCustomer() == c) {
                t.kosongkanMeja();
                break;
            }
        }
    }

    private void spawnCustomer(int x, int y) {
        String name = customerNames[random.nextInt(customerNames.length)];
        customers.add(new Customer(nextCustomerId++, name, x, y));
    }

    private int getQueueIndex(Customer c) {
        int index = 0;
        for (Customer other : customers) {
            if (other.getState() == Customer.CustomerState.WAITING_FOR_TABLE && other.getId() < c.getId()) {
                index++;
            }
        }
        return index;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void clearCustomers() {
        customers.clear();
        nextCustomerId = 1;
        spawnCooldown = 120;
    }
}
