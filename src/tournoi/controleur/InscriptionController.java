package tournoi.controleur;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import tournoi.modele.Joueur;
import tournoi.service.GestionnaireTournoi;
import tournoi.service.ResultatInscription;

/**
 * Contrôleur de l'interface d'inscription
 */
public class InscriptionController implements Initializable {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox equipeCheckBox;

    @FXML
    private CheckBox capitaineCheckBox;

    @FXML
    private Slider dureeSlider;

    @FXML
    private Label dureeLabel;

    @FXML
    private Button inscrireButton;

    @FXML
    private Label infoTournoiLabel;

    private GestionnaireTournoi gestionnaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gestionnaire = GestionnaireTournoi.getInstance();

        // Configuration de l'affichage de la durée
        dureeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int jours = newVal.intValue();
            dureeLabel.setText(jours + " jour" + (jours > 1 ? "s" : ""));
        });

        // Par défaut, si l'utilisateur n'a pas d'équipe, il ne peut pas être capitaine
        equipeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            capitaineCheckBox.setDisable(!newVal);
            if (!newVal) {
                capitaineCheckBox.setSelected(false);
            }
        });

        // Affichage des informations du tournoi
        updateTournoiInfo();
    }

    /**
     * Met à jour les informations affichées sur le tournoi
     */
    private void updateTournoiInfo() {
        infoTournoiLabel.setText(
                "Tournoi de Volley : " +
                        gestionnaire.getTournoi().getNombreEquipes() + "/" +
                        gestionnaire.getTournoi().getMaxEquipes() + " équipes"
        );
    }

    /**
     * Gère l'action du bouton d'inscription
     * @param event Événement du bouton
     */
    @FXML
    void handleInscription(ActionEvent event) {
        // Validation des champs
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || usernameField.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        // Création du joueur
        Joueur joueur = new Joueur(
                nomField.getText(),
                prenomField.getText(),
                usernameField.getText()
        );

        // Appel au service d'inscription
        ResultatInscription resultat = gestionnaire.inscrireJoueur(
                joueur,
                equipeCheckBox.isSelected(),
                capitaineCheckBox.isSelected(),
                (int) dureeSlider.getValue()
        );

        // Affichage du résultat
        if (resultat.isSucces()) {
            showAlert(AlertType.INFORMATION, "Succès", resultat.getMessage());
            clearFields();
        } else {
            showAlert(AlertType.WARNING, "Attention", resultat.getMessage());
        }

        // Mise à jour des informations du tournoi
        updateTournoiInfo();
    }

    /**
     * Affiche une boîte de dialogue
     * @param type Type d'alerte
     * @param titre Titre de l'alerte
     * @param message Message de l'alerte
     */
    private void showAlert(AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Réinitialise les champs du formulaire
     */
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        usernameField.clear();
        equipeCheckBox.setSelected(false);
        capitaineCheckBox.setSelected(false);
        dureeSlider.setValue(1);
    }
}