package org.example.ticketPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The TicketPool class represents a singleton pool of tickets with thread-safe methods for
 * adding and removing tickets. It also tracks the total number of tickets released and purchased.
 * This class uses a ReentrantLock to manage concurrent access to ticket operations.
 */
public class TicketPool {

    private final List<Integer> tickets = Collections.synchronizedList(new LinkedList<>());

    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    /** The singleton instance of the TicketPool. */
    private static TicketPool instance;

    /** The maximum ticket capacity of the pool. */
    private final int maxTicketCapacity;

    /** Lock to ensure thread-safe access to ticket operations. */
    private final ReentrantLock lock = new ReentrantLock();

    /** The total number of tickets released by vendors. */
    private int totalTicketsReleased = 0;

    /** The total number of tickets purchased by customers. */
    private int totalTicketsPurchased = 0;

    private final int initialTickets;




    /**
     * Private constructor to initialize the TicketPool with the initial ticket count and maximum capacity.
     * This constructor is private to enforce the singleton pattern.
     *
     * @param initialTickets The initial number of tickets available.
     * @param maxTickets The maximum capacity of tickets in the pool.
     */
    private TicketPool(int initialTickets, int maxTickets) {
        this.initialTickets = initialTickets;
        this.maxTicketCapacity = maxTickets;

        for (int i = 0; i < initialTickets; i++) {
            tickets.add(1);  // Adding intial tickets to the pool
        }
    }

    /**
     * Returns the singleton instance of TicketPool. If the instance is null, it initializes it
     * with the specified initial ticket count and maximum capacity.
     *
     * @param initialTickets The initial number of tickets available.
     * @param maxTickets The maximum capacity of tickets in the pool.
     * @return The singleton instance of TicketPool.
     */
    public static synchronized TicketPool getInstance(int initialTickets, int maxTickets) {
        if (instance == null) {
            instance = new TicketPool(initialTickets, maxTickets);
        }
        return instance;
    }

    /**
     * Adds a ticket to the pool, provided the current count is below the maximum ticket capacity.
     * Increments the total tickets released count.
     *
     * @param vendorID The ID of the vendor adding the ticket.
     */
    public boolean addTickets(String vendorID) {
        lock.lock();
        try {
            if (tickets.size() < maxTicketCapacity) {
//                availableTickets++;
                totalTicketsReleased++;  // Increment the released counter
                tickets.add(1);
                logger.info("{} released a ticket - tickets remaining {}", vendorID, tickets.size());
                return true;
            } else {
                logger.warn("Max capacity reached.");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a ticket from the pool for a customer, provided there are tickets available.
     * Increments the total tickets purchased count.
     *
     * @param customerId The ID of the customer purchasing the ticket.
     */
//
    public boolean removeTicket(String customerId) {
        lock.lock();
        try {
            if (!tickets.isEmpty()) {
//                availableTickets--;
                totalTicketsPurchased++;  // Increment the purchased counter
                tickets.remove(0);
                logger.info("{} purchased a ticket - tickets remaining {}", customerId, tickets.size());
                return true; // Successfully purchased a ticket
            } else {
                logger.warn("No tickets available for customer to purchase.");
                return false; // No tickets left
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Returns the current number of available tickets in the pool.
     *
     * @return The current number of available tickets.
     */
    public int getAvailableTickets() {
        return tickets.size();
    }

    /**
     * Returns the total number of tickets released by vendors.
     *
     * @return The total tickets released.
     */
    public int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }


    /**
     * Returns the total number of tickets purchased by customers.
     *
     * @return The total tickets purchased.
     */
    public int getTotalTicketsPurchased() {
        return totalTicketsPurchased;
    }
}
