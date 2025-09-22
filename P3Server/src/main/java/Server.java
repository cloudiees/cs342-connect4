
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Server{

    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    Database database;
    ArrayList<ClientThread> matchmakingList = new ArrayList<>();
    ArrayList<Match> matches = new ArrayList<>();

    Server(Consumer<Serializable> call){
        callback = call;
        server = new TheServer(this);
        server.start();
    }


    public class TheServer extends Thread{
        Server server;

        TheServer(Server server){
            this.server = server;
        }

        public void run() {
            try {
                database = new Database();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try(ServerSocket mysocket = new ServerSocket(5555);){
                System.out.println("Server is waiting for a client!");
                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count, server);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();
                    count++;
                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }


    public class ClientThread extends Thread{
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        Message.Account account;
        MessageHandler.FindMatch matchMakingThread;
        CountDownLatch latch;
        boolean awaitingForRematch = false;
        boolean wantsRematch = false;
        Match match;
        CountDownLatch waitLatch;
        Server server;

        ClientThread(Socket s, int count, Server server){
            this.connection = s;
            this.count = count;
            this.server = server;
        }

        public void send(Message message) throws IOException {
            this.out.writeObject(message);
        }

        public void run(){
            account = null;
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            while(true) {
                try {
                    latch = new CountDownLatch(1);
                    Message data = (Message)in.readObject();
                    callback.accept(data);
                    System.out.println(account + "\n" + data.toString());
                    if(data.rematch != null) {
                        awaitingForRematch = true;
                        wantsRematch = data.rematch;
                    }
                    System.out.println(awaitingForRematch);
                    if(data.chatMsg == null) {
                        sleep(10);
                        latch.countDown();
                    }
                    MessageHandler.handleData(data, this, database, server);
                }
                catch(Exception e) {
                    try {
                        handleExit();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            } //

        }//end of run

        private void handleExit() throws InterruptedException {
            Match matchToRemove = null;
            if(matchmakingList.contains(this)) {
                matchmakingList.remove(this);
            }
            System.out.println("MM List: " + matchmakingList);
            if(match != null) {
                synchronized (match){
                for (int i = 0; i < match.players.size(); i++) {
                    if (Objects.equals(match.players.get(i).account.username, this.account.username)) {
                        try {
                            database.incrementLosses(match.players.get(i).account.username);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        Message msg = new Message();
                        msg.matchResult = "DC 1";
                        int j;
                        if (i == 0) j = 1;
                        else j = 0;
                        if (match != null) {
                            try {
                                match.players.get(j).send(msg);
                            } catch (IOException ex) { // do nothing bc that means opponent dc'd
                            }
                            try {
                                sleep(10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if (this.match != null) {
                                if (match.rematchLatch != null) {
                                    match.rematchLatch.countDown();
                                    try {
                                        sleep(10);
                                    } catch (InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                matchToRemove = match;
                            }
                            if (this.match != null) {
                                if (match.players.get(j) != null) {
                                    this.match.players.get(j).match = null;
                                }
                            }
                        }
                        try {
                            if (this.match != null) {
                                if (this.match.ongoing && match.players.size() == 2) {
                                    database.incrementWins(match.players.get(j).account.username);
                                }
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        this.match = null;
                        break;

                    }
                }
                if (matchToRemove != null && matches.contains(matchToRemove) && !matches.isEmpty()) {
                    matches.remove(matchToRemove);
                }
            }
            }
            callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
            clients.remove(this);
            System.out.println("Clients " + clients);
        }
    }//end of client thread
}






