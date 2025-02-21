package com.poec.plumedenfant.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poec.plumedenfant.dao.model.Histoire;
import com.poec.plumedenfant.service.HistoireService;
import com.poec.plumedenfant.tts.GoogleTextToSpeechService;
import com.poec.plumedenfant.tts.LanguageCode;
import com.poec.plumedenfant.tts.TextToSpeechService;
import com.poec.plumedenfant.tts.TtsAudioEncoding;
import com.poec.plumedenfant.tts.VoiceGender;


@RestController
@RequestMapping("/api/tts")
public class TtsController {

    @Autowired
	private HistoireService histoireService;

    @GetMapping(value = "/speak/{idHistoire}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> speak(@PathVariable int idHistoire) {
               
            LanguageCode languageCode =  LanguageCode.fr_FR;
            //VoiceGender gender = VoiceGender.FEMALE;
            VoiceGender gender = VoiceGender.MALE;
            TtsAudioEncoding encoding = TtsAudioEncoding.LINEAR16;
            byte[]audioData = null;

            TextToSpeechService ttsService = new GoogleTextToSpeechService();

            System.out.println("id histoire:" + idHistoire);

            try {

                Optional<Histoire> histoireRecup = histoireService.getHistoireById(idHistoire);
                if(histoireRecup.isPresent()) {
                    System.out.println("Creation audio");
                    audioData = ttsService.convertTextToSpeech(histoireRecup.get().getCorps(), languageCode, gender, encoding, 1.0).toByteArray();
                    if (audioData == null) {
                        System.out.println("Audio data null...");
                        return ResponseEntity.internalServerError().build(); 
                    }
                } else {
                    System.out.println("Histoire not found");
                    return ResponseEntity.notFound().build(); 
                }

                
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }

            if (audioData != null) { // Vérification avant de retourner la réponse
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.wav\"")
                        .body(audioData);
            } else {
                return ResponseEntity.internalServerError().build(); // Gestion du cas audioData null
            }

    }
}
