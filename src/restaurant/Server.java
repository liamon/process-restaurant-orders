/* Liam O'Neill - 15349756 */
package restaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Liam
 */
public class Server implements Runnable {

    
    private static BlockingQueue<String> orders;
    private String name;
    private Map<String, Integer> menuItems = new HashMap<>();
    
    public Server(String name) {
        this.name = "Server " + name;
    }
    
    @Override
    public void run() {
        Random random = new Random();
        boolean areOrdersLeft = true;
        while (areOrdersLeft) {
            String currentOrder = "";
            try {
                // 3 seconds seems to be a good time to wait if an item
                // has been put in the ArrayBlockingQueue or not.
                currentOrder = orders.poll(3, TimeUnit.SECONDS);
                // In the version of this program I submitted, I did not understand
                // that the Servers were supposed to serve the food as soon as it
                // was prepared, so I included the next line (commented out for GitHub):
                //Thread.sleep(250 + random.nextInt(1250));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            // If there were any orders left, a Chef would have put an item in
            // the queue before the poll method timed out and currentOrder would
            // not have been assigned to null.
            if (currentOrder == null) {
                break;
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
            
            System.out.println(name + " is serving " + currentOrder + ".");
        }
    }
    
    public static void setOrderAmount(int orderAmount) {
        orders = new ArrayBlockingQueue(orderAmount);
    }
    
    public static void giveOrderToServers(String order) {
        try {
            orders.put(order);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public String finishedServing() {
        return name + " finished serving " + totalOrders() +
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
