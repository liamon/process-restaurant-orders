/* Liam O'Neill - 15349756 */
package restaurant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Liam
 */
public class Main {

    // Note this is a constant, but not public as it does not need to be.
    private static final String ORDER_LIST_FILE = "orderList.txt";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Queue<String> orders = readOrdersFromFile();
        Chef.setOrders(orders);
        
        Chef chefJohn = new Chef("John");
        Chef chefMark = new Chef("Mark");
        Server serverKatie = new Server("Katie");
        Server serverAndrew = new Server("Andrew");
        Server serverEmily = new Server("Emily");
        
        Thread[] restaurantThreads = new Thread[]{
            new Thread(chefJohn), new Thread(chefMark),
            new Thread(serverKatie), new Thread(serverAndrew), new Thread(serverEmily)
        };
        
        Server.setOrderAmount(orders.size());
        
        for(Thread t : restaurantThreads) {
            t.start();
        }
        
        // This for loop will not finish until all the Threads have stopped
        // running - see https://stackoverflow.com/a/1252202
        // This needs to be in a separate for loop to the last one, as otherwise
        // each Thread would only start after the previous one finished.
        for (Thread t : restaurantThreads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        System.out.println(chefJohn.finishedPreparing());
        System.out.println(chefMark.finishedPreparing());
        System.out.println(serverKatie.finishedServing());
        System.out.println(serverAndrew.finishedServing());
        System.out.println(serverEmily.finishedServing());
    }
    
    private static Queue<String> readOrdersFromFile() {
        Queue<String> orders = new LinkedList<>();
        BufferedReader orderListInput = null;
        try {
            orderListInput = new BufferedReader(new FileReader(ORDER_LIST_FILE));
            String order;
            // See https://www.roseindia.net/java/beginners/java-read-file-line-by-line.shtml
            while ((order = orderListInput.readLine()) != null) {
                orders.add(order);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (orderListInput != null) {
                try {
                    orderListInput.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return orders;
    }
}
