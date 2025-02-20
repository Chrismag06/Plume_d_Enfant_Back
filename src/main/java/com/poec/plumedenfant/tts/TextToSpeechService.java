package com.tts;

public interface TextToSpeechService {
	void convertTextToSpeech(String text, LanguageCode languageCode, VoiceGender voiceGender, TtsAudioEncoding ttsAudioEncoding, String outputFile) throws Exception;
	
}

