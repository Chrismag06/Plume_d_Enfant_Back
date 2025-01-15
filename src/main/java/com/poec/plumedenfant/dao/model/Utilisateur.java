package com.poec.plumedenfant.dao.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Utilisateur implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String email;
	
	@Column
	private String mdp;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="user_like",
				joinColumns = @JoinColumn(name="utilisateur_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name="histoire_id", referencedColumnName = "id"))
	private List<Histoire> listeLike;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="user_favori",
				joinColumns = @JoinColumn(name="utilisateur_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name="histoire_id", referencedColumnName = "id"))
	private List<Histoire> listeFavori;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="user_vues",
				joinColumns = @JoinColumn(name="utilisateur_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name="histoire_id", referencedColumnName = "id"))
	private List<Histoire> listeVue;
	
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "user_role", 
				joinColumns = @JoinColumn(name = "utilisateur_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
	)
	private Set<Role> roles = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}

	public List<Histoire> getListeLike() {
		return listeLike;
	}

	public void setListeLike(List<Histoire> listeLike) {
		this.listeLike = listeLike;
	}

	public List<Histoire> getListeFavori() {
		return listeFavori;
	}

	public void setListeFavori(List<Histoire> listeFavori) {
		this.listeFavori = listeFavori;
	}

	public List<Histoire> getListeVue() {
		return listeVue;
	}

	public void setListeVue(List<Histoire> listeVue) {
		this.listeVue = listeVue;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	
	
	
	
	
}
