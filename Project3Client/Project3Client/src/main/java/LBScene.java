import header.LBHeader;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class LBScene extends SceneBasic {
    public LBHeader header;
    public TableView<accountLB> leaderboard;
    public TextField myPosition;

    public LBScene(LinkedDS globalObjs) {
        this.globalObjs = globalObjs;
        header = new LBHeader("Leaderboard");
        myPosition = new TextField();
        myPosition.setEditable(false);
        myPosition.getStyleClass().add("miniHeader");
        root = new BorderPane();
        root.setTop(header.hbox);
        HBox hbox = new HBox(myPosition);
        innitLB();
        VBox vbox = new VBox(leaderboard);
        vbox.setAlignment(Pos.CENTER);
        root.setCenter(vbox);
        root.getStyleClass().add("borderpane");
        root.setBottom(hbox);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
    }

    @Override
    public void updateSceneImgs(boolean dark) {
        String path = "images/";
        if(dark) {path += "black/";}
        else {path += "white/";}
        header.leftButton.setGraphic(generateImageView((path + "back.png"), 40, 40));
        header.refresh.setGraphic(generateImageView((path + "refresh.png"), 40, 40));
    }

    private class accountLB extends Message.Account{
        int position;
        accountLB(Message.Account account) {
            this.wins = account.wins;
            this.username = account.username;
            this.losses = account.losses;
        }
    }

    private void innitLB() {
        leaderboard = new TableView<>();
        leaderboard.setEditable(false);
        leaderboard.maxWidthProperty().bind(root.widthProperty().multiply(0.7));
        TableColumn<accountLB, Integer> position = new TableColumn<>("#");
        position.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().position).asObject());
        TableColumn<accountLB, String> name = new TableColumn<>("Username");
        name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username));
        TableColumn<accountLB, Integer> wins = new TableColumn<>("Wins");
        wins.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().wins).asObject());
        TableColumn<accountLB, Integer> losses = new TableColumn<>("Losses");
        losses.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().losses).asObject());
        leaderboard.getColumns().addAll(position, name, wins, losses);
        for (TableColumn<?, ?> tableColumn : leaderboard.getColumns()) {
            tableColumn.setReorderable(false);
            tableColumn.prefWidthProperty().bind(leaderboard.widthProperty().divide(4));
        }
    }

    public void loadLeaderboard(ArrayList<Message.Account> leaderboardData) throws SQLException {
        leaderboard.getItems().clear();
        int i = 1;
        for (Message.Account account : leaderboardData) {
            accountLB accountLB = new accountLB(account);
            accountLB.position = i;
            leaderboard.getItems().add(accountLB);
            if(Objects.equals(account.username, globalObjs.account.username)) {
                myPosition.setText("Your position: #" + i);
            }
            i++;
        }
    }
}
