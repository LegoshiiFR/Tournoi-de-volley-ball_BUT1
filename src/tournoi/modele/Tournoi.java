package tournoi.modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un tournoi de volley
 */
public class Tournoi {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int maxEquipes;
    private List<Equipe> equipes;

    public Tournoi(LocalDate dateDebut, LocalDate dateFin, int maxEquipes) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.maxEquipes = maxEquipes;
        this.equipes = new ArrayList<>();
    }

    /**
     * Vérifie si une date est dans la période d'inscription du tournoi
     * @param date Date à vérifier
     * @return true si la date est dans la période d'inscription
     */
    public boolean estDansPeriodeInscription(LocalDate date) {
        return !date.isBefore(dateDebut) && !date.isAfter(dateFin);
    }

    /**
     * Vérifie si le tournoi a atteint le nombre maximum d'équipes
     * @return true si le tournoi est complet
     */
    public boolean estComplet() {
        return equipes.size() >= maxEquipes;
    }

    /**
     * Ajoute une équipe au tournoi
     * @param equipe Équipe à ajouter
     * @return true si l'équipe a été ajoutée avec succès
     */
    public boolean ajouterEquipe(Equipe equipe) {
        if (estComplet()) {
            return false;
        }
        equipes.add(equipe);
        return true;
    }

    /**
     * Renvoie le nombre d'équipes inscrites au tournoi
     * @return Nombre d'équipes
     */
    public int getNombreEquipes() {
        return equipes.size();
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public int getMaxEquipes() {
        return maxEquipes;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }
}