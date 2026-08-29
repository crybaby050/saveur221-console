package com.saveur221;

import com.saveur221.config.Container;
import com.saveur221.entities.Utilisateur;
import com.saveur221.views.LoginView;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Container container = new Container();

        LoginView loginView = new LoginView(container.getAuthService(), scanner);
        Utilisateur utilisateurConnecte = loginView.demarrerConnexion();

        System.out.println("Menu principal à venir pour le rôle : " + utilisateurConnecte.getRole());
    }
}