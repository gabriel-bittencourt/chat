package audio;


import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Speaker {

    private AudioFormat audioFormat;
    private SourceDataLine speaker;
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

        System.out.println("audio");
        System.out.println(audioData);

        byteInputStream = new ByteArrayInputStream(audioData);
        audioInputStream = new AudioInputStream(byteInputStream, audioFormat,
                audioData.length / audioFormat.getFrameSize());

        try {
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
            speaker = (SourceDataLine) AudioSystem.getLine(info);

            playThread = new PlayThread();
            playThread.start();
        }
        catch(LineUnavailableException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

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
                speaker.open(audioFormat);
                speaker.start();

                do {
                    cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0)
                        speaker.write(tempBuffer, 0, cnt);
                } while (cnt != -1);

                speaker.drain();
                speaker.close();
                System.out.println("Done!");
            }
            catch(LineUnavailableException | IOException e){
                System.out.println(e.toString());
                e.printStackTrace();;
            }
        }
    }
}
