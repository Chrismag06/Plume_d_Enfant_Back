package com.poec.plumedenfant.audio;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public interface AudioOutput {
    void playAudio(String filePath) throws IOException, LineUnavailableException, UnsupportedAudioFileException;
}