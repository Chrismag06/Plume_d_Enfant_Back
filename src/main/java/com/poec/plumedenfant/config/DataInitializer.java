package com.poec.plumedenfant.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.poec.plumedenfant.dao.RoleDao;
import com.poec.plumedenfant.dao.UtilisateurDao;
import com.poec.plumedenfant.dao.model.Role;
import com.poec.plumedenfant.dao.model.RoleEnum;
import com.poec.plumedenfant.dao.model.Utilisateur;

@Component
public class DataInitializer implements CommandLineRunner {
	
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UtilisateurDao utilisateurDao;
	
	@Value("${profil.user.email}")
	private String userEmail;
	
	@Value("${profil.user.password}")
	private String userPassword;
	
	@Value("${profil.admin.email}")
	private String adminEmail;
	
	@Value("${profil.admin.password}")
	private String adminPassword;
	
	@Override
	public void run(String... args) throws Exception {
		
		// Get all existing role from RoleEnum
		for(RoleEnum roleEnum: RoleEnum.values()) {
			// Check si le role existe
			if(roleDao.findByName(roleEnum.toString()).isEmpty()) {
				// Créer et save un nouveau role s'il n'existe pas
				Role newRole = new Role();
				newRole.setName(roleEnum.toString());
				roleDao.save(newRole);
			}
		}
		
		// Créer l'admin s'il n'existe pas
		if(utilisateurDao.findByEmail(adminEmail).isEmpty()) {
			Utilisateur admin = new Utilisateur();
			admin.setEmail(adminEmail);
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(adminPassword);
			admin.setMdp(encodedPassword);
			admin = utilisateurDao.save(admin);
			Role adminRole = roleDao.findByName(RoleEnum.ADMIN.toString()).get();
			admin.setRoles(Set.of(adminRole));
			admin = utilisateurDao.save(admin);
		}
		
		// Créer un utilisateur par défaut s'il n'existe pas
		if(utilisateurDao.findByEmail(userEmail).isEmpty()) {
			Utilisateur user = new Utilisateur();
			user.setEmail(userEmail);
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(userPassword);
			user.setMdp(encodedPassword);
			user = utilisateurDao.save(user);
			Role userRole = roleDao.findByName(RoleEnum.USER.toString()).get();
			user.setRoles(Set.of(userRole));
			user = utilisateurDao.save(user);
			
		}
	}

}
