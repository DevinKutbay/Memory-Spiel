import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Ein Memory-Spiel, bei dem die Spieler ein Kartensatz gezeigt wird
 * und er dann sich an die Position jedes Paares erinnern müssen
 *
 * @author Devin Kutbay
 * @date 03.07.2021
 */
public class Memory extends JFrame {

    private Board mBoard;
    // GUI Komponente
    private JButton mNeustart;
    private JButton mNeuesSpiel;
    private JSplitPane mSplitPane;
    private JLabel mpunkte;


    ///////////////////////////////////////////////////////////////////////////
    // Konstruktor
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Erstellt einen Rahmen zum Starten und Anzeigen des
     * Spiels für den Benutzer.
     */
    public Memory() {


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.WHITE);


        mBoard = new Board();
        add(mBoard, BorderLayout.CENTER);

        mSplitPane = new JSplitPane();
        add(mSplitPane, BorderLayout.SOUTH);

        //Punkteanzeige: zeigt die Punkte an
        mpunkte = new JLabel("<html> Spieler 1: " + mBoard.getSpieler1Punkte() +
                "<br/><br/>Spieler " + mBoard.getSpieler() + " ist am Zug" +
                "<br/><br/> Spieler 2: " + mBoard.getSpieler2Punkte() + "<html>");
        add(mpunkte, BorderLayout.WEST);

        new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mpunkte.setText("<html>Spieler 1: " + mBoard.getSpieler1Punkte() +
                        "<br/><br/>Spieler " + mBoard.getSpieler() + " ist am Zug" +
                        "<br/><br/>Spieler 2: " + mBoard.getSpieler2Punkte() + "<html>");
            }
        }).start();


        //Neustart: Stellt das Spiel zurück
        mNeustart = new JButton("Neustarten");
        mNeustart.setFocusPainted(false);
        mNeustart.addMouseListener(btnMouseListener);
        mSplitPane.setLeftComponent(mNeustart);

        //Neues Spiel: Startet ein neues Spiel
        mNeuesSpiel = new JButton("Neues Spiel");
        mNeuesSpiel.setFocusPainted(false);
        mNeuesSpiel.addMouseListener(btnMouseListener);
        mSplitPane.setRightComponent(mNeuesSpiel);

        pack();
        setResizable(true);
        setVisible(true);

    }


    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    private MouseListener btnMouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1 && e.getComponent() == mNeustart) {
                mBoard.reInit();
            } else if (e.getClickCount() == 1 && e.getComponent() == mNeuesSpiel) {
                mBoard.init();
            }
        }
    };


    ///////////////////////////////////////////////////////////////////////////

    /**
     * Startet das Spiel.
     *
     * @param args
     */

    public static void main(String[] args) {
        new Memory();
    }
}
