package com.poec.plumedenfant.tts;

import com.google.protobuf.ByteString;

public interface TextToSpeechService {

	ByteString convertTextToSpeech(String text, LanguageCode languageCode, VoiceGender voiceGender, TtsAudioEncoding ttsAudioEncoding, Double speakingRate) throws Exception;

}

