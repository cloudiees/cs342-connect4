import header.Header;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MMScene extends SceneBasic {
    public Header header;
    public TextField status;
    public StatusEllipses statusEllipses;

    public MMScene(LinkedDS globalObjs) {
        this.globalObjs = globalObjs;
        header = new Header("Matchmaking");
        status = new TextField();
        status.setEditable(false);
        status.getStyleClass().add("header");
        root = new BorderPane();
        root.setTop(header.hbox);
        VBox vbox = new VBox(status, generateRectangleSpacer(1,40));
        root.setCenter(vbox);
        vbox.setAlignment(Pos.CENTER);
        root.getStyleClass().add("borderpane");
    }

    public void startStatusEllipses() {
        statusEllipses = new StatusEllipses();
        statusEllipses.start();
    }

    public void interruptStatusEllipses() {
        statusEllipses.interrupt();
        statusEllipses = null;
    }
    public void killStatusEllipses() {
        statusEllipses.stop();
        statusEllipses = null;
    }

    @Override
    public void updateSceneImgs(boolean dark) {
        String path = "images/";
        if(dark) {path += "black/";}
        else {path += "white/";}
        header.leftButton.setGraphic(generateImageView((path + "back.png"), 40, 40));
    }

    private class StatusEllipses extends Thread {
        public StatusEllipses(){
            super();
        }
        @Override
        public void run() {
            int i = 0;
            while (!this.isInterrupted()) {
                StringBuilder s = new StringBuilder("Finding match");
                for (int j = 0; j < i; j++) {
                    s.append(".");
                }
                Platform.runLater(() -> status.setText(s.toString()));
                i++;
                if (i > 3) {
                    i = 0;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
