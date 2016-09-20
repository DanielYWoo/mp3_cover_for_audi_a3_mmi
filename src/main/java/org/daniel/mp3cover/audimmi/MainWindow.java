package org.daniel.mp3cover.audimmi;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

public class MainWindow extends JPanel implements ActionListener {

    private JButton openButton;
    private JTextArea log;
    private JFileChooser fc;

    public MainWindow() {
        super(new BorderLayout());

        log = new JTextArea(40, 80);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        openButton = new JButton("Click to choose the directory to convert");
        openButton.addActionListener(this);
        add(openButton, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(MainWindow.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                log.append("Opening: " + file.getAbsolutePath() + ".\n");
                new Converter(log).convert(file);
                log.append("Done!\n");
            } else {
                log.append("Open command cancelled by user.\n");
            }
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            JFrame frame = new JFrame("Mp3 Cover converter for Audi A3 MMI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MainWindow());
            frame.pack();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
            frame.setVisible(true);
        });
    }
}
