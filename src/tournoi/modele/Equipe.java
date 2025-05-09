package tournoi.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une équipe de volley
 */
public class Equipe {
    private String nom;
    private List<Joueur> joueurs;
    private Joueur capitaine;
    private int dureeInscription; // Durée d'inscription en jours

    public Equipe(String nom, Joueur capitaine, int dureeInscription) {
        this.nom = nom;
        this.joueurs = new ArrayList<>();
        this.capitaine = capitaine;
        this.dureeInscription = dureeInscription;

        // Ajouter automatiquement le capitaine à l'équipe
        this.joueurs.add(capitaine);
    }

    /**
     * Vérifie si l'équipe est complète (a assez de joueurs pour jouer)
     * @return true si l'équipe est complète
     */
    public boolean estComplete() {
        // Une équipe de volley nécessite au moins 6 joueurs
        return joueurs.size() >= 6;
    }

    /**
     * Ajoute un joueur à l'équipe
     * @param joueur Joueur à ajouter
     * @return true si le joueur a été ajouté avec succès
     */
    public boolean ajouterJoueur(Joueur joueur) {
        if (joueurs.contains(joueur)) {
            return false;
        }
        joueurs.add(joueur);
        return true;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public Joueur getCapitaine() {
        return capitaine;
    }

    public int getDureeInscription() {
        return dureeInscription;
    }

    public void setDureeInscription(int dureeInscription) {
        this.dureeInscription = dureeInscription;
    }

    @Override
    public String toString() {
        return nom + " (Capitaine: " + capitaine.getNom() + ")";
    }
}