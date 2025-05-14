package tournoi.controleur;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tournoi.modele.Equipe;
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
    private ComboBox<Equipe> equipeComboBox;

    @FXML
    private Button inscrireButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Label infoTournoiLabel;

    @FXML
    private ListView<Equipe> equipesListView;

    @FXML
    private ListView<Joueur> joueursListView;

    private GestionnaireTournoi gestionnaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gestionnaire = GestionnaireTournoi.getInstance();

        // Configuration de l'affichage/masquage du ComboBox selon la sélection du CheckBox
        equipeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            equipeComboBox.setVisible(newVal);
            updateEquipeComboBox();
        });

        // Configuration de la sélection d'équipe dans la ListView pour afficher ses joueurs
        equipesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldEquipe, newEquipe) -> {
            if (newEquipe != null) {
                updateJoueursListView(newEquipe);
            } else {
                joueursListView.getItems().clear();
            }
        });

        // Affichage initial des informations du tournoi et des équipes
        updateTournoiInfo();
        updateEquipesListView();
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
     * Met à jour la liste déroulante des équipes disponibles
     */
    private void updateEquipeComboBox() {
        if (equipeCheckBox.isSelected()) {
            ObservableList<Equipe> equipes = FXCollections.observableArrayList(gestionnaire.getTournoi().getEquipes());
            equipeComboBox.setItems(equipes);
        }
    }

    /**
     * Met à jour la liste des équipes dans la ListView
     */
    private void updateEquipesListView() {
        ObservableList<Equipe> equipes = FXCollections.observableArrayList(gestionnaire.getTournoi().getEquipes());
        equipesListView.setItems(equipes);
    }

    /**
     * Met à jour la liste des joueurs d'une équipe sélectionnée
     * @param equipe L'équipe dont on veut afficher les joueurs
     */
    private void updateJoueursListView(Equipe equipe) {
        ObservableList<Joueur> joueurs = FXCollections.observableArrayList(equipe.getJoueurs());
        joueursListView.setItems(joueurs);
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

        // Valeur par défaut pour la durée d'inscription (5 jours)
        int dureeInscription = 5;

        // Vérifier si le joueur a sélectionné une équipe existante
        boolean aUneEquipe = equipeCheckBox.isSelected();
        boolean estCapitaine = false;

        // Si le joueur a une équipe mais n'a pas sélectionné d'équipe existante,
        // on considère qu'il veut créer une nouvelle équipe dont il sera capitaine
        if (aUneEquipe && equipeComboBox.getSelectionModel().isEmpty()) {
            estCapitaine = true;
        }

        // Appel au service d'inscription
        ResultatInscription resultat = gestionnaire.inscrireJoueur(
                joueur,
                aUneEquipe,
                estCapitaine,
                dureeInscription
        );

        // Affichage du résultat
        if (resultat.isSucces()) {
            showAlert(AlertType.INFORMATION, "Succès", resultat.getMessage());
            clearFields();
        } else {
            showAlert(AlertType.WARNING, "Attention", resultat.getMessage());
        }

        // Mise à jour des informations et des listes
        updateTournoiInfo();
        updateEquipesListView();
    }

    /**
     * Gère l'action du bouton de rafraîchissement
     * @param event Événement du bouton
     */
    @FXML
    void handleRefresh(ActionEvent event) {
        updateTournoiInfo();
        updateEquipesListView();
        updateEquipeComboBox();

        // Effacer la sélection actuelle de joueurs
        joueursListView.getItems().clear();
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
        equipeComboBox.getSelectionModel().clearSelection();
        equipeComboBox.setVisible(false);
    }
}