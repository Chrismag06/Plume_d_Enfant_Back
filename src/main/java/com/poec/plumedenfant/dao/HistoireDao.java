package com.poec.plumedenfant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.poec.plumedenfant.dao.model.Histoire;

public interface HistoireDao extends CrudRepository<Histoire, Integer> {
	
	@Modifying
	@Transactional
	@Query("UPDATE Histoire h set h.corps = :corps WHERE h.id =:idHistoire")
	public void updateCorps(int idHistoire, String corps);
	
	@Modifying
	@Transactional
	@Query("UPDATE Histoire h set h.nbLike = (SELECT COUNT(u) FROM Utilisateur u JOIN u.listeLike h WHERE h.id = :idHistoire) WHERE h.id =:idHistoire")
	public void updateNbLike(int idHistoire);
	
	@Modifying
	@Transactional
	@Query("UPDATE Histoire h set h.titre = :titre WHERE h.id =:idHistoire")
	public void updateTitre(int idHistoire, String titre);
	
	@Modifying
	@Transactional
	@Query("UPDATE Histoire h set h.urlImage = :urlImage WHERE h.id =:idHistoire")
	public void updateUrlImage(int idHistoire, String urlImage);
	
	@Query("SELECT h FROM Histoire h ORDER BY h.id")
	public List<Histoire> findAllHistoiresSortedById();
	
	@Query("SELECT h FROM Histoire h ORDER BY h.nbLike DESC")
	public List<Histoire> findAllHistoiresSortedByLike();
	
	@Query("SELECT h FROM Histoire h WHERE h.createur.id =:idUtilisateur")
	public List<Histoire> getVosHistoiresCrees(int idUtilisateur);

}
