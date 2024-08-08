import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.sound.midi.*;

public class MainMenu extends JFrame implements ActionListener {
    // Attributes
    private JPanel scene;
    private Color myColor = new Color(245, 245, 220); // beige
    private Color buttonColor = new Color(0, 206, 209); // teal

    // Add your theme song
    private static final int[] notesRemix = {
    71, 73, 76, 71, 73, 74, 71, 73, 71, 69, 72, 74, 69, 67,
    71, 69, 67, 69, 71, 74, 72, 71, 74, 72, 71, 74, 76, 79, 76,
    74, 71, 72, 74, 71, 72, 74, 71, 69, 72, 74, 69, 67, 71, 69,
    67, 69, 71, 74, 72, 71, 74, 72, 71, 74, 76, 79, 76, 74, 71,
    72, 74, 71, 72, 74, 71, 69, 72, 74, 69, 67, 71, 69, 67,
};

    private Sequencer sequencer;

    // Constructor
    public MainMenu() {
        setTitle("LET'S PLAY MUSIC GAME!!");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // JPanel setup
        scene = new JPanel();
        scene.setLayout(new BoxLayout(scene, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
        add(scene);
        scene.setBackground(myColor);

        // Strings to print and font
        JLabel title = new JLabel("MUSIC GAME");
        title.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the title
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(buttonColor);

        JLabel instructions = new JLabel("INSTRUCTIONS:");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the instructions
        instructions.setFont(new Font("Arial", Font.BOLD, 24));
        instructions.setForeground(buttonColor);

        JLabel[] instLabels = {
            new JLabel("1.) Press the Play Button"),
            new JLabel("2.) Hit the Left and Right Arrow Keys to move"),
            new JLabel("3.) Land on Platforms to Play Notes"),
            new JLabel("4.) Run into Power Ups for a New Experience"),
            new JLabel("5.) DON'T FALL DOWN!!!!!")
        };

        // Set font and foreground color for instruction labels
        for (JLabel instLabel : instLabels) {
            instLabel.setFont(new Font("Arial", Font.BOLD, 16));
            instLabel.setForeground(buttonColor);
        }

        // Create button
        JButton b = new JButton("LET'S PLAY!!!");
        b.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        b.setBackground(buttonColor);
        b.addActionListener(this);

        // Add components to panel
        scene.add(Box.createVerticalGlue()); // Add space at the top
        scene.add(title);
        scene.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between title and instructions
        scene.add(instructions);
        scene.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between instructions and buttons
        for (JLabel instLabel : instLabels) {
            JPanel instructionPanel = new JPanel();
            instructionPanel.setOpaque(false); // Make the panel transparent
            instructionPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align labels to the left
            instructionPanel.add(instLabel);
            scene.add(instructionPanel);
            scene.add(Box.createRigidArea(new Dimension(0, 5))); // Add space between instruction lines
        }
        scene.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between instructions and button
        scene.add(b);
        scene.add(Box.createVerticalGlue()); // Add space at the bottom

        // Centers main menu window
        setLocationRelativeTo(null);

        setVisible(true);

        // Start playing the theme song
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            Sequence sequence = createSequence(notesRemix);
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    // Action performed method
    @Override
    public void actionPerformed(ActionEvent e) {
        // Stop the theme song
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            sequencer.close();
        }

        SwingUtilities.invokeLater(new MusicGame());
        // Closes main menu window after starting game
        setVisible(false);
    }

    // Method to create a MIDI sequence from an array of notes
    private Sequence createSequence(int[] notes) throws InvalidMidiDataException{
        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();

        for (int i = 0; i < notes.length; i++) {
            int note = notes[i];
                track.add(createNoteOnEvent(note, i * 4));
                track.add(createNoteOffEvent(note, i * 4 + 4));
            }
        return sequence;
        }
    

    // Method to create a MIDI event to start a note
    private MidiEvent createNoteOnEvent(int nKey, long lTick) throws InvalidMidiDataException {
        return createNoteEvent(ShortMessage.NOTE_ON, nKey, 93, lTick);
    }

    // Method to create a MIDI event to stop a note
    private MidiEvent createNoteOffEvent(int nKey, long lTick) throws InvalidMidiDataException {
        return createNoteEvent(ShortMessage.NOTE_OFF, nKey, 0, lTick);
    }

    // Method to create a MIDI event
    private MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(nCommand, 0, nKey, nVelocity);
        return new MidiEvent(message, lTick);
    }

    // Main method
    public static void main(String[] args) {
        new MainMenu();
    }
}