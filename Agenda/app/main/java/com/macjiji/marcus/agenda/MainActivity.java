package com.macjiji.marcus.agenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macjiji.marcus.agenda.db.Database;
import com.macjiji.marcus.agenda.objets.Evenement;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 *
 * @author Marcus
 * @version 1.0
 * @see AjouterEvenement
 * @see ModifierEvenement
 * @see Database
 * @see Evenement
 *
 * Classe permettabt de gérer l'activité d'accueil
 *
 */

public class MainActivity extends AppCompatActivity {

    private Database database;
    private ArrayList<Evenement> listeEvenements = new ArrayList<>();

    protected CaldroidFragment caldroidFragment;
    protected FloatingActionButton ajouterEvement;
    protected ImageButton modifierEvenement, supprimerEvenement;
    protected LinearLayout apercuEvenement;
    protected TextView nomEvenement, dateEvement, descriptionEvenement;

    /**
     * Méthode onCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseBaseDeDonnees(); // On commence par initialiser la base de données...
        initialiseLayout(); // ... Puis le layout d'aperçu d'un évément...
        initialiseBoutons(); // ... Puis le bouton d'ajout d'un événement...
        initialiseCalendrier(); // ... Enfin, le calendrier
    }

    /**
     * Méthode permettant d'initialiser la base de données
     */
    private void initialiseBaseDeDonnees(){
        database = new Database(this); // On crée l'instance de la base de données...
        database.open(); // ... Puis on ouvre la base de données
    }

    /**
     * Méthode permettant d'initialiser le Layout d'aperçu d'un événement
     */
    private void initialiseLayout(){
        apercuEvenement = (LinearLayout)findViewById(R.id.apercuEvenement);
        nomEvenement = (TextView)findViewById(R.id.nomEvenement);
        dateEvement = (TextView)findViewById(R.id.dateEvenement);
        descriptionEvenement = (TextView)findViewById(R.id.descriptionEvenement);
        apercuEvenement.setVisibility(View.GONE);
    }

    /**
     * Méthode permettant d'initialiser les boutons sur la page d'accueil
     */
    private void initialiseBoutons(){
        ajouterEvement = (FloatingActionButton)findViewById(R.id.buttonAppuie);
        modifierEvenement = (ImageButton)findViewById(R.id.modifierEvenement);
        supprimerEvenement = (ImageButton)findViewById(R.id.supprimerEvenement);

        ajouterEvement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AjouterEvenement.class));
            }
        });

        modifierEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentModification = new Intent(MainActivity.this, ModifierEvenement.class);
                intentModification.putExtra("nom", nomEvenement.getText().toString());
                startActivity(intentModification);
            }
        });

        supprimerEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Suppression d'un événement") // Titre de la boite de dialogue
                        .setMessage("Voulez-vous réellement supprimer l'événement : " + nomEvenement.getText().toString()) // Contenu du message
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() { // Bouton de confirmation
                            public void onClick(DialogInterface dialog, int which) {

                                // Etape 0 : On crée l'événement qui sera supprimer, et on lui attribue la valeur null.
                                Evenement evenementASupprimer = null;

                                // Etape 1 : On parcours la liste des événements et on supprimer l'événement adéquat
                                for(Evenement evenement : listeEvenements){
                                    if(evenement.getNom().equals(nomEvenement.getText().toString())){
                                        database.deleteEventWithId(evenement.getId());
                                        evenementASupprimer = evenement;
                                    }
                                }

                                // Etape 2 : S'il existe bien un événement à supprimer, on rafraichit l'activité
                                if(evenementASupprimer != null){
                                    // Sous-étape 1 : On fait disparaitre l'aperçu de l'événement supprimé
                                    apercuEvenement.setVisibility(View.GONE);

                                    // Sous-étape 2 : On rafraichit le calendrier
                                    Calendar cal = Calendar.getInstance();
                                    cal.set(Calendar.DAY_OF_MONTH, evenementASupprimer.getJour());
                                    cal.set(Calendar.MONTH, evenementASupprimer.getMois());
                                    cal.set(Calendar.YEAR, evenementASupprimer.getAnnee());

                                    Date date = cal.getTime();

                                    ColorDrawable white = new ColorDrawable(Color.rgb(255, 255, 255));
                                    caldroidFragment.setBackgroundDrawableForDate(white, date);
                                    caldroidFragment.setTextColorForDate(R.color.caldroid_black, date);
                                    caldroidFragment.refreshView();

                                    // Sous-étape 3 : On elève de liste l'événement supprimer
                                    listeEvenements.remove(evenementASupprimer);
                                }
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener(){ // Bouton d'annulation
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show(); // On affiche la boite de dialogue
            }
        });
    }

    /**
     * Méthode permettant d'initialiser le calendrier
     */
    private void initialiseCalendrier(){

        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

        caldroidFragment = new CaldroidFragment();
        caldroidFragment.setArguments(args);

        listeEvenements = database.getAllEvents(); // On récupère tous les événements présents en base de données

        if(listeEvenements != null){ // On teste s'il existe des événements dans la base de données
            for(Evenement evenement : listeEvenements){ // Pour chaque événement présent dans la liste
                Log.d("Evenements", "Liste : " + evenement);

                // Etape 1 : On définit la date à partir de Calendar
                cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, evenement.getJour());
                cal.set(Calendar.MONTH, evenement.getMois());
                cal.set(Calendar.YEAR, evenement.getAnnee());

                // Etape 2 : On récupère sous forme de Date la valeur de Calendar
                Date date = cal.getTime();

                // Etape 3 : On crée la date dans le calendrier
                ColorDrawable blue = new ColorDrawable(Color.rgb(0, 119, 155));
                caldroidFragment.setBackgroundDrawableForDate(blue, date);
                caldroidFragment.setTextColorForDate(R.color.caldroid_white, date);
            }
        }

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                // Etape 1 : On créer un objet Calendar pour vérifier si la date existe en base de données à partir de la date sélectionnée
                Calendar calendrierVerification = Calendar.getInstance(Locale.getDefault());
                calendrierVerification.setTime(date);
                final int jour = calendrierVerification.get(Calendar.DAY_OF_MONTH);
                final int mois = calendrierVerification.get(Calendar.MONTH);
                final int annee = calendrierVerification.get(Calendar.YEAR);

                // Etape 2 : On crée un booléen qui va nous permettre d'afficher ou non l'aperçu d'un événement
                boolean existenceEvenement = false; // On considère qu'un événement n'existe pas par défaut

                // Etape 3 : On parcours la liste des événements puis on teste
                if(listeEvenements != null){
                    for(Evenement evenement : listeEvenements){
                        if(jour == evenement.getJour() && mois == evenement.getMois() && annee == evenement.getAnnee()){
                            nomEvenement.setText(evenement.getNom());
                            dateEvement.setText(evenement.getJour() + "/" + (evenement.getMois() + 1) + "/" + evenement.getAnnee());
                            if(evenement.getDescription() != null){
                                descriptionEvenement.setText(evenement.getDescription());
                            } else {
                                descriptionEvenement.setText("Pas de description");
                            }
                            existenceEvenement = true;
                        }
                    }

                    // Etape 4 : On finit par afficher ou non l'aperçu d'un événement s'il existe à une date précise
                    if(existenceEvenement){
                        apercuEvenement.setVisibility(View.VISIBLE);
                    } else {
                        apercuEvenement.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onChangeMonth(int month, int year) { }

            @Override
            public void onLongClickDate(Date date, View view) { }

            @Override
            public void onCaldroidViewCreated() { }

        };

        caldroidFragment.setCaldroidListener(listener);

    }

}
