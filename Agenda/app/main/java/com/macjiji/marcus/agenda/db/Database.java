package com.macjiji.marcus.agenda.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;

import com.macjiji.marcus.agenda.AjouterEvenement;
import com.macjiji.marcus.agenda.objets.Evenement;
import com.macjiji.marcus.agenda.MainActivity;

import java.util.ArrayList;

/**
 *
 * @author Marcus
 * @version 1.0
 * @see Evenement
 * @see MainActivity
 * @see AjouterEvenement
 *
 * Classe permettant de gérer la base de données de l'application
 *
 */

public class Database {

    private Database.DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    private static final String DATABASE_NAME = "agendajava.db"; // Nom de la base de données
    private static final int DATABASE_VERSION = 1; // Version de la base de données

    // Nom des différentes tables de la base de données
    private static final String TABLE_EVENEMENT = "evenement";

    // Nom des colonnes de la table EVENEMENT
    private static final String COL_EVENEMENT_ID="_id";
    private static final String COL_EVENEMENT_NOM="nom";
    private static final String COL_EVENEMENT_DESCRIPTION="description";
    private static final String COL_EVENEMENT_DATE="date";
    private static final String COL_EVENEMENT_JOUR="jour";
    private static final String COL_EVENEMENT_MOIS="mois";
    private static final String COL_EVENEMENT_ANNEE="annee";

    // Chaine "CREATE TABLE" pour la table Evenement
    private static final String CREATE_TABLE_EVENEMENT
            = "CREATE TABLE " + TABLE_EVENEMENT  + " ( "
                + COL_EVENEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " // ID de l'événement
                + COL_EVENEMENT_NOM + " TEXT NOT NULL, " // Nom de l'événement
                + COL_EVENEMENT_DESCRIPTION + " TEXT DEFAULT NULL, " // Description de l'événement
                + COL_EVENEMENT_DATE + " LONG NOT NULL, " // Date de l'événement
                + COL_EVENEMENT_JOUR + " INTEGER NOT NULL, " // Jour de l'événement
                + COL_EVENEMENT_MOIS + " INTEGER NOT NULL, " // Mois de l'événement
                + COL_EVENEMENT_ANNEE + " INTEGER NOT NULL);"; // Année de l'événement

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        // Méthode de création des tables
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_EVENEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENEMENT);
            onCreate(db);
        }

    }

    /**
     * Constructeur par défaut, permettant la création d'une instance de la base de données sur une activité.
     */
    public Database(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Méthode permettant l'ouverture de la base de données
     */
    public Database open() throws SQLException {
        mDbHelper = new Database.DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Méthode permettant la fermeture de la base de données
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Méthode permettant de récupérer un événement à partir d'un nom
     * @param name le nom del'événement
     * @return l'objet Evenement
     * @throws SQLException Exception si problème avec la base de données
     */
    public Evenement getEventWithName(final String name) throws SQLException{
        Cursor mCursor =
                mDb.query(true, TABLE_EVENEMENT, null, COL_EVENEMENT_NOM + "='" + name + "'", null,
                        null, null, null, null);
        if(mCursor != null && mCursor.moveToFirst()) {
            Evenement evenement = new Evenement();
            evenement.setId(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_ID)));
            evenement.setNom(mCursor.getString(mCursor.getColumnIndex(COL_EVENEMENT_NOM)));
            evenement.setDescription(mCursor.getString(mCursor.getColumnIndex(COL_EVENEMENT_DESCRIPTION)));
            evenement.setDate(mCursor.getLong(mCursor.getColumnIndex(COL_EVENEMENT_DATE)));
            evenement.setJour(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_JOUR)));
            evenement.setMois(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_MOIS)));
            evenement.setAnnee(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_ANNEE)));
            mCursor.close();
            return evenement;
        }else {
            return null;
        }
    }

    /**
     * Méthode permettant de récupérer tous les événements présents dans la base de données
     * @return Une liste des événements
     */
    public ArrayList<Evenement> getAllEvents(){
        ArrayList<Evenement> evenements = new ArrayList<>();
        Cursor mCursor =
                mDb.query(TABLE_EVENEMENT, null, null, null, null, null, null);
        if(mCursor != null && mCursor.moveToFirst()){
            for(int i=0; i<mCursor.getCount(); i++){
                Evenement evenement = new Evenement();
                evenement.setId(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_ID)));
                evenement.setNom(mCursor.getString(mCursor.getColumnIndex(COL_EVENEMENT_NOM)));
                evenement.setDescription(mCursor.getString(mCursor.getColumnIndex(COL_EVENEMENT_DESCRIPTION)));
                evenement.setDate(mCursor.getLong(mCursor.getColumnIndex(COL_EVENEMENT_DATE)));
                evenement.setJour(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_JOUR)));
                evenement.setMois(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_MOIS)));
                evenement.setAnnee(mCursor.getInt(mCursor.getColumnIndex(COL_EVENEMENT_ANNEE)));
                evenements.add(evenement);
                mCursor.moveToNext();
            }
            mCursor.close();
            return evenements;
        } else {
            return null;
        }
    }

    /**
     * Méthode permettant de supprimer un événement dans la base de données
     * @param id l'identifiant de l'événement
     * @throws SQLException Exception SQL
     */
    public void deleteEventWithId(final int id) throws SQLException {
        mDb.delete(TABLE_EVENEMENT, COL_EVENEMENT_ID + " = '" + id + "'", null);
    }

    /**
     * Méthode permettant de créer un événement dans la base de données
     * @param evenement l'événement à insérer en base de données
     * @return 0 si l'insertion n'a pas été effectué, valeur supérieur à 0 sinon
     * @throws SQLException Exception SQL
     */
    public long createEvent(Evenement evenement) throws SQLException{
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_EVENEMENT_NOM, evenement.getNom());
        initialValues.put(COL_EVENEMENT_DESCRIPTION, evenement.getDescription());
        initialValues.put(COL_EVENEMENT_DATE, evenement.getDate());
        initialValues.put(COL_EVENEMENT_JOUR, evenement.getJour());
        initialValues.put(COL_EVENEMENT_MOIS, evenement.getMois());
        initialValues.put(COL_EVENEMENT_ANNEE, evenement.getAnnee());
        return mDb.insert(TABLE_EVENEMENT, null, initialValues);
    }

    /**
     * Méthode de mise à jour d'un événement
     * @param evenement L'événement à mettre à jour
     * @throws SQLException Exception SQL
     */
    public void updateEvent(Evenement evenement) throws SQLException{
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EVENEMENT_NOM, evenement.getNom());
        contentValues.put(COL_EVENEMENT_DESCRIPTION, evenement.getDescription());
        contentValues.put(COL_EVENEMENT_DATE, evenement.getDate());
        contentValues.put(COL_EVENEMENT_JOUR, evenement.getJour());
        contentValues.put(COL_EVENEMENT_MOIS, evenement.getMois());
        contentValues.put(COL_EVENEMENT_ANNEE, evenement.getAnnee());
        mDb.update(TABLE_EVENEMENT, contentValues, COL_EVENEMENT_ID + "='" + evenement.getId() + "'", null);
    }


}
