package logic;

import entity.Customer;
import entity.TableEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerLogic {

    private List<Customer> customers;
    private int nextCustomerId = 1;
    private Random random = new Random();
    private int spawnCooldown = 0;

    // Names for cafe customers
    private String[] customerNames = {"Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah"};

    public CustomerLogic() {
        this.customers = new ArrayList<>();
    }

    public void update(List<TableEntity> tables, InventoryManager inventory, DifficultyManager difficulty) {
        // 1. Spawning Customers
        if (spawnCooldown > 0) {
            spawnCooldown--;
        } else if (customers.size() < 5) { // Max 5 active customers
            spawnCustomer(difficulty);
            spawnCooldown = difficulty.getCustomerSpawnInterval();
        }

        // 2. Process Customer AI States
        List<Customer> toRemove = new ArrayList<>();
        
        for (Customer c : customers) {
            // Apply floating bobbing animation
            c.setBobOffset((int)(Math.sin(System.currentTimeMillis() * 0.005 + c.getId()) * 3));

            switch (c.getState()) {
                case SPAWNED:
                    c.setState(Customer.CustomerState.WAITING_FOR_TABLE);
                    break;

                case WAITING_FOR_TABLE:
                    // Find a clean, vacant table
                    TableEntity openTable = null;
                    for (TableEntity t : tables) {
                        if (t.getState() == TableEntity.TableState.CLEAN && !t.isOccupied()) {
                            openTable = t;
                            break;
                        }
                    }

                    if (openTable != null) {
                        // Seat customer
                        openTable.setOccupied(true);
                        openTable.setState(TableEntity.TableState.OCCUPIED);
                        openTable.setAssignedCustomerId(c.getId());
                        
                        c.setAssignedTableId(openTable.getTableId());
                        c.setState(Customer.CustomerState.SEATED);
                        c.setX(openTable.getSeatX());
                        c.setY(openTable.getSeatY());
                        
                        // Patience reset for order phase
                        c.setPatience(100.0);
                        c.setPatienceDecreaseRate(difficulty.getCustomerPatienceDecayRate());
                    } else {
                        // Waiting in queue spot (staggered visually on the left side)
                        int queueIndex = getQueueIndex(c);
                        c.setX(40);
                        c.setY(150 + (queueIndex * 70));
                        
                        // Slowly lose patience while in line
                        c.setPatience(c.getPatience() - (difficulty.getCustomerPatienceDecayRate() * 0.3));
                        if (c.getPatience() <= 0) {
                            c.setState(Customer.CustomerState.LEAVING_ANGRY);
                        }
                    }
                    break;

                case SEATED:
                    // Thinking of what to order (Brief wait)
                    c.setPatience(c.getPatience() - (c.getPatienceDecreaseRate() * 0.5));
                    if (c.getPatience() <= 40.0) {
                        c.setState(Customer.CustomerState.WAITING_FOR_ORDER);
                        c.setOrderedItem("Completed Cafe Drink");
                        c.setPatience(100.0); // Reset patience to wait for order delivery
                    }
                    break;

                case WAITING_FOR_ORDER:
                    // Waiting for player to deliver drink
                    c.setPatience(c.getPatience() - c.getPatienceDecreaseRate());
                    if (c.getPatience() <= 0.0) {
                        c.setState(Customer.CustomerState.LEAVING_ANGRY);
                        
                        // Table becomes dirty when they storm off
                        TableEntity tbl = getTableById(tables, c.getAssignedTableId());
                        if (tbl != null) {
                            tbl.setOccupied(false);
                            tbl.setState(TableEntity.TableState.DIRTY);
                            tbl.setAssignedCustomerId(-1);
                        }
                    }
                    break;

                case EATING:
                    // Shrink customer/eating animation
                    c.setRenderScale(0.9 + Math.abs(Math.sin(System.currentTimeMillis() * 0.01)) * 0.1);
                    
                    // Consume time is represented by patience recovery/decay logic in custom states
                    // Here we will use patience as an "eating timer". Once it depletes to 0, they finish.
                    c.setPatience(c.getPatience() - 0.4); // Eating speed
                    if (c.getPatience() <= 10.0) {
                        c.setState(Customer.CustomerState.LEAVING_HAPPY);
                        c.setRenderScale(1.0);
                        
                        // Pay money and clean state transition
                        inventory.addMoney(15); // $15 per completed drink!
                        difficulty.addScore(25); // +25 points
                        
                        TableEntity tbl = getTableById(tables, c.getAssignedTableId());
                        if (tbl != null) {
                            tbl.setOccupied(false);
                            tbl.setState(TableEntity.TableState.DIRTY);
                            tbl.setAssignedCustomerId(-1);
                        }
                    }
                    break;

                case LEAVING_HAPPY:
                    // Animate walk out of screen (smoothly move to exit)
                    c.setY(c.getY() + 4);
                    if (c.getY() > 600) {
                        toRemove.add(c);
                    }
                    break;

                case LEAVING_ANGRY:
                    // Storm off
                    c.setY(c.getY() + 5);
                    if (c.getY() > 600) {
                        toRemove.add(c);
                        inventory.adjustReputation(-1); // Lose a reputation star
                    }
                    break;
            }
        }

        customers.removeAll(toRemove);
    }

    private void spawnCustomer(DifficultyManager difficulty) {
        String name = customerNames[random.nextInt(customerNames.length)];
        
        // Spawn entrance coordinates
        int sx = 40;
        int sy = 550;
        
        Customer c = new Customer(nextCustomerId++, name, sx, sy);
        c.setPatienceDecreaseRate(difficulty.getCustomerPatienceDecayRate());
        customers.add(c);
    }

    private int getQueueIndex(Customer c) {
        int index = 0;
        for (Customer other : customers) {
            if (other.getState() == Customer.CustomerState.WAITING_FOR_TABLE) {
                if (other.getId() < c.getId()) {
                    index++;
                }
            }
        }
        return index;
    }

    private TableEntity getTableById(List<TableEntity> tables, int tableId) {
        for (TableEntity t : tables) {
            if (t.getTableId() == tableId) {
                return t;
            }
        }
        return null;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
