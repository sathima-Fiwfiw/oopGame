import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterAndIPSelection extends JFrame {
    private String selectedCharacter;
    private JTextField ipField;

    public CharacterAndIPSelection() {
        setTitle("Select Character & Enter Host IP");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create IP input components
        JLabel ipLabel = new JLabel("Enter Host IP Address:");
        ipField = new JTextField(15);

        // Create character buttons
        JLabel charLabel = new JLabel("Select Your Character:");
        JButton character1 = new JButton("Character 1");
        JButton character2 = new JButton("Character 2");
        JButton character3 = new JButton("Character 3");

        // Button to confirm selection and connect
        JButton connectButton = new JButton("Connect");

        // Add action listeners to the character buttons
        character1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCharacter = "Character 1";
            }
        });

        character2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCharacter = "Character 2";
            }
        });

        character3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCharacter = "Character 3";
            }
        });

        // Add action listener to the connect button
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ipAddress = ipField.getText();
                if (!ipAddress.isEmpty() && selectedCharacter != null) {
                    // Here you can add logic to connect to the game using the IP and selected character
                    JOptionPane.showMessageDialog(null, "Connecting to " + ipAddress + " with " + selectedCharacter);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter IP and select a character.");
                }
            }
        });

        // Layout setup
        setLayout(new GridLayout(6, 1));
        add(ipLabel);
        add(ipField);
        add(charLabel);
        add(character1);
        add(character2);
        add(character3);
        add(connectButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CharacterAndIPSelection().setVisible(true);
        });
    }
}