import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) throws IOException {

        try (Socket socket = new Socket("localhost", 3500)) {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);
            String input;
            String name;

            //prompt the client to enter name as soon as they join
            System.out.print("Enter your name: ");
            name = sc.nextLine();

            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();

            //taking in the clients messages and throwing an if statement to allow the clients to Exit
            while (true) {
                String message = ("" + name + "" + ": ");
                input = sc.nextLine();
                out.println(" - " + message + " " + input);

                if (input.equals("Exit")) {
                    out.println("The user you were talking to has left the chat.");
                    break;
                }
            }
         } catch(Exception e) {
            System.out.println("Exception in client" + e.getStackTrace());

        }

    }
}

class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader input;

    public ClientThread(Socket s) throws IOException {
        this.socket = s;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run () {
        try {
            while(true){
                String response = input.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}


