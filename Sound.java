/* 
import javax.sound.midi.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Sound {
    private static final int VELOCITY_HIGH = 127;
    private static final int VELOCITY_LOW = 90;
    private static final int DURATION_LONG = 500;
    private static final int DURATION_SHORT = 250;
    private static final int MAX_ACTIVE_NOTES = 6;
    private static final AtomicInteger activeNotes = new AtomicInteger(0);

    private static Synthesizer synth;
    private static MidiChannel channel;
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final int[] scaleNotes = {
        64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69, 67, 71, 69, 67,
        71, 64, 64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69, 67, 71,
        69, 67, 71, 64, 64, 71, 73, 64, 71, 73, 64, 71, 73, 71, 69,
        67, 71, 69, 67, 71
    };

    private static final int[] scaleNotesHard = {
        50, 53, 55, 56, 58, 50, 53, 55, 56, 58, 50, 53, 55, 56, 58,
        50, 53, 55, 56, 58
    };

    private static int currentNoteIndex = 0;

    static {
        initializeSound();
    }

    private static int[] lastSelectedScale = null;

    public static void playNote(Player player) {
        int[] selectedScale = (player.getScore() >= 100) ? scaleNotesHard : scaleNotes;

        if (lastSelectedScale != selectedScale) {
            stopAllNotes();
            lastSelectedScale = selectedScale;
            currentNoteIndex = 0; // Reset the note index when the scale changes
        }

        int note = selectedScale[currentNoteIndex];
        int velocity = ((currentNoteIndex % 8 == 0) ? VELOCITY_HIGH : VELOCITY_LOW);
        int duration = ((currentNoteIndex % 2 == 0) ? DURATION_LONG : DURATION_SHORT);

        if (activeNotes.get() < MAX_ACTIVE_NOTES) {
            activeNotes.incrementAndGet();
            playSound(note, velocity, duration);
        }

        currentNoteIndex = (currentNoteIndex + 1) % selectedScale.length;
    }

    public static void stopAllNotes() {
        if (synth != null) {
            for (MidiChannel channel : synth.getChannels()) {
                if (channel != null) {
                    channel.allNotesOff();
                }
            }
        }
    }

    public static void handleCollisionWithPaddle(Player player) {
        int[] selectedScale = (player.getScore() >= 100) ? scaleNotesHard : scaleNotes;
        long delay = 0;
        for (int note : selectedScale) {
            executor.schedule(() -> {
                if (activeNotes.get() < MAX_ACTIVE_NOTES) {
                    activeNotes.incrementAndGet();
                    playSound(note, 127, 100);
                }
            }, delay, TimeUnit.MILLISECONDS);
            delay += 1;
        }
    }

    public static void gameOverJingle() {
        stopAllNotes();
        int[] jingleNotes = {45, 57, 41, 52, 36};
        long delay = 0;
        for (int note : jingleNotes) {
            executor.schedule(() -> {
                if (activeNotes.get() < MAX_ACTIVE_NOTES) {
                    activeNotes.incrementAndGet();
                    playSound(note, 127, 300);
                }
            }, delay, TimeUnit.MILLISECONDS);
            delay += 200;
        }
    }

    public static void playPowerUpSound() {
        stopAllNotes();
        if (activeNotes.get() < MAX_ACTIVE_NOTES) {
            activeNotes.incrementAndGet();
            playSound(90, 127, 500);
        }
    }

    public static void initializeSound() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channel = synth.getChannels()[0];
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        try {
            // Shut down the executor
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }

            // Close the synth
            if (synth != null) {
                synth.close();
                synth = null;
            }

            // Reset other variables
            currentNoteIndex = 0;
            lastSelectedScale = null;
            activeNotes.set(0);

            // Reinitialize the executor and the sound
            executor = Executors.newSingleThreadScheduledExecutor();
            initializeSound();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void playSound(int note, int velocity, int duration) {
        executor.submit(() -> {
            try {
                channel.allNotesOff();
                channel.noteOn(note, velocity);
                Thread.sleep(duration);
                channel.noteOff(note);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                activeNotes.decrementAndGet();
            }
        });
    }
}
*/