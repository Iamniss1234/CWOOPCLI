package org.example;

public interface User extends Runnable {
    @Override
    default void run() {
    }


}
