package com.poec.plumedenfant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poec.plumedenfant.dao.HistoireDao;
import com.poec.plumedenfant.dao.model.CategorieAge;
import com.poec.plumedenfant.dao.model.CategorieHistoire;
import com.poec.plumedenfant.dao.model.FormulaireHistoire;
import com.poec.plumedenfant.dao.model.Histoire;
import com.poec.plumedenfant.exception.IAGenerationHistoireException;
import com.poec.plumedenfant.exception.IAGenerationImageException;
import com.poec.plumedenfant.exception.IdUtilisateurIntrouvableException;


@Service
public class HistoireService {

	@Autowired
	private HistoireDao histoireDao;
	
	@Autowired
	private IAService iaService;
	
	@Autowired
	private UtilisateurService utilisateurService;
	
	// Création de la requete pour le corps de l'histoire
	public String creationRequete(FormulaireHistoire formulaire) {
		return "Ecris moi une histoire, en français, sur le thème " + formulaire.getCategorieHistoire().getValeur() +
				" pour un enfant de " + formulaire.getCategorieAge().getValeur() +
				". Le personnage principal s'appelle " + formulaire.getNomPersoPrincipal() + ". " + 
				formulaire.getDetailPersoPrincipal() +
				formulaire.getPhraseListePersoSecondaire() + 
				formulaire.getPhraseDetailsSupplementaires() + 
				". Donne un titre à cette histoire sur la première ligne sans utiliser de décoration de texte, l'histoire doit faire environ 600 mots, aére bien le texte en faisant des paragraphes, n'écris pas FIN à la fin, ne met pas de commentaires je ne veux voir que l'histoire "
				;
		}
	
	// Creation de la requete pour l'image de l'histoire
	public String creationRequeteImage(String histoire) {
		return "Génère moi une image pour illustrer l'histoire suivante sans afficher de texte sur l'image : " + histoire;
	}
	
	
	// Creation de l'histoire
	public void insertHistoire(String request, int idCreateur, CategorieHistoire categorieHistoire, CategorieAge categorieAge) 
			throws IdUtilisateurIntrouvableException, IAGenerationHistoireException, IAGenerationImageException {
		Histoire histoire = new Histoire();
		histoire.setId(null);
		histoire.setCategorieHistoire(categorieHistoire);
		histoire.setCategorieAge(categorieAge);
		
		// Vérification que l'id du créateur est trouvable
		if(utilisateurService.getUtilisateurById(idCreateur).isEmpty()) {
			throw new IdUtilisateurIntrouvableException("Utilisateur introuvable");
		} else {
			utilisateurService.getUtilisateurById(idCreateur).ifPresent(utilisateur -> {
				histoire.setCreateur(utilisateur);
			});
		}
		
		System.out.println(request);
		String contenuHistoire = iaService.faireRequete(request);
		
		// Vérification des erreurs lors de la génération d'histoire
		if(contenuHistoire.equals("error")) {
			throw new IAGenerationHistoireException("Erreur lors de la génération d'histoire");
		}
		
		// Extraction du titre pour le mettre dans l'attribut titre
		int finTitre = contenuHistoire.indexOf("\n");
		String titre = contenuHistoire.substring(0, finTitre);
		titre = titre.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "").trim();
		contenuHistoire = contenuHistoire.substring(finTitre + 2); 
		histoire.setTitre(titre);
		
		// On met le corps de l'histoire sans le titre
		if(!contenuHistoire.equals("error")) {
			histoire.setCorps(contenuHistoire);
		}
		
		// Récupération de l'image en B64Json
		String imageB64Json = "data:image/png;base64," + iaService.creerImage(creationRequeteImage(contenuHistoire));
		
		// Vérification des erreurs lors de la génération d'image
		if(imageB64Json.equals("data:image/png;base64,error")) {
			throw new IAGenerationImageException("Erreur lors de la génération d'image");
		}
		if(!imageB64Json.equals("error")) {
			histoire.setImageB64Json(imageB64Json);
		}
		
		// Initialisation du nombre de like
		histoire.setNbLike(0);
		
		histoireDao.save(histoire);
	}
	
	// Récupération d'une histoire
	public Optional<Histoire> getHistoireById(int idHistoire) {
		return histoireDao.findById(idHistoire);
	}
	
	// Récupération de la liste d'histoire
	public List<Histoire> getAllHistoireSortedByLike() {
		return (List<Histoire>) histoireDao.findAllHistoiresSortedByLike();
	}
	
	// Récupération de la dernière histoire
	public Optional<Histoire> getLastHistoire() {
		List<Histoire> liste = (List<Histoire>) histoireDao.findAll();
		Histoire lastHistoire = liste.get(liste.size() -1);
		return Optional.of(lastHistoire);
	}
	
	// Récupération de la liste d'histoire créées par l'utilisateur connecté
	public List<Histoire> getVosHistoiresCrees(int idUtilisateur) {
		return histoireDao.getVosHistoiresCrees(idUtilisateur);
	}
	
	// Modification d'une histoire
	public void updateHistoire(Histoire histoire, int idHistoire) {
		if(histoireDao.findById(idHistoire) != null) {
			// update du corps de l'histoire
			if(histoire.getCorps() != null) {
				histoireDao.updateCorps(idHistoire, histoire.getCorps());
			}
			// update du nombre de like de l'histoire
			if(histoire.getNbLike() != null) {
				histoireDao.updateNbLike(idHistoire, histoire.getNbLike());
			}
			// update du titre de l'histoire
			if(histoire.getTitre() != null) {
				histoireDao.updateTitre(idHistoire, histoire.getTitre());
			}
			// update de l'image de l'histoire
			if(histoire.getImageB64Json() != null) {
				histoireDao.updateUrlImage(idHistoire, histoire.getImageB64Json());
			}
		} else {
			System.out.println("Update impossible : L'histoire n'est pas reconnue");
		}
	}
	
	// Suppression d'une histoire
	public void deleteHistoireById(int idHistoire) {
		if(histoireDao.findById(idHistoire) != null) {
			histoireDao.deleteById(idHistoire);
		} else {
			System.out.println("Suppression impossible : L'id de l'histoire n'est pas reconnu");
		}
	}
	
}
