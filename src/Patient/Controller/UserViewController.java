package Patient.Controller;

import Patient.Block;
import Patient.journalMakerClasses.JournalGenerator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;
import supportClasses.AES;
import supportClasses.RSA;

import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewController implements Initializable
{
    @FXML
    private AnchorPane pane;

    @FXML
    private JFXListView<Label> jfxViewList;

    @FXML
    private Label nameOrCPR;

    @FXML
    private JFXButton makeJournal;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String publicKeyAsString;
    private List<Block> blockList;

    private int clickedJournal;
    private boolean aJournalHasBeenClicked = false;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
    }

    @FXML
    public void getJournalButton(ActionEvent event)
    {
        try{
            JournalGenerator journalGenerator = new JournalGenerator();
            AES AES = new AES();
            RSA RSA = new RSA();
            if(aJournalHasBeenClicked) {
                /* Decrypts AES-key */
                byte[] returnedEncryptedAESKey = Base64.decodeBase64(blockList.get(getClickedJournal()).encryptedAESKey);
                String decryptedAESKey = RSA.decrypt(returnedEncryptedAESKey, privateKey);

                String retrievedJournal = AES.decrypt(Base64.decodeBase64(blockList.get(getClickedJournal()).encryptedData), decryptedAESKey);
                System.out.println(retrievedJournal);
                /* Splits lines separated by semicolon */
                List<String> splitLinesList = Arrays.asList(retrievedJournal.split(":"));
                /* The name of the saved file is the 10 first characters of the encrypted journal. */
                String saveFileName = blockList.get(getClickedJournal()).encryptedData.substring(0, 10);
                /* Inserts all the variables in JournalGenerator */
                journalGenerator.makeJournal(saveFileName, splitLinesList.get(0), splitLinesList.get(1), splitLinesList.get(2), splitLinesList.get(3),
                        splitLinesList.get(4), splitLinesList.get(5), splitLinesList.get(6), splitLinesList.get(7),
                        splitLinesList.get(8), splitLinesList.get(9), splitLinesList.get(10), splitLinesList.get(11),
                        splitLinesList.get(12), splitLinesList.get(13), splitLinesList.get(14));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void handleMouseClick(MouseEvent mouseEvent)
    {
        /* If any element that is not white space (-1) is clicked save the index of that element in clickedJournal */
        if(jfxViewList.getSelectionModel().getSelectedIndex() != -1) {
            setClickedJournal(jfxViewList.getSelectionModel().getSelectedIndex());
            setaJournalHasBeenClicked(true);
            System.out.println("clicked on " + jfxViewList.getSelectionModel().getSelectedIndex());
        }
    }

    public void passBlockList(List<Block> blockListParam)
    {
        // A blockchain goes from oldest to newest, therefore reverse the jfxViewList to get the latest journal at the top
        Collections.reverse(blockListParam);

        for (Block block : blockListParam)
            jfxViewList.getItems().add(new Label(block.encryptedData));

        blockList = blockListParam;
    }

    public void passPrivateKey(PrivateKey privateKey)
    {
        this.privateKey = privateKey;
    }

    public void passPublicKeyAsString(String publicKeyAsString)
    {
        this.publicKeyAsString = publicKeyAsString;
    }

    public void passPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public int getClickedJournal() {
        return clickedJournal;
    }

    public void setClickedJournal(int clickedJournal) {
        this.clickedJournal = clickedJournal;
    }

    public void setaJournalHasBeenClicked(boolean aJournalHasBeenClicked) {
        this.aJournalHasBeenClicked = aJournalHasBeenClicked;
    }
}

