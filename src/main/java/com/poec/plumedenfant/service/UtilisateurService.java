package com.poec.plumedenfant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poec.plumedenfant.dao.UtilisateurDao;
import com.poec.plumedenfant.dao.model.Utilisateur;

@Service
public class UtilisateurService {

	@Autowired
	private UtilisateurDao utilisateurDao;
	
	// Création d'un utilisateur
	/*
	public void insertUtilisateur(Utilisateur utilisateur) {
		utilisateur.setId(null);
		utilisateurDao.save(utilisateur);
	}
	*/
	
	// Récupération d'un utilisateur
	public Optional<Utilisateur> getUtilisateurById(int idUtilisateur) {
		return utilisateurDao.findById(idUtilisateur);
	}
	
	public Optional<Utilisateur> getUtilisateurByUsername(String email) {
		return utilisateurDao.findByEmail(email);
	}
	
	// Récupération de la liste des utilisateurs
	public List<Utilisateur> getAllUtilisateur() {
		return (List<Utilisateur>) utilisateurDao.findAll();
	}
	
	// Modification d'un utilisateur
	public void updateUtilisateur(Utilisateur utilisateur, int idUtilisateur) {
		if(utilisateurDao.findById(idUtilisateur) != null) {
			if(utilisateur.getEmail() != null) {
				utilisateurDao.updateEmail(idUtilisateur, utilisateur.getEmail());
			}
			if(utilisateur.getMdp() != null) {
				utilisateurDao.updateMdp(idUtilisateur, utilisateur.getMdp());
			}
			if(utilisateur.getListeLike() != null) {
				// utilisateurDao.updateListLike(idUtilisateur, utilisateur.getListeLike());
				if(utilisateurDao.findById(idUtilisateur) != null) {
					Utilisateur user = utilisateurDao.findById(idUtilisateur).get();
					user.setListeLike(utilisateur.getListeLike());
					user.setId(idUtilisateur);
					utilisateurDao.save(user);
				}
			}
			if(utilisateur.getListeFavori() != null) {
				// utilisateurDao.updateListFavori(idUtilisateur, utilisateur.getListeFavori());
				if(utilisateurDao.findById(idUtilisateur) != null) {
					Utilisateur user = utilisateurDao.findById(idUtilisateur).get();
					user.setListeFavori(utilisateur.getListeFavori());
					user.setId(idUtilisateur);
					utilisateurDao.save(user);
				}
			}
			if(utilisateur.getListeVue() != null) {
				// utilisateurDao.updateListVue(idUtilisateur, utilisateur.getListeVue());
				if(utilisateurDao.findById(idUtilisateur) != null) {
					Utilisateur user = utilisateurDao.findById(idUtilisateur).get();
					user.setListeVue(utilisateur.getListeVue());
					user.setId(idUtilisateur);
					utilisateurDao.save(user);
				}
			}
		} else {
			System.out.println("Update impossible : L'utilisateur n'est pas reconnu");
		}
		
	}
	
	// Suppression d'un utilisateur
	public void deleteUtilisateurById(int idUtilisateur) {
		utilisateurDao.deleteById(idUtilisateur);
	}
	
	
	
	
}
