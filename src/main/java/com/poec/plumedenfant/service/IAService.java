package com.poec.plumedenfant.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.poec.plumedenfant.exception.IAGenerationHistoireException;
import com.poec.plumedenfant.exception.IAGenerationImageException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class IAService {
	
	// URL de l'API d'OpenAI pour générer du texte (GPT-3.5 ou GPT-4)
    private final String url = "https://api.openai.com/v1/chat/completions";
    
    // URL de l'API d'OpenAI pour générer des images
    private final String urlImage = "https://api.openai.com/v1/images/generations";
	
	// Accès à la clef API qui se trouve dans application properties
    @Value("${openai.api.key}")
    private String apiKey;

    // Création du client OkHttp
    private final OkHttpClient client = new OkHttpClient().newBuilder()
    		.connectTimeout(60, TimeUnit.SECONDS)
    		.readTimeout(60, TimeUnit.SECONDS)
    		.writeTimeout(60, TimeUnit.SECONDS)
    		.build();
    
	public String faireRequete(String prompt) throws IAGenerationHistoireException { 
		
		// Création du corps de la requête JSON
	    JsonObject requestBody = new JsonObject();
		
	    requestBody.addProperty("model", "gpt-3.5-turbo-0125"); // Modèle GPT-3.5 (ou GPT-4)
	    
	    JsonArray messages = new JsonArray();

	    // Message "system" pour définir l'assistant
	    JsonObject message1 = new JsonObject();
	    message1.addProperty("role", "system");
	    message1.addProperty("content", "You are a helpful assistant.");

	    // Message "user" pour l'entrée de l'utilisateur
	    JsonObject message2 = new JsonObject();
	    message2.addProperty("role", "user");
	    message2.addProperty("content", prompt);

	    messages.add(message1);
	    messages.add(message2);

	    // Ajouter le tableau de messages dans la requête
	    requestBody.add("messages", messages);

	    // Convertir l'objet Json en chaîne
	    String json = requestBody.toString();

	    // Construire la requête HTTP POST
	    RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

	    Request request = new Request.Builder()
	            .url(url)
	            .post(body)
	            .addHeader("Authorization", "Bearer " + apiKey) // Authentification avec la clé API
	            .build();

	    // Exécuter la requête
	    try (Response response = client.newCall(request).execute()) {
	        if (response.isSuccessful()) {
	        	
	            // Recupération de la réponse sous forme de String
	            String responseString = response.body().string();
	            
	            // Conversion en JSON
	            JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
	            
	            // Récupération de la liste choices
	            JsonArray choices = jsonObject.getAsJsonArray("choices");
	            
	            // Extraction de la réponse
	            JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
	            String assistantResponse = message.get("content").getAsString();
	            System.out.println(assistantResponse);
	            return assistantResponse;
	        } else {
	            System.out.println("Request failed: " + response.code());
	            throw new IAGenerationHistoireException("Erreur lors de la génération d'histoire : La réponse de l'IA n'est pas reçue");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new IAGenerationHistoireException("Erreur lors de la génération d'histoire : La requête n'est pas envoyée à l'IA");
	    }
	}
	
	public InputStream creerImage(String prompt) throws IAGenerationImageException {
		
		// Création du corps de la requête JSON
	    JsonObject requestBody = new JsonObject();
	    
	    // Paramètres de la requete
	    requestBody.addProperty("model", "dall-e-3");
	    requestBody.addProperty("prompt", prompt);
	    requestBody.addProperty("n", 1);
	    requestBody.addProperty("size", "1024x1024");
	    
	    String json = requestBody.toString();
	    
	    RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
	    
	    Request request = new Request.Builder()
	    					.url(urlImage)
	    					.post(body)
	    					.addHeader("Content-Type", "application/json")
	    					.addHeader("Authorization", "Bearer " + apiKey)
	    					.build();
	    
	    System.out.println(prompt);
	    
	    // Execution de la requete
	    try (Response response = client.newCall(request).execute()) {
	    	String urlImageOpenAI;
	    	
	    	if(response.isSuccessful()) {
	    		
	    		System.out.println("Reponse récupérée avec succès");
	    		// Recupération de la réponse sous forme de String
	            String responseBody = response.body().string();
	            
	            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
	            JsonArray dataArray = responseJson.getAsJsonArray("data");
	            JsonObject imageData = dataArray.get(0).getAsJsonObject();
	            urlImageOpenAI = imageData.get("url").getAsString();
	            System.out.println(urlImageOpenAI);
	    	} else {
	    		System.err.println("Erreur lors de la génération d'image : " + response.code());
	    		System.out.println(response.body().string());
	    		throw new IAGenerationImageException("Erreur lors de la génération d'images : La réponse de l'IA n'est pas reçue");
	    	}
	    	
	    	response.close();
	    	return downloadImageFromURL(urlImageOpenAI);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IAGenerationImageException("Erreur lors de la génération d'images : La requête n'est pas envoyée à l'IA");
		}
		
	}
	
	// Telechargement de l'image depuis l'URL fourni par l'API de OpenAI
	private static InputStream downloadImageFromURL(String imageUrl) throws IOException {
		
		URL url = new URL(imageUrl);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		
		// Verifier la connexion http
		if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("Erreur lors du téléchargement de l'image");
		}
		
		return connection.getInputStream();
	
	}
	
	
	
	
	
}
