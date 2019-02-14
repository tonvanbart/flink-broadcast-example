package org.vanbart.servers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * A minimal server which sends random data on a socket.
 */
public class Dataserver {

    private static Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(Dataserver.class);

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(7777);
        log.info("Starting server.");
        while (true) {
            Socket socket = serverSocket.accept();
            log.info("Accepted connection. {}", socket);
            new Thread(() -> sendData(socket) ).start();
        }
    }

    public static void sendData(Socket socket) {
        boolean running = true;
        while (running) {
            try {
                log.debug("sending data");
                String value = "|" + (1 + random.nextInt(6));
                socket.getOutputStream().write(value.getBytes());
                socket.getOutputStream().flush();
                Thread.sleep(1000);
            } catch (IOException e) {
                log.warn("Got IO exception", e);
                running = false;
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

}
