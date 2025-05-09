package tournoi.modele;

/**
 * Classe représentant un joueur de volley
 */
public class Joueur {
    private String nom;
    private String prenom;
    private String nomUtilisateur;

    public Joueur(String nom, String prenom, String nomUtilisateur) {
        this.nom = nom;
        this.prenom = prenom;
        this.nomUtilisateur = nomUtilisateur;
    }

    /**
     * Vérifie si le nom d'utilisateur est valide (ne contient pas de chiffres)
     * @return true si le nom d'utilisateur est valide
     */
    public boolean estNomUtilisateurValide() {
        // Vérifier qu'il n'y a pas de chiffres dans le nom d'utilisateur
        return !nomUtilisateur.matches(".*\\d.*");
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + nomUtilisateur + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Joueur joueur = (Joueur) obj;
        return nomUtilisateur.equals(joueur.nomUtilisateur);
    }

    @Override
    public int hashCode() {
        return nomUtilisateur.hashCode();
    }
}