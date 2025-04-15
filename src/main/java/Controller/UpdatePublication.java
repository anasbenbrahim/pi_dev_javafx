package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modele.Publication;
import services.ServicePublication;

public class UpdatePublication {

    @FXML
    private DatePicker dateupdate;

    @FXML
    private TextArea descriptionupdate;

    @FXML
    private TextField imageupdate;

    @FXML
    private TextField titreupdate;

    private Publication publication;
    private final ServicePublication service = new ServicePublication();

    // This method is called externally after FXML is loaded
    public void setPublication(Publication publication) {
        this.publication = publication;

        if (publication != null && titreupdate != null) {
            titreupdate.setText(publication.getTitre());
            descriptionupdate.setText(publication.getDescription());
            imageupdate.setText(publication.getImageUrl());
            dateupdate.setValue(publication.getDate());
        }
    }

    @FXML
    public void updatePublication() {
        if (isValidInput()) {
            publication.setTitre(titreupdate.getText());
            publication.setDescription(descriptionupdate.getText());
            publication.setImageUrl(imageupdate.getText());
            publication.setDate(dateupdate.getValue());

            service.update(publication);
            closeWindow();
        } else {
            // Optionally show an error message here
        }
    }

    @FXML
    public void cancelUpdate() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titreupdate.getScene().getWindow();
        stage.close();
    }

    private boolean isValidInput() {
        return !titreupdate.getText().isEmpty() &&
                !descriptionupdate.getText().isEmpty() &&
                !imageupdate.getText().isEmpty() &&
                dateupdate.getValue() != null;
    }
}
