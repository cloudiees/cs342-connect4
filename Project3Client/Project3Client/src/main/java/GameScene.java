import header.GameHeader;
import header.Header;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameScene extends SceneBasic {
    public GameHeader header;
    public GameBoard board;
    public ListView<String> chat;
    public TextField message;
    public Button send;

    public GameScene(LinkedDS globalObjs) {
        this.globalObjs = globalObjs;
        header = new GameHeader("Gaming");
        board = new GameBoard();
        root = new BorderPane();
        chat = new ListView<>();
        send = new Button();
        ImageView sendImg = new ImageView(new Image(getClass().getResourceAsStream("/images/white/send.png")));
        sendImg.setFitHeight(10);
        sendImg.setFitWidth(10);
        send.setGraphic(sendImg);
        message = new TextField();
        message.setPromptText("Enter your message");
        message.getStyleClass().add("inputTxt");
        chat.setEditable(false);
        chat.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...
                }else{
                    // set the width's
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());
                    // allow wrapping
                    setWrapText(true);
                    setText(item.toString());
                }
            }
        });
        chat.getStyleClass().add("chat");
        HBox sendBox = new HBox(message, send);
        sendBox.setAlignment(Pos.CENTER);
        VBox chatBox = new VBox(chat, sendBox);
        chatBox.setAlignment(Pos.CENTER);
        HBox hbox2 = new HBox(10, board.board, chatBox);
        HBox hbox = new HBox(generateHSpacer() ,hbox2, generateHSpacer());
        hbox.setAlignment(Pos.CENTER);
        root.setCenter(hbox);
        root.setTop(header.hbox);
        root.getStyleClass().add("borderpane");
        hbox.maxHeightProperty().bind(root.heightProperty().multiply(.75));
    }

    public void clearChatbox(){
        chat.getItems().clear();
        chat.getItems().add("Game Chat");
    }

    public void addMsg(String msg){
        chat.getItems().add(msg);
    }

    @Override
    public void updateSceneImgs(boolean dark) {
        String path = "images/";
        if(dark) {path += "black/";}
        else {path += "white/";}
        header.leftButton.setGraphic(generateImageView((path + "menu.png"), 40, 40));
        send.setGraphic(generateImageView((path + "send.png"), 10, 10));
    }
}
