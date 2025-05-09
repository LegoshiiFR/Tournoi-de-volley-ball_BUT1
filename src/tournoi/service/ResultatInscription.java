package tournoi.service;

/**
 * Classe représentant le résultat d'une inscription
 */
public class ResultatInscription {
    private boolean succes;
    private String message;
    private TypeResultat typeResultat;

    public ResultatInscription(boolean succes, String message, TypeResultat typeResultat) {
        this.succes = succes;
        this.message = message;
        this.typeResultat = typeResultat;
    }

    public boolean isSucces() {
        return succes;
    }

    public String getMessage() {
        return message;
    }

    public TypeResultat getTypeResultat() {
        return typeResultat;
    }
}

/**
 * Énumération des types de résultats possibles
 */
enum TypeResultat {
    INVITATION_SPECTATEUR,  // Invitation à venir en tant que spectateur
    MESSAGE_ERREUR,         // Message d'erreur
    INSCRIPTION_EQUIPE,     // Inscription dans une équipe existante
    NOUVELLE_EQUIPE,        // Création d'une nouvelle équipe
    PAS_DE_PLACE            // Plus de place dans le tournoi
}