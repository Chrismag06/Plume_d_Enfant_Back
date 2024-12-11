package com.poec.plumedenfant.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.poec.plumedenfant.config.JwtTokenProvider;
import com.poec.plumedenfant.dao.RoleDao;
import com.poec.plumedenfant.dao.UtilisateurDao;
import com.poec.plumedenfant.dao.model.Role;
import com.poec.plumedenfant.dao.model.RoleEnum;
import com.poec.plumedenfant.dao.model.Utilisateur;
import com.poec.plumedenfant.dto.InscriptionDto;
import com.poec.plumedenfant.dto.LoginDto;
import com.poec.plumedenfant.exception.EmailAlreadyUsedException;

@SpringBootTest
public class AuthServiceTest {
	
	@InjectMocks
	private AuthService authService;

	@Mock
	private UtilisateurDao utilisateurDao;
	
	@Mock
	private RoleDao roleDao;
	
	@Mock
    private AuthenticationManager authenticationManager;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	
	private Utilisateur utilisateur;
	private Role role;
	private LoginDto loginDto;
	private InscriptionDto inscriptionDto;
	
	
	@BeforeEach
	public void setUp() {
		// Initialisation des objets
		MockitoAnnotations.openMocks(this);
		
		utilisateur = new Utilisateur();
		utilisateur.setEmail("test@example.com");
		utilisateur.setMdp("password");
		
		role = new Role();
		role.setName(RoleEnum.USER.toString());
		
		loginDto = new LoginDto();
		loginDto.setEmail("test@example.com");
		loginDto.setMdp("password");
		
		inscriptionDto = new InscriptionDto();
		inscriptionDto.setEmail("test@example.com");
		inscriptionDto.setMdp("password");
	}
	
	
	
	
	@Test
	public void testLogin() {
		// Simulation de l'authentification avec un token généré
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication))
			.thenReturn("mocked-jwt-token");
		
		String token = authService.login(loginDto);
		
		// Vérification du resultat
		assertEquals("mocked-jwt-token", token);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtTokenProvider).generateToken(authentication);
	}
	
	
	@Test
	public void testRegister() throws EmailAlreadyUsedException {
		// Simuler la recherche du role
		when(roleDao.findByName(RoleEnum.USER.toString()))
			.thenReturn(Optional.of(role));
		
		// Simuler la méthode save
		when(utilisateurDao.save(any(Utilisateur.class)))
			.thenReturn(utilisateur);
		
		// Simuler que l'email n'existe pas encore
		when(utilisateurDao.findByEmail(any(String.class)))
			.thenReturn(Optional.empty());
		
		// Appeler la méthode register
		Utilisateur savedUser = authService.register(inscriptionDto);
		
		// Verification du resultat
		assertNotNull(savedUser);
		assertEquals("test@example.com", savedUser.getEmail());
		
		verify(utilisateurDao, times(2)).save(any(Utilisateur.class));
		verify(roleDao).findByName(RoleEnum.USER.toString());
	}
	
	@Test
	public void testRegister_WhenEmailAlreadyUsed() throws EmailAlreadyUsedException {
		// Simuler la recherche du role
		when(roleDao.findByName(RoleEnum.USER.toString()))
			.thenReturn(Optional.of(role));
		
		// Simuler la méthode save
		when(utilisateurDao.save(any(Utilisateur.class)))
			.thenReturn(utilisateur);
		
		// Simuler un email existant
		when(utilisateurDao.findByEmail(any(String.class)))
		.thenReturn(Optional.of(utilisateur));
		
		// Verification du resultat
		assertThrows(EmailAlreadyUsedException.class, () -> {
			authService.register(inscriptionDto);
		});
		
	}
	
	
	
}
