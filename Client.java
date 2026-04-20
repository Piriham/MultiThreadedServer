import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    // Returns a Runnable → defines what each client thread will do
    public Runnable getRunnable() {
        return new Runnable() {
            @Override
            public void run() {

                int port = 8011;

                try {
                    // Socket → establishes connection to server (IP + port)
                    // "localhost" = same machine
                    Socket s = new Socket("localhost", port);

                    // Output stream → send data to server
                    // true → auto-flush ensures immediate sending
                    PrintWriter toServer = new PrintWriter(
                            s.getOutputStream(), true
                    );

                    // Input stream → receive data from server
                    // Converts byte stream → character stream → readable text
                    BufferedReader fromServer = new BufferedReader(
                            new InputStreamReader(s.getInputStream())
                    );

                    // Send request to server
                    toServer.println("Hello from client");

                    // Blocking call → waits for server response
                    String response = fromServer.readLine();

                    System.out.println("Response from server: " + response);

                    // Close connection (also closes streams)
                    s.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) {

        Client c = new Client();

        // Spawn 100 client threads → simulate multiple users connecting simultaneously
        for (int i = 0; i < 100; i++) {
            try {
                Thread t = new Thread(c.getRunnable());

                // Start each client thread
                t.start();

            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}