import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

public class MessageHandler {
    private static void handleAccount(Message data, Server.ClientThread self, Server server, Database database) {
        Message toSend = null;
        try {
            toSend = loginAttempt(data.account.username, data.account.password, database, server);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (toSend.account != null) {
            self.account = toSend.account;
        }
        try {
            self.send(toSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleFF(Server.ClientThread self, Server server, Database database) {
        System.out.println("Forfeit found");
        Server.ClientThread otherClientThread = null;
        if (self.match.players.get(0).account.username.equals(self.account.username)) {
            otherClientThread = self.match.players.get(1);
        } else {
            otherClientThread = self.match.players.get(0);
        }
        try {
            database.incrementWins(otherClientThread.account.username);
            database.incrementLosses(self.account.username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Message selfMsg = new Message();
        selfMsg.matchResult = "0";
        try {
            System.out.println("sending message to self " + self.account.username);
            self.send(selfMsg);
            Message other = new Message();
            other.matchResult = "1";
            otherClientThread.send(other);
            System.out.println("sending message to other client " + otherClientThread.account.username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            sleep(10);
            CountDownLatch godForbidThisWorks = new CountDownLatch(2);
            Server.ClientThread finalOtherClientThread = otherClientThread;
            Thread otherThread = new Thread(() -> {
                try {
                    self.match.voteRematch(finalOtherClientThread, self, godForbidThisWorks, server.matches);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            self.match.renewLatch();
            otherThread.start();
            self.match.voteRematch(self, otherClientThread, godForbidThisWorks, server.matches);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void handleMove(Message data, Server.ClientThread self, Server server, Database database) {
        int playerNum = self.match.players.get(0) == self ? 0 : 1;
        boolean won = self.match.game.playMove(data.move, self.match.symbols.get(playerNum));
        self.match.currentPlayer = self.match.players.get(playerNum == 0 ? 1 : 0);
        Message msg = new Message();
        msg.move = data.move;
        if (won || self.match.game.moves >= 42) {
            Message selfMsg = new Message();
            if (won) {
                self.match.ongoing = false;
                msg.matchResult = "0";
                selfMsg.matchResult = "1";
                try {
                    database.incrementWins(self.account.username);
                    database.incrementLosses(self.match.currentPlayer.account.username);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                msg.matchResult = "-1";
                selfMsg.matchResult = "-1";
            }
            try {
                self.match.currentPlayer.send(msg);
                self.send(selfMsg);
                CountDownLatch sharedLatch = new CountDownLatch(2);
                self.match.renewLatch();
                Thread threadA = new Thread(() -> {
                    try {
                        self.match.voteRematch(self.match.currentPlayer, self, sharedLatch, server.matches);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                threadA.start();
                self.match.voteRematch(self, self.match.currentPlayer, sharedLatch, server.matches);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            msg.myTurn = true;
            try {
                self.match.currentPlayer.send(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


    }

    public static void handleData(Message data, Server.ClientThread self, Database database, Server server) {
        Thread thread = new Thread(() -> {
            System.out.println("Starting handling thread...");
            if (data.account != null) {
                handleAccount(data, self, server, database);
            } else if (data.lbRequest) {
                try {
                    self.send(getLBData(database));
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (data.mm != null) {
                if (Objects.equals(data.mm, "Find Match")) {
                    server.matchmakingList.add(self);
                    self.matchMakingThread = new FindMatch(self, server);
                    self.matchMakingThread.start();
                } else if (Objects.equals(data.mm, "Cancel Match Making")) {
                    self.matchMakingThread.interrupt();
                }
            } else if (data.matchResult != null) {
                if (data.matchResult.equals("FF")) {
                    handleFF(self, server, database);
                }
            } else if (data.move != null) {
                if (self.match != null) {
                    handleMove(data, self, server, database);
                }
            } else if (data.chatMsg != null) {
                Server.ClientThread opponentClientThread = self.match.players.get(0) == self ? self.match.players.get(1) : self.match.players.get(0);
                Message msg = new Message();
                msg.chatMsg = data.chatMsg;
                try {
                    opponentClientThread.send(msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (data.logout) {
                self.account = null;
            }
            System.out.println("Finished handling thread...");
        });
        thread.start();
    }

    private static Message getLBData(Database database) throws SQLException {
        Message message = new Message();
        message.lbData = database.getLeaderboard();
        return message;
    }

    public static class FindMatch extends Thread {
        Server.ClientThread c;
        Server s;
        FindMatch(Server.ClientThread c, Server s) {
            this.c = c;
            this.s = s;
        }
        public void run() {
            System.out.println("Find matching thread started for account " + this.c.account.username);
            while (!this.isInterrupted()) {
                for (Match match : s.matches) {
                    for (Server.ClientThread player : match.players) {
                        if (player.account.username.equals(this.c.account.username)) {
                            s.matchmakingList.remove(this.c);
                            System.out.println("Match: " + match + " Opp: " + (match.players.get(0).account.username.equals(this.c.account.username) ? match.players.get(1).account.username : match.players.get(0).account.username));
                            System.out.println("MM List FM: " + s.matchmakingList);
                            Message msg = new Message();
                            msg.mm = "Match Found";
                            msg.myTurn = false;
                            try {
                                c.send(msg);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            c.match = match;
                            match.ongoing = true;
                            return;
                        }
                    }
                }
                synchronized (s.matchmakingList) {
                    if (s.matchmakingList.size() >= 2) {
                        System.out.println("Match found by searching");
                        Match m = new Match();
                        m.players.add(this.c);
                        s.matchmakingList.remove(this.c);
                        m.players.add(s.matchmakingList.get(0));
                        s.matchmakingList.remove(s.matchmakingList.get(0));
                        s.matches.add(m);
                        m.setCurrentPlayer(this.c);
                        Message msg = new Message();
                        msg.mm = "Match Found";
                        msg.myTurn = true;
                        try {
                            this.c.send(msg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        c.match = m;
                        return;
                    }
                }
            }
            Message msg = new Message();
            msg.mm = "Match Making Canceled";
            try {
                this.c.send(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            s.matchmakingList.remove(this.c);
        }

    }

    private static Message loginAttempt(String username, String password, Database database, Server server) throws SQLException {
        Message message = new Message();
        try {
            database.insertUser(username, password);
            message.loginFeedback = "Successfully registered";
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                if (!database.validatePassword(username, password)) {
                    message.loginFeedback = "ERR: Invalid password";
                    return message;
                } else {
                    for (Server.ClientThread client : server.clients) {
                        if (client.account != null) {
                            if (client.account.username.equals(username)) {
                                message.loginFeedback = "ERR: Account is in use";
                                return message;
                            }
                        }
                    }
                    message.loginFeedback = "Login successful";
                }
            }
        }
        message.account = database.getAccount(username);
        return message;
    }

}
