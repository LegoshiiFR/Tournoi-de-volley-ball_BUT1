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

        // Le joueur a-t-il une équipe ?
        if (aUneEquipe) {
            // Son équipe est-elle remplie ?
            Equipe nouvelleEquipe = new Equipe("Équipe de " + joueur.getNom(), joueur, dureeInscription);

            if (capitaine) {
                // Vérification du nombre d'équipes
                if (tournoi.getNombreEquipes() < tournoi.getMaxEquipes()) {
                    tournoi.ajouterEquipe(nouvelleEquipe);
                    return new ResultatInscription(true, "Votre équipe a été inscrite pour " + dureeInscription + " jours", TypeResultat.INSCRIPTION_EQUIPE);
                } else {
                    return new ResultatInscription(false, "Il n'y a plus de place dans le tournoi", TypeResultat.PAS_DE_PLACE);
                }
            } else {
                // Création d'une nouvelle équipe avec le joueur comme membre
                if (tournoi.getNombreEquipes() < tournoi.getMaxEquipes()) {
                    return new ResultatInscription(true, "Une nouvelle équipe est créée avec vous comme joueur", TypeResultat.NOUVELLE_EQUIPE);
                } else {
                    return new ResultatInscription(false, "Il n'y a plus de place dans le tournoi", TypeResultat.PAS_DE_PLACE);
                }
            }
        } else {
            // Une équipe a de la place ?
            boolean equipeDisponible = false;
            for (Equipe equipe : tournoi.getEquipes()) {
                if (!equipe.estComplete()) {
                    equipeDisponible = true;
                    equipe.ajouterJoueur(joueur);
                    return new ResultatInscription(true, "Vous êtes inscrit dans l'équipe " + equipe.getNom() + " pour " + equipe.getDureeInscription() + " jours", TypeResultat.INSCRIPTION_EQUIPE);
                }
            }

            if (!equipeDisponible) {
                // Création d'une nouvelle équipe avec le joueur comme membre
                if (tournoi.getNombreEquipes() < tournoi.getMaxEquipes()) {
                    return new ResultatInscription(true, "Une nouvelle équipe est créée avec vous comme joueur", TypeResultat.NOUVELLE_EQUIPE);
                } else {
                    return new ResultatInscription(false, "Il n'y a plus de place dans le tournoi", TypeResultat.PAS_DE_PLACE);
                }
            }
        }

        // Ne devrait jamais arriver ici
        return new ResultatInscription(false, "Une erreur est survenue lors de l'inscription", TypeResultat.MESSAGE_ERREUR);
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