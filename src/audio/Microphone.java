package audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.Scanner;

public class Microphone {

    private AudioFormat audioFormat;
    private TargetDataLine microphone;
    private ByteArrayOutputStream byteOutputStream;
    private DataLine.Info info;
    private boolean capturing;
    private CaptureThread captureThread;

    public byte[] audioData;

    public Microphone(){
        audioFormat = getAudioFormat();
    }

    private AudioFormat getAudioFormat(){
        float sampleRate = 8000.0f;
        int sampleSize = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;

        return new AudioFormat(sampleRate, sampleSize, channels, signed, bigEndian);
    }

    public void startCapturing(){

        try{
            info = new DataLine.Info(TargetDataLine.class, audioFormat);
            microphone = (TargetDataLine) AudioSystem.getLine(info);

            captureThread = new CaptureThread();
            captureThread.start();
        }
        catch (LineUnavailableException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    public void stopCapturing(){
        capturing = false;

        while(captureThread.isAlive());

        audioData = byteOutputStream.toByteArray();
    }

    public boolean isCapturing(){
        return captureThread.isAlive();
    }

    private class CaptureThread extends Thread{
        @Override
        public void run() {

            byteOutputStream = new ByteArrayOutputStream();
            int bytesRead;
            int totalBytesRead = 0;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];

            capturing = true;
            System.out.println("Capturing...");
            try {
                microphone.open();
                microphone.start();

                while(capturing){
                    bytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    totalBytesRead += bytesRead;
                    byteOutputStream.write(data, 0, bytesRead);
                }

                microphone.stop();
                microphone.close();

                System.out.println("Capturing ended.");

            } catch (LineUnavailableException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }
    }

    // Main para testes
    public static void main(String[] args) {
        Microphone mic = new Microphone();
        Speaker speaker = new Speaker();

        mic.startCapturing();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        mic.stopCapturing();
        speaker.play(mic.audioData);
        while(speaker.isPlaying());
        speaker.play(mic.audioData);
    }
}
