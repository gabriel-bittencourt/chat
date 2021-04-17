package audio;

import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Speaker {

    private AudioFormat audioFormat;
    private SourceDataLine speakers;
    private ByteArrayInputStream byteInputStream;
    private AudioInputStream audioInputStream;
    private DataLine.Info info;
    private PlayThread playThread;

    public Speaker(){
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

    public void play(byte[] audioData){

        byteInputStream = new ByteArrayInputStream(audioData);
        audioInputStream = new AudioInputStream(byteInputStream, audioFormat,
                audioData.length / audioFormat.getFrameSize());

        try {
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
            speakers = (SourceDataLine) AudioSystem.getLine(info);

            playThread = new PlayThread();
            playThread.start();
        }
        catch(LineUnavailableException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        System.out.println("Done!");
    }

    public boolean isPlaying(){
        return playThread.isAlive();
    }

    class PlayThread extends Thread{
        @Override
        public void run() {
            byte[] tempBuffer = new byte[10000];
            int cnt;

            System.out.println("Playing audio...");
            try {
                speakers.open(audioFormat);
                speakers.start();

                do {
                    cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0)
                        speakers.write(tempBuffer, 0, cnt);
                } while (cnt != -1);

                speakers.drain();
                speakers.close();
            }
            catch(LineUnavailableException | IOException e){
                System.out.println(e.toString());
                e.printStackTrace();;
            }
        }
    }
}
