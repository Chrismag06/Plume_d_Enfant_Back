package com.poec.plumedenfant.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poec.plumedenfant.tts.GoogleTextToSpeechService;
import com.poec.plumedenfant.tts.LanguageCode;
import com.poec.plumedenfant.tts.TextToSpeechService;
import com.poec.plumedenfant.tts.TtsAudioEncoding;
import com.poec.plumedenfant.tts.VoiceGender;


@RestController
@RequestMapping("/api/tts")
public class TtsController {

    @GetMapping(value = "/speak", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> speak(@RequestParam String text) {
               
            LanguageCode languageCode =  LanguageCode.fr_FR;
            //VoiceGender gender = VoiceGender.FEMALE;
            VoiceGender gender = VoiceGender.MALE;
            TtsAudioEncoding encoding = TtsAudioEncoding.LINEAR16;
            byte[]audioData = null;

            TextToSpeechService ttsService = new GoogleTextToSpeechService();

            try {
                audioData = ttsService.convertTextToSpeech(text, languageCode, gender, encoding, 1.0).toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.wav\"")
                     .body(audioData);


    }
}
