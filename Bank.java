import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;

public class Bank {

    // maximum number of tellers
    public static final int MAX_TELLERS = 3;
    // maximum number of customers
    public static final int MAX_CUSTOMERS = 50;

    // tells if a teller is free and within the queue
    public static Semaphore teller_ready = new Semaphore(0, true);
    // queue of open tellers
    public static Queue<Teller> tellers_line = new LinkedList<>();
    // enforces mutex on tellers_line
    public static Semaphore tellers_mutex = new Semaphore(1, true);

    // keeps count of customers
    public static int customers_count = 0;
    // enforces mutex on customers_count
    public static Semaphore customers_mutex = new Semaphore(1, true);

    // tells if manager is free
    // 1 permit = 1 manager permit given
    public static Semaphore manager = new Semaphore(1, true);

    // tells if safe is free
    // 2 permits = 2 safe permits given
    public static Semaphore safe = new Semaphore(2, true);

    public static class Teller extends Thread {
        int tellerId; // stores teller number

        Customer customer; // stores customer reference 

        Semaphore teller_turn = new Semaphore(0);

        // constructor
        public Teller(int id) {
           tellerId = id;
        }

        public void run() {
            while (true) {
                System.out.println("Teller " + tellerId + " is ready to serve.");

                // --------
                // wait for tellers queue mutex
                try {
                    tellers_mutex.acquire();
                } catch (Exception e) {
                }

                // add teller to ready queue
                tellers_line.add(this);
                System.out.println("Teller " + tellerId + " is waiting for a customer.");

                // signal tellers queue mutex
                tellers_mutex.release();
                // --------

                // signal teller ready
                teller_ready.release();

                // --------
                // wait for customers count mutex
                try {
                    customers_mutex.acquire();
                } catch (Exception e) {
                }

                // exit condition
                // check if all customers served
                if (customers_count >= (MAX_CUSTOMERS - 2)) {
                    
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    customers_mutex.release();
                    break;
                }

                // signal customers count mutex
                customers_mutex.release();
                // --------

                // wait for teller turn
                try {
                    teller_turn.acquire();
                } catch (Exception e) {
                }

                System.out.println("Teller " + tellerId + " is serving Customer " + customer.customerId + "."); 

                // signal customer turn
                customer.customer_turn.release();

                // wait for teller turn
                try {
                    teller_turn.acquire();
                } catch (Exception e) {
                }

                // process transaction choice
                if (customer.choice == 1) {
                    System.out.println("Teller " + tellerId + " is handling the withdrawal transaction.");
                
                    System.out.println("Teller " + tellerId + " is going to the manager.");

                    // wait for manager resource
                    try {
                        manager.acquire();
                    } catch (Exception e) {
                    }

                    System.out.println("Teller " + tellerId + " is getting the manager's permission.");

                    // delay to simulate work
                    // delay is random between 5 to 30 milliseconds
                    try {
                        Random rand = new Random();
                        Thread.sleep(rand.nextInt(25) + 5);
                    } catch (Exception e) {
                    }

                    // release manager resource
                    manager.release();

                    System.out.println("Teller " + tellerId + " got the manager's permission."); 

                } else {
                    System.out.println("Teller " + tellerId + " is handling the deposit transaction.");
                }

                // go to the safe
                System.out.println("Teller " + tellerId + " is going to the safe.");

                // wait for safe resource
                try {
                    safe.acquire();
                } catch (Exception e) {
                }

                System.out.println("Teller " + tellerId + " is in the safe.");

                // delay to simulate work
                // delay is random between 10 and 50 milliseconds
                try {
                    Random rand = new Random();
                    Thread.sleep(rand.nextInt(40) + 10);
                } catch (Exception e) {
                }

                System.out.println("Teller " + tellerId + " is leaving the safe.");

                // release safe resource
                safe.release();

                System.out.println("Teller " + tellerId + " finishes Customer " + customer.customerId + "'s " + (customer.choice == 1 ? "withdrawal" : "deposit") + " transaction."); 

                // signal customer turn
                customer.customer_turn.release();

                // --------
                // wait for customer count mutex
                try {
                    customers_mutex.acquire();
                } catch (Exception e) {
                }

                // update customer count
                customers_count += 1;

                // signal customer count mutex
                customers_mutex.release();
                // --------

                // DEBUG LINE
                //System.out.println("DEBUG: Teller " + tellerId + " Cutomer Count: " + customers_count);
            }
            System.out.println("Teller " + tellerId + " is leaving for the day.");
        }
    }
    

    public static class Customer extends Thread {
        int customerId; // stores customer number
        int choice; // 0 = deposit, 1 = withdrawal

        Semaphore customer_turn = new Semaphore(0);

        // constructor
        public Customer(int id) {
           customerId = id;
        }

        public void run() {
            System.out.println("Customer " + customerId + " is going to the bank.");
            // sets random transaction
            choice = (int)(Math.random() * 2); 

            System.out.println("Customer " + customerId + " is getting in line.");

            // wait for a teller to be ready
            // teller_ready gives access to threads in the order of request
            // adds customer to line in FIFO queue
            try {
                teller_ready.acquire();
            } catch (Exception e) {
            }

            // --------
            // wait for tellers queue mutex
            try {
                tellers_mutex.acquire();
            } catch (Exception e) {
            }    

            System.out.println("Customer " + customerId + " is selecting a teller.");

            // remove teller from available queue
            Teller teller = tellers_line.remove();

            // signal tellers queue mutex
            tellers_mutex.release();
            // --------

            System.out.println("Customer " + customerId + " goes to Teller " + teller.tellerId + ".");

            teller.customer = this;
            System.out.println("Customer " + customerId + " introduces itself to Teller " + teller.tellerId + ".");

            // signal teller turn
            teller.teller_turn.release(); 

            // wait for customer turn
            try {
                customer_turn.acquire();
            } catch (Exception e) {
            }

            System.out.println("Customer " + customerId + " asks for " + (choice == 0 ? "deposit" : "withdrawal") + " transaction.");

            // sign teller turn
            teller.teller_turn.release();

            // wait for teller to be done
            try {
                customer_turn.acquire();
            } catch (Exception e) {
            }

            System.out.println("Customer " + customerId + " thanks the Teller " + teller.tellerId + " and leaves the bank.");
        }
    } 

    public static void main(String[] args) {

        // manage all threads
        ArrayList<Thread> threads = new ArrayList<>();

        // instantiate tellers 
        for (int i=0; i<MAX_TELLERS; i++) {
            Thread temp = new Teller(i);
            temp.start();
            threads.add(temp);
        }

        // instantiate customers
        for (int i=0; i<MAX_CUSTOMERS; i++) {
            Thread temp = new Customer(i);
            temp.start();
            threads.add(temp);
        }

        // wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (Exception e) {
            }
        }

        System.out.println("The bank closes for the day.");
    }
}