import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Message implements Serializable {
    public static class Account implements Serializable {
        public String username;
        public String password;
        public int wins;
        public int losses;
    }
    static final long serialVersionUID = 42L;
    public Account account;
    public String chatMsg;
    public String loginFeedback;
    public boolean lbRequest;
    public ArrayList<Account> lbData;
    public String mm;
    public String matchResult;
    public Boolean rematch;
    public Integer move;
    public Boolean myTurn;
    public boolean logout;
    public String toString(){
        String ret = "";
        ret += "ACCOUNT: ";
        if(account != null){
            ret += account.username;
            ret += " ";
            ret += account.password;
            ret += " ";
            ret += account.wins;
            ret += " ";
            ret += account.losses;
            ret += " ";
        } else{ ret = "NULL"; }
        ret += "\nchatMsg: " + chatMsg
                + "\nloginFeedback: " + loginFeedback
                + "\nlbRequest: " + lbRequest
                + "\nlbData: " + lbData;
        ret += "\nmm: " + mm;
        ret += "\nmatchResult: " + matchResult;
        ret += "\nrematch: " + rematch;
        ret += "\nmove: " + move;
        ret += "\nmyTurn: " + myTurn;
        return ret;
    }
}
