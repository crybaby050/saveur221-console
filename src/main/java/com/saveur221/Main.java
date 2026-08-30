package com.saveur221;

import com.saveur221.config.Container;
import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.ChoixAccueil;
import com.saveur221.views.AccueilView;
import com.saveur221.views.LoginView;
import com.saveur221.views.Router;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Container container = new Container();

        AccueilView accueilView = new AccueilView(scanner);
        ChoixAccueil choix = accueilView.demander();

        if (choix == ChoixAccueil.QUITTER) {
            System.out.println("Au revoir !");
            return;
        }

        LoginView loginView = new LoginView(container.getAuthService(), scanner);
        Utilisateur utilisateurConnecte = loginView.demarrerConnexion();

        Router router = new Router(container, utilisateurConnecte, scanner);
        router.demarrer();

        System.out.println("Au revoir !");
    }
}