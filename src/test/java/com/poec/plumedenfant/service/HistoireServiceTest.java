package com.poec.plumedenfant.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.poec.plumedenfant.dao.HistoireDao;
import com.poec.plumedenfant.dao.model.CategorieAge;
import com.poec.plumedenfant.dao.model.CategorieHistoire;
import com.poec.plumedenfant.dao.model.Histoire;
import com.poec.plumedenfant.dao.model.Utilisateur;
import com.poec.plumedenfant.exception.IAGenerationHistoireException;
import com.poec.plumedenfant.exception.IAGenerationImageException;
import com.poec.plumedenfant.exception.IdUtilisateurIntrouvableException;

@SpringBootTest
public class HistoireServiceTest {
	
	@InjectMocks
	private HistoireService histoireService;
	
	@Mock
	private UtilisateurService utilisateurService;
	
	@Mock
	private IAService iaService;
	
	@Mock
	private S3Service s3Service;
	
	@Mock
	private HistoireDao histoireDao;
	
	private String request;
	private int idCreateur;
	private CategorieHistoire categorieHistoire;
	private CategorieAge categorieAge;
	private Utilisateur utilisateur;
	
	@BeforeEach
	public void setUp() {
		// Initialisation des mocks
		MockitoAnnotations.openMocks(this);
		
		request = "Crée moi une belle histoire";
		categorieHistoire = CategorieHistoire.FANTASTIQUE;
		categorieAge = CategorieAge.DEUX_TROIS_ANS;
		idCreateur = 1;
		
		utilisateur = new Utilisateur();
		utilisateur.setId(idCreateur);
		utilisateur.setEmail("exemple@email.fr");
		utilisateur.setMdp("password");
	}
	
	@Test
	public void insertHistoireTest() 
			throws IdUtilisateurIntrouvableException, IAGenerationHistoireException, IAGenerationImageException {
		// Simulation de la récupération de l'utilisateur
		when(utilisateurService.getUtilisateurById(any(Integer.class)))
			.thenReturn(Optional.of(utilisateur));
		
		// Simuler la réponse de iaService pour la création d'histoire
		when(iaService.faireRequete(any(String.class)))
			.thenReturn("***Titre de l'histoire***\n C'est un beau roman, c'est une belle histoire");
		
		// Simuler la réponse de iaService pour la creation d'image
		when(iaService.creerImage(any(String.class)))
			.thenReturn(mock(InputStream.class));
		
		// Simuler la réponse de S3Service pour le stockage d'image
		when(s3Service.uploadImage(any(InputStream.class), any(String.class)))
			.thenReturn("urlImage");
		
		// Appel de la méthode insert
		histoireService.insertHistoire(request, idCreateur, categorieHistoire, categorieAge);
		
		// Verifications que ces opérations ne se font qu'une seule fois ou deux selon ce qu'on veut
		verify(utilisateurService, times(2)).getUtilisateurById(idCreateur);
		verify(iaService).faireRequete(request);
		verify(iaService).creerImage(any(String.class));
		verify(histoireDao).save(any(Histoire.class));
		
		// Vérification que le titre est bien défini et bien nettoyé
		String titreAttendu = "Titre de l'histoire";
		ArgumentCaptor<Histoire> captor = ArgumentCaptor.forClass(Histoire.class);
		verify(histoireDao).save(captor.capture());
		assertEquals(titreAttendu, captor.getValue().getTitre());
	}
	
	@Test
	public void insertHistoireTest_WhereUtilisateurUnknown() 
			throws IdUtilisateurIntrouvableException, IAGenerationImageException, IAGenerationHistoireException {
		// Simulation de utilisateur non trouvé
		when(utilisateurService.getUtilisateurById(any(Integer.class)))
			.thenReturn(Optional.empty());
		
		// Simuler la réponse de iaService pour la création d'histoire
		when(iaService.faireRequete(any(String.class)))
			.thenReturn("***Titre de l'histoire***\n C'est une belle histoire nul");
		
		// Simuler la réponse de iaService pour la creation d'image
		when(iaService.creerImage(any(String.class)))
			.thenReturn(mock(InputStream.class));
		
		// Simuler la réponse de S3Service pour le stockage d'image
		when(s3Service.uploadImage(any(InputStream.class), any(String.class)))
			.thenReturn("urlImage");
		
		// Vérifications
		verify(histoireDao, never()).save(any(Histoire.class));
		assertThrows(IdUtilisateurIntrouvableException.class, () -> {
			histoireService.insertHistoire(request, idCreateur, categorieHistoire, categorieAge);
		});
	}

}
