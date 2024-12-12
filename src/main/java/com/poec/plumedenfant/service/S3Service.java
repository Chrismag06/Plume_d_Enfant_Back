package com.poec.plumedenfant.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	// Cette classe est le service pour le stockage d'images
	
	@Autowired
	private AmazonS3 amazonS3;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	// Telechargement de l'image en tableau de byte dans S3 et renvois de son url
	public String uploadImage(InputStream imageStream, String titreImage) {

		// Requête d'upload vers S3
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, titreImage, imageStream, null);
		
		// Telechargement du fichier sur S3
		amazonS3.putObject(putObjectRequest);
		System.out.println("Image telechargée dans S3 avec succès");
		
		// Retourner l'url publique du fichier
		return amazonS3.getUrl(bucketName, titreImage).toString();
	}
}
