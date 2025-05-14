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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

    @FXML
    private RadioButton sansEquipeRadio;

    @FXML
    private RadioButton rejoindreEquipeRadio;

    @FXML
    private RadioButton creerEquipeRadio;

    @FXML
    private ToggleGroup optionEquipe;

    @FXML
    private TextField nomEquipeField;

    private GestionnaireTournoi gestionnaire;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gestionnaire = GestionnaireTournoi.getInstance();

        // Configuration des options d'équipe avec les boutons radio
        sansEquipeRadio.setSelected(true);

        // Ajout des écouteurs pour les boutons radio
        sansEquipeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                equipeComboBox.setVisible(false);
                nomEquipeField.setVisible(false);
            }
        });

        rejoindreEquipeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                equipeComboBox.setVisible(true);
                nomEquipeField.setVisible(false);
                updateEquipeComboBox();
            }
        });

        creerEquipeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                equipeComboBox.setVisible(false);
                nomEquipeField.setVisible(true);

                // Vérifier si la limite d'équipes est atteinte
                if (gestionnaire.getTournoi().getNombreEquipes() >= gestionnaire.getTournoi().getMaxEquipes()) {
                    showAlert(AlertType.WARNING, "Limite atteinte",
                            "Le nombre maximum d'équipes (" + gestionnaire.getTournoi().getMaxEquipes() + ") est atteint.");
                    sansEquipeRadio.setSelected(true);
                }
            }
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

        // Désactiver l'option de création d'équipe si le maximum est atteint
        if (gestionnaire.getTournoi().getNombreEquipes() >= gestionnaire.getTournoi().getMaxEquipes()) {
            creerEquipeRadio.setDisable(true);
        } else {
            creerEquipeRadio.setDisable(false);
        }
    }

    /**
     * Met à jour la liste déroulante des équipes disponibles
     */
    private void updateEquipeComboBox() {
        ObservableList<Equipe> equipes = FXCollections.observableArrayList(gestionnaire.getTournoi().getEquipes());
        equipeComboBox.setItems(equipes);

        // Si aucune équipe n'est disponible, informer l'utilisateur
        if (equipes.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Information", "Aucune équipe n'est disponible à rejoindre.");
            rejoindreEquipeRadio.setDisable(true);
        } else {
            rejoindreEquipeRadio.setDisable(false);
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

        // Si l'option "Créer une équipe" est sélectionnée mais aucun nom d'équipe n'est fourni
        if (creerEquipeRadio.isSelected() && nomEquipeField.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez saisir un nom pour votre équipe");
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

        // Traitement selon l'option sélectionnée
        ResultatInscription resultat;

        if (sansEquipeRadio.isSelected()) {
            // Inscription sans équipe
            resultat = gestionnaire.inscrireJoueur(joueur, false, false, dureeInscription);
        } else if (rejoindreEquipeRadio.isSelected()) {
            // Vérifier si une équipe est sélectionnée
            if (equipeComboBox.getSelectionModel().isEmpty()) {
                showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner une équipe à rejoindre");
                return;
            }

            // Rejoindre l'équipe sélectionnée
            Equipe equipeSelectionnee = equipeComboBox.getSelectionModel().getSelectedItem();
            equipeSelectionnee.ajouterJoueur(joueur);

            resultat = new ResultatInscription(true, "Vous avez rejoint l'équipe " + equipeSelectionnee.getNom(), null);
        } else if (creerEquipeRadio.isSelected()) {
            // Vérifier si le tournoi a atteint le nombre maximum d'équipes
            if (gestionnaire.getTournoi().getNombreEquipes() >= gestionnaire.getTournoi().getMaxEquipes()) {
                showAlert(AlertType.ERROR, "Erreur", "Le nombre maximum d'équipes est atteint");
                return;
            }

            // Créer une nouvelle équipe avec un nom personnalisé
            Equipe nouvelleEquipe = new Equipe(nomEquipeField.getText(), joueur, dureeInscription);
            gestionnaire.getTournoi().ajouterEquipe(nouvelleEquipe);

            resultat = new ResultatInscription(true, "Votre équipe \"" + nomEquipeField.getText() + "\" a été créée avec vous comme capitaine", null);
        } else {
            // Ne devrait jamais arriver ici
            return;
        }

        // Affichage du résultat
        if (resultat != null) {
            if (resultat.isSucces()) {
                showAlert(AlertType.INFORMATION, "Succès", resultat.getMessage());
                clearFields();
            } else {
                showAlert(AlertType.WARNING, "Attention", resultat.getMessage());
            }
        }

        // Mise à jour des informations et des listes
        updateTournoiInfo();
        updateEquipesListView();
        updateEquipeComboBox();
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
        sansEquipeRadio.setSelected(true);
        equipeComboBox.getSelectionModel().clearSelection();
        equipeComboBox.setVisible(false);
        nomEquipeField.clear();
        nomEquipeField.setVisible(false);
    }
}