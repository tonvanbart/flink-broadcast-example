package org.vanbart.servers;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

/**
 * Minimal server which reads from stdin and broadcasts the value to all connected sockets.
 */
public class StateServer {

    private static final Logger log = LoggerFactory.getLogger(StateServer.class);

    private static Set<Socket> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        URL resource = Dataserver.class.getClassLoader().getResource("default-log4j.properties");
        PropertyConfigurator.configure(resource);
        ServerSocket serverSocket = new ServerSocket(7778);
        new Thread( () -> runServer(serverSocket)).start();
        log.info("starting broadcast state server");
        while (true) {
            updateListeners();
        }
    }

    private static void updateListeners() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter multiplication factor");
            int factor = scanner.nextInt();
            for (Iterator<Socket> iter = clients.iterator(); iter.hasNext(); ) {
                Socket socket = iter.next();
                try {
                    log.info("Notifying {}", socket);
                    socket.getOutputStream().write((factor + " ").getBytes());
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    log.warn("error writing {}", e);
                    iter.remove();
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("Input should be an integer");
        }
    }

    private static void runServer(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                log.info("Accepted connection: {}", socket);
                clients.add(socket);
            } catch (IOException e) {
                log.warn("Incoming connection failed, ignoring.");
            }
        }
    }
}
