/* Liam O'Neill - 15349756 */
package restaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Liam
 */
public class Chef implements Runnable {
    
    private static Lock orderLock = new ReentrantLock();
    private static Queue<String> orders;
    
    private String name;
    private Map<String, Integer> menuItems = new HashMap<>();
    
    public Chef(String name) {
        this.name = "Chef " + name;
    }
    
    public static void setOrders(Queue<String> orders) {
        Chef.orders = orders;
    }
    
    @Override
    public void run() {
        Random random = new Random();
        while (!orders.isEmpty()) {
            String currentOrder = "";
            orderLock.lock();
            // It is good real-world practice to use a try-finally statement with
            // Locks to guarantee they will be unlocked - see https://stackoverflow.com/a/6950098
            try {
                currentOrder = orders.remove();
            } finally {
                orderLock.unlock();
            }
            
            String orderWithoutNumber = removeNumberFromOrder(currentOrder);
            if (menuItems.containsKey(orderWithoutNumber)) {
                Integer amountOfOrders = menuItems.get(orderWithoutNumber);
                // There will not be any problems with two of the same key being in
                // the map, as put just overwrites the old value linked to the key.
                menuItems.put(orderWithoutNumber, amountOfOrders + 1);
            } else {
                menuItems.put(orderWithoutNumber, 1);
            }
            
            System.out.println(name + " is preparing " + currentOrder);
            Server.giveOrderToServers(currentOrder);
            try {
                Thread.sleep(250 + random.nextInt(1750));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String finishedPreparing() {
        return name + " finished preparing " + totalOrders() +
                " orders including " + individualOrderTotals() + ".";
    }
    
    private String removeNumberFromOrder(String order) {
        // The regex here means a space followed by one or more digits.
        return order.replaceAll(" \\d+", "");
    }
    
    private int totalOrders() {
        int total = 0;
        for (int number : menuItems.values()) {
            total += number;
        }
        return total;
    }
    
    private String individualOrderTotals() {
        // It is more efficient to use StringBuilder here instead of String, as
        // I will be appending to a string inside a for loop.
        StringBuilder sb = new StringBuilder();
        int itemCount = 0;
        // See https://stackoverflow.com/a/1066607
        for (Map.Entry<String, Integer> entry : menuItems.entrySet()) {
            sb.append(entry.getValue());
            sb.append(" ");
            sb.append(entry.getKey());
            if (itemCount == menuItems.size() - 2) {
                // There needs to be an "and" after the second-last menu item
                // and before the last item.
                sb.append(" and ");
            } else if (itemCount < menuItems.size() - 1) {
                // As this is an else if block, a comma will not get appended to
                // the second-last element even though menuItems.size() - 2 < menuItems.size() - 1.
                sb.append(", ");
            } // else itemCount == menuItems.size() - 1 so it's the last entry, append nothing.
            itemCount++;
        }
        return sb.toString();
    }
}
