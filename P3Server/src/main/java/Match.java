import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class Match {
    ArrayList<Server.ClientThread> players;
    ArrayList<Character> symbols;
    public Connect4 game;
    Server.ClientThread currentPlayer;
    CountDownLatch rematchLatch;
    boolean ongoing;

    public void voteRematch(Server.ClientThread self, Server.ClientThread otherClientThread, CountDownLatch sharedLatch, ArrayList<Match> matches) throws InterruptedException {
        System.out.println("Waiting for self vote as " + self.account.username);
        self.latch.await();
        System.out.println("Self vote received as " + self.account.username);
        rematchLatch.countDown();
        if(!self.wantsRematch) {
            System.out.println("I dont want a rematch " + self.account.username);
            otherClientThread.latch.countDown();
            sleep(1);
            otherClientThread.latch = new CountDownLatch(1);
            rematchLatch.countDown();
        }
        System.out.println("Waiting for other client vote as " + self.account.username);
        rematchLatch.await();
        System.out.println("Other client vote received as " + self.account.username);
        Message msg1 = new Message();
        Message msg2 = new Message();
        System.out.println("I am " + self.account.username + " and the shared latch is " + sharedLatch.getCount());
        if(sharedLatch.getCount() == 1) {
            System.out.println("Shared Latch count is 1, so I wait as " + self.account.username);
            sharedLatch.await();
        }
        else{
            System.out.println("Counting down sharedLatch as " + self.account.username);
            sharedLatch.countDown();
        }
        if(rematchLatch == null) {
            System.out.println("Rematch Latch is null");
            return;
        }
        System.out.println("I AM " + self.account.username + " I DO " + self.wantsRematch + " AND MY OPPONENT " + otherClientThread.account.username + " DOES " + self.wantsRematch + " AND THE REMATCH LATCH IS " + rematchLatch.getCount());
        if(otherClientThread.wantsRematch && self.wantsRematch && rematchLatch != null){
            System.out.println("Rematching " + otherClientThread.account.username);
            rematchLatch = null;
            msg1.rematch = true;
            msg2.rematch = true;
            self.wantsRematch = false;
            msg1.myTurn = true;
            msg2.myTurn = false;
            self.match.currentPlayer = self;
            otherClientThread.wantsRematch = false;
            System.out.println("Other msg: " +msg2.toString());
            System.out.println("My msg: " +msg1.toString());
            self.match.game.resetBoard();
            try {
                self.send(msg1);
                otherClientThread.send(msg2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.ongoing = true;
        }
        else {
            assert rematchLatch != null;
            if (rematchLatch.getCount() <= 1) {
                if(self.match != null){
                    System.out.println("no ur rematch");
                    msg1.rematch = false;
                    msg2.rematch = false;
                    self.wantsRematch = false;
                    otherClientThread.wantsRematch = false;
                    System.out.println("Other msg: " +msg2.toString());
                    System.out.println("My msg: " +msg1.toString());
                    try{
                        self.send(msg1);
                        otherClientThread.send(msg2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                matches.remove(self.match);
                otherClientThread.match = null;
                self.match = null;
            }
        }
        sharedLatch.countDown();
    }

    public void renewLatch() {
        rematchLatch = new CountDownLatch(2);
    }
    Match(){
        players = new ArrayList<>(2);
        game = new Connect4();
        symbols = new ArrayList<>(2);
        symbols.add('0');
        symbols.add('1');
    }

    public void setCurrentPlayer(Server.ClientThread currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
