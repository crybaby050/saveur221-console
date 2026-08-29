package com.saveur221.config;

import com.saveur221.repositories.*;
import com.saveur221.services.*;

public class Container {

    private final CategorieRepository categorieRepository = new CategorieRepository();
    private final ProduitRepository produitRepository = new ProduitRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepository();
    private final CommandeRepository commandeRepository = new CommandeRepository();
    private final LigneCommandeRepository ligneCommandeRepository = new LigneCommandeRepository();
    private final PaiementRepository paiementRepository = new PaiementRepository();
    private final FactureRepository factureRepository = new FactureRepository();
    private final RecuRepository recuRepository = new RecuRepository();

    private final AuthService authService = new AuthService(utilisateurRepository);
    private final CategorieService categorieService = new CategorieService(categorieRepository, produitRepository);
    private final ProduitService produitService = new ProduitService(produitRepository);
    private final UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository);
    private final ClientService clientService = new ClientService(clientRepository);
    private final RecuService recuService = new RecuService(recuRepository);
    private final FactureService factureService = new FactureService(factureRepository);
    private final PaiementService paiementService =
            new PaiementService(paiementRepository, commandeRepository, recuService);
    private final CommandeService commandeService = new CommandeService(
            commandeRepository, ligneCommandeRepository, produitRepository, produitService, factureService);

    public AuthService getAuthService() { return authService; }
    public CategorieService getCategorieService() { return categorieService; }
    public ProduitService getProduitService() { return produitService; }
    public UtilisateurService getUtilisateurService() { return utilisateurService; }
    public ClientService getClientService() { return clientService; }
    public RecuService getRecuService() { return recuService; }
    public FactureService getFactureService() { return factureService; }
    public PaiementService getPaiementService() { return paiementService; }
    public CommandeService getCommandeService() { return commandeService; }
}