import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

public class Server {

    // Consumer<Socket> = takes a client socket and processes it (no return value)
    // This defines how EACH client connection will be handled
    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            try {
                // Output stream → used to SEND data to client
                // true → auto-flush ensures data is sent immediately
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);

                // Input stream → used to RECEIVE data from client
                // InputStreamReader converts bytes → characters
                // BufferedReader allows efficient reading (readLine)
                BufferedReader fromClient = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );

                // Blocking call → waits until client sends a message
                String message = fromClient.readLine();

                System.out.println("Message from the client: " + message);

                // Send response back to client
                toClient.println("Hello from the server");

                // Close resources (connection ends here)
                toClient.close();
                fromClient.close();
                clientSocket.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    }

    public static void main(String[] args) throws IOException {

        Server s = new Server();
        int port = 8011;

        // ServerSocket → listens for incoming client connections on given port
        ServerSocket serverSocket = new ServerSocket(port);

        // If no NEW client connects within 10 seconds → accept() throws exception
        serverSocket.setSoTimeout(10000);

        System.out.println("Server running on port: " + port);

        // Infinite loop → server keeps accepting clients
        while (true) {
            try {
                // accept() blocks → waits for a client to connect
                // Once connected → returns a Socket (represents that client)
                Socket client = serverSocket.accept();

                // Create a new thread for each client
                // This allows handling multiple clients concurrently
                Thread thread = new Thread(() -> s.getConsumer().accept(client));

                // Start the thread → executes client-handling logic
                thread.start();

            } catch (SocketTimeoutException ex) {
                // Happens if no client connects within 10 seconds
                System.out.println("Client didnt connect in stipulated time");

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}