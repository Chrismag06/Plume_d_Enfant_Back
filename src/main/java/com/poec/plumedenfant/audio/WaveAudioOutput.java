package com.poec.plumedenfant.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WaveAudioOutput implements AudioOutput{

	@Override
	public void playAudio(String filePath) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		// TODO Auto-generated method stub
        File audioFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

        audioLine.open(format);
        audioLine.start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = audioStream.read(buffer)) != -1) {
            audioLine.write(buffer, 0, bytesRead);
        }

        audioLine.drain();
        audioLine.close();
        audioStream.close();
	}

	
}
