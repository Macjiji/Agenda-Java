package com.macjiji.marcus.agenda;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.macjiji.marcus.agenda.db.Database;
import com.macjiji.marcus.agenda.objets.Evenement;

import java.util.Calendar;

/**
 *
 * @author Marcus
 * @version 1.0
 * @see Database
 * @see Evenement
 *
 * Classe permettant de définir l'activité d'ajout d'un événement
 *
 */

public class AjouterEvenement extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Database database;

    private String nomEvenement = "", descriptionEvenement = "";
    private int jour = 0, mois = 0, annee = 0;
    private long date = 0;

    protected EditText editNom, editDescription, editDate;
    protected ImageButton boutonCalendrier;
    protected Button validerEvenement;

    /**
     * Méthode onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_evenement);

        initialiseBaseDeDonnees(); // On initialise la base de données en premier...
        initialiseBoutons(); // ... Puis les boutons d'interactions...
        initialiseEditText(); // ... Enfin, les zone d'édition de texte
    }

    /**
     * Méthode héritée de DatePickerDialog, permettant de récupérer la date renseigné par l'utilisateur
     * @param view La vue du DatePicker
     * @param year L'année renseignée dans le DatePicker
     * @param monthOfYear Le mois renseigné dans le DatePicker
     * @param dayOfMonth Le jour du mois renseigné dans le DatePicker
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar calendrier = Calendar.getInstance();
        calendrier.set(Calendar.YEAR, year);
        calendrier.set(Calendar.MONTH, monthOfYear);
        calendrier.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        editDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        date = calendrier.getTimeInMillis() / 1000L;
        jour = dayOfMonth;
        mois = monthOfYear;
        annee = year;
    }


    /**
     * Méthode permettant d'initialiser la base de données
     */
    private void initialiseBaseDeDonnees(){
        database = new Database(this); // On crée l'instance de la base de données...
        database.open(); // ... Puis on ouvre la base de données
    }

    /**
     * Méthode permettant d'initialiser les boutons de l'activité
     */
    private void initialiseBoutons(){
        boutonCalendrier = (ImageButton)findViewById(R.id.buttonDate);
        validerEvenement = (Button)findViewById(R.id.buttonValider);

        boutonCalendrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afficherDatePicker();
            }
        });

        validerEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(evenementEstValide()){
                    Evenement evenement = new Evenement();
                    evenement.setNom(nomEvenement);
                    evenement.setDate(date);
                    evenement.setJour(jour);
                    evenement.setMois(mois);
                    evenement.setAnnee(annee);
                    if(!descriptionEvenement.trim().isEmpty()){ // On teste si la description a été renseignée
                        evenement.setDescription(descriptionEvenement);
                    }
                    Log.d("Evenement", "Valeurs : " + evenement.toString());
                    database.createEvent(evenement);
                    startActivity(new Intent(AjouterEvenement.this, MainActivity.class));
                }
            }
        });

    }

    /**
     * Méthode permettant d'initialiser les champs d'édition de l'activité
     */
    private void initialiseEditText(){
        editNom = (EditText)findViewById(R.id.champNom);
        editDescription = (EditText)findViewById(R.id.champDescription);
        editDate = (EditText)findViewById(R.id.champDate);

        editNom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nomEvenement = editNom.getText().toString();
            }
        });
        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                descriptionEvenement = editDescription.getText().toString();
            }
        });
    }

    /**
     * Méthode permettant d'afficher le fragment d'édition de date
     */
    private void afficherDatePicker(){
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    /**
     * Méthode permettant de vérifier si le formulaire est bien remplit
     *      -> On considérera que le champ de description est facultatif
     * @return True s'il est bien remplit, False sinon
     */
    private boolean evenementEstValide(){

        boolean verification = true; // On considére que la vérification est valide par défaut

        if(nomEvenement.equals("") || nomEvenement.trim().isEmpty()){ // On teste si le nom de l'événement est bien renseigné
            verification = false; // On met la valeur de vérification à False
            editNom.setError("Vous devez renseigner le nom de l'événement"); // Affichage de l'erreur sur le champ d'édition
        } else {
            editNom.setError(null); // On enlève l'erreur sur le champ d'édition si c'est bien renseigné
        }

        if(date < System.currentTimeMillis() / 1000L){ // On teste sir la date est supérieur au jour actuel
            verification = false; // On met la valeur de vérification à False
            editDate.setError("La date doit être supérieur à la date du jour"); // Affichage de l'erreur sur le champ d'édition
        } else {
            editDate.setError(null); // On enlève l'erreur sur le champ d'édition si c'est bien renseigné
        }

        return verification; // On retourne la valeur de vérification

    }

}
