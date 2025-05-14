package tournoi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import tournoi.modele.Equipe;
import tournoi.modele.Joueur;
import tournoi.modele.Tournoi;

/**
 * Classe responsable de la gestion du tournoi
 * Implémente le singleton pattern pour avoir une instance unique
 */
public class GestionnaireTournoi {
    private static GestionnaireTournoi instance;

    private Tournoi tournoi;
    private List<Equipe> equipesEnAttente;

    private GestionnaireTournoi() {
        // Initialisation du tournoi avec des dates et nombre max d'équipes par défaut
        tournoi = new Tournoi(
                LocalDate.now().minusDays(7),  // Date de début il y a une semaine
                LocalDate.now().plusDays(7),   // Date de fin dans une semaine
                8                             // Maximum de 8 équipes
        );
        equipesEnAttente = new ArrayList<>();
    }

    /**
     * Récupère l'instance unique du gestionnaire
     * @return Instance du gestionnaire
     */
    public static synchronized GestionnaireTournoi getInstance() {
        if (instance == null) {
            instance = new GestionnaireTournoi();
        }
        return instance;
    }

    /**
     * Inscrit un joueur au tournoi selon le logigramme
     * @param joueur Joueur à inscrire
     * @param aUneEquipe Si le joueur a déjà une équipe
     * @param capitaine Si le joueur est capitaine
     * @param dureeInscription Durée d'inscription en jours
     * @return ResultatInscription contenant le résultat et un message
     */
    public ResultatInscription inscrireJoueur(Joueur joueur, boolean aUneEquipe, boolean capitaine, int dureeInscription) {
        // Vérification de la période d'inscription
        if (!tournoi.estDansPeriodeInscription(LocalDate.now())) {
            return new ResultatInscription(false, "Vous ne pouvez pas vous inscrire en dehors de la période d'inscription", TypeResultat.INVITATION_SPECTATEUR);
        }

        // Vérification du nom d'utilisateur
        if (!joueur.estNomUtilisateurValide()) {
            return new ResultatInscription(false, "Le nom d'utilisateur ne doit pas contenir de chiffres", TypeResultat.MESSAGE_ERREUR);
        }

        // Vérification du nombre maximum d'équipes
        if (tournoi.estComplet()) {
            return new ResultatInscription(false, "Le tournoi a atteint le nombre maximum d'équipes", TypeResultat.PAS_DE_PLACE);
        }

        // Le joueur a-t-il une équipe ?
        if (aUneEquipe) {
            if (capitaine) {
                // Création d'une nouvelle équipe avec le joueur comme capitaine
                Equipe nouvelleEquipe = new Equipe("Équipe de " + joueur.getNom(), joueur, dureeInscription);
                boolean ajoutReussi = tournoi.ajouterEquipe(nouvelleEquipe);

                if (ajoutReussi) {
                    return new ResultatInscription(true, "Votre équipe a été inscrite pour " + dureeInscription + " jours", TypeResultat.INSCRIPTION_EQUIPE);
                } else {
                    return new ResultatInscription(false, "Erreur lors de l'ajout de l'équipe", TypeResultat.MESSAGE_ERREUR);
                }
            } else {
                // Rejoindre une équipe existante - à implémenter dans le contrôleur
                return new ResultatInscription(false, "Veuillez sélectionner une équipe à rejoindre", TypeResultat.MESSAGE_ERREUR);
            }
        } else {
            // Le joueur n'a pas d'équipe, on cherche une équipe disponible
            boolean equipeDisponible = false;
            for (Equipe equipe : tournoi.getEquipes()) {
                if (!equipe.estComplete()) {
                    equipeDisponible = true;
                    equipe.ajouterJoueur(joueur);
                    return new ResultatInscription(true, "Vous êtes inscrit dans l'équipe " + equipe.getNom() + " pour " + equipe.getDureeInscription() + " jours", TypeResultat.INSCRIPTION_EQUIPE);
                }
            }

            if (!equipeDisponible) {
                // Pas d'équipe disponible, création d'une nouvelle équipe avec le joueur comme capitaine
                Equipe nouvelleEquipe = new Equipe("Équipe de " + joueur.getNom(), joueur, dureeInscription);

                // CORRECTION: On ajoute toujours l'équipe, sans vérifier la parité du nombre d'équipes
                boolean ajoutReussi = tournoi.ajouterEquipe(nouvelleEquipe);

                if (ajoutReussi) {
                    return new ResultatInscription(true, "Aucune équipe disponible, une nouvelle équipe a été créée avec vous comme capitaine", TypeResultat.NOUVELLE_EQUIPE);
                } else {
                    return new ResultatInscription(false, "Erreur lors de la création d'une nouvelle équipe", TypeResultat.MESSAGE_ERREUR);
                }
            }
        }

        // Ne devrait jamais arriver ici
        return new ResultatInscription(false, "Une erreur est survenue lors de l'inscription", TypeResultat.MESSAGE_ERREUR);
    }

    /**
     * Vérifie et traite les équipes en attente
     * @return true si des équipes ont été traitées
     */
    public boolean traiterEquipesEnAttente() {
        if (equipesEnAttente.isEmpty()) {
            return false;
        }

        // Si on a deux équipes en attente, on peut les ajouter car elles formeront un nombre pair
        if (equipesEnAttente.size() >= 2) {
            for (int i = 0; i < 2; i++) {
                Equipe equipe = equipesEnAttente.remove(0);
                tournoi.ajouterEquipe(equipe);
            }
            return true;
        }

        return false;
    }

    public Tournoi getTournoi() {
        return tournoi;
    }

    public void setTournoi(Tournoi tournoi) {
        this.tournoi = tournoi;
    }

    public List<Equipe> getEquipesEnAttente() {
        return equipesEnAttente;
    }
}