package com.poec.tts;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.*;

public class GoogleTextToSpeechService implements TextToSpeechService {

	@Override
	public void  convertTextToSpeech(String text, LanguageCode languageCode, VoiceGender voiceGender, TtsAudioEncoding ttsAudioEncoding, String outputFile)  throws Exception {
		
		// TODO Auto-generated method stub
		
        // Charger les credentials depuis `resources`
        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("projet-tts-450215-354b164845db.json");

        if (credentialsStream == null) {
            throw new RuntimeException("Fichier de credentials non trouvé dans resources !");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(TextToSpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build())) {
		
            // Configuration de la synthèse
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Choix de la voix française
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(getLanguageCode(languageCode) ) 
                    .setSsmlGender(getSsmlGender(voiceGender)) 
                    .build();

            // Configuration audio
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(getAudioEncoding(ttsAudioEncoding))
                    .build();
            
            // Synthèse vocale
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            
            ByteString audioContents = response.getAudioContent();
            try (OutputStream out = new FileOutputStream(outputFile)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio enregistré dans : " + outputFile);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	
    private SsmlVoiceGender getSsmlGender(VoiceGender gender) {
        switch (gender) {
            case MALE:
                return SsmlVoiceGender.MALE;
            case FEMALE:
                return SsmlVoiceGender.FEMALE;
            case NEUTRAL:
            default: // NEUTRAL par défaut
                return SsmlVoiceGender.NEUTRAL;
        }
    }

    private String getLanguageCode(LanguageCode languageCode) {
    	return languageCode.toString().replace("_", "-");
    }
    
    private AudioEncoding getAudioEncoding(TtsAudioEncoding ttsAudioEncoding) {
    	switch (ttsAudioEncoding) {
	    	case MP3:
	    		return AudioEncoding.MP3;
	    	case LINEAR16:
	    		return AudioEncoding.LINEAR16;
	    	case OGG_OPUS:
	    		return AudioEncoding.OGG_OPUS;
	    	default:
	    		return AudioEncoding.AUDIO_ENCODING_UNSPECIFIED;
    	}    	
    }
    
}



