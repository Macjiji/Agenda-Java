package com.macjiji.marcus.agenda.objets;

import com.macjiji.marcus.agenda.MainActivity;
import com.macjiji.marcus.agenda.db.Database;

/**
 *
 * @author Marcus
 * @version 1.0
 * @see Database
 * @see MainActivity
 *
 * Classe permettant de définir un événement
 *
 */

public class Evenement {

    private int id;
    private String nom;
    private String description;
    private long date;
    private int jour;
    private int mois;
    private int annee;

    /**
     * Constructeur par défaut
     */
    public Evenement(){ }

    /**
     * Constructeur prenant en paramètre tous les attributs d'un événement
     * @param nom Le nom de l'événement
     * @param description La description de l'événement
     * @param date La date de l'événement
     * @param jour Le jour de l'événement
     * @param mois Le mois de l'événement
     * @param annee L'année de l'événement
     */
    public Evenement(int id, String nom, String description, long date, int jour, int mois, int annee){
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.jour = jour;
        this.mois = mois;
        this.annee = annee;
    }

    // GETTERS
    public int getId(){ return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public long getDate() { return date; }
    public int getJour() { return jour; }
    public int getMois() { return mois; }
    public int getAnnee() { return annee; }

    // SETTERS
    public void setId(int id){ this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(long date) { this.date = date; }
    public void setJour(int jour) { this.jour = jour; }
    public void setMois(int mois) { this.mois = mois; }
    public void setAnnee(int annee) { this.annee = annee; }

    /**
     * Méthode toString() permettant de renvoyer l'ensemble des informations d'un objet Evenement
     * @return les informations d'un événement
     */
    @Override
    public String toString() {
        return "Evenement{" +
                "nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", jour=" + jour +
                ", mois=" + mois +
                ", annee=" + annee +
                '}';
    }
}
