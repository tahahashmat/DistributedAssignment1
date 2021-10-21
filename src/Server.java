import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server {
    private static final int port = 3500;
    private static ArrayList<ServerThread> clientsList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //creating a server object in order to run the startServer method in the main function

        Server s = new Server();
        s.startServer(port);

    }

    // created a startServer function which takes port as an argument (specified above). startServer basically

    public void startServer(int port) throws IOException {
        try (ServerSocket request = new ServerSocket(port)) {
            request.setReuseAddress(true);
            do {
                // accepting socket connectivity requests and printing out whenever a new client has joined
                Socket client = request.accept();
                System.out.println("New client connected");


                //use arraylists to track all the new clients joining and printing out the current number of clients in the chat
                ServerThread newClient = new ServerThread(client, clientsList);
                clientsList.add(newClient);
                newClient.start();
                System.out.println("The number of clients present in the chat are: " + clientsList.size());
            } while (true);

            }

    }
}

class ServerThread extends Thread {
    private final Socket socket;
    private final ArrayList<ServerThread> threadList;
    private PrintWriter output;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dtd = DateTimeFormatter.ofPattern("dd:MM:uuuu");


//ServerThread constructor

    public ServerThread(Socket socket, ArrayList<ServerThread> threads) {
        this.socket = socket;
        this.threadList = threads;
    }

    @Override
    public void run(){
        try {
            BufferedReader input = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            output = new PrintWriter(socket.getOutputStream(), true);

            //uses the printToAllClients method to notify all clients whenever a new client has joined the chat

            printToAllClients("A new client has joined");

            //using the buffer reader object to print out whatever messages each client is sending to the other clients

            while (true) {
                String outputString = input.readLine();
                if (outputString.equals("Exit")){
                    break;
                }
                printToAllClients(outputString);
                System.out.println("Server Received at "+ java.time.LocalDateTime.now().format(dtd) + " on " + java.time.LocalDateTime.now().format(dtf) + outputString);
            }

        } catch (IOException e) {
            System.out.println("Error occurred in server" + e.getStackTrace());
        }
    }
    private void printToAllClients(String outputString) {
        for (ServerThread st: threadList){
            st.output.println(outputString);
        }
    }
}