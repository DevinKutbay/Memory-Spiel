import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.swing.*;

/**
 * Eine Board-Implementierung, die Karten hält und deren Status beibehält.
 *
 * @author Devin Kutbay
 * @date 03.07.2021
 */
public class Board extends JPanel implements ActionListener {

    private static final String TAG = "Board: ";

    private int[] einstellungen = Einstellungen();
    private static final int grenzBreite = 20;
    private static final int minAnzahlKarten = 1;
    private int anzahlZeilen = einstellungen[0];
    private int anzahlSpalten = einstellungen[1];
    private int maxAnzahlKarten = einstellungen[2];
    private int anzahlPaare = maxAnzahlKarten / 2;
    private static final int maxAuswahlKarten = 2;
    private static final int ersteKarte = 0;
    private static final int zweiteKarte = 1;
    private static final int sichtbar = (int) 2 * 1000;
    private static final int spaehen = (int) 2 * 1000;

    // Karten Typen
    private static final int leereZelle = 0;
    private static final int verborgeneKarteArt = 102;
    private static final int leereKarteArt = 101;

    // Karten Bilder File Eigenschaften
    private static final String standartBildSuffix = ".jpg";
    private static final String standartBildPrefix = "meme-";
    private static final String standartBildOrdner = "/bilder/";
    private static final String verborgenesBildPfad = standartBildOrdner
            + standartBildPrefix + "102"
            + standartBildSuffix;
    private static final String leeresBildPfad = standartBildOrdner
            + standartBildPrefix + "101"
            + standartBildSuffix;

    private static ArrayList<Cell> gewaehlteKarten = new ArrayList<Cell>();
    private static int ausgewaehlteKarten = 0;

    private static int spieler1Punkte = 0;
    private static int spieler2Punkte = 0;
    private static int spieler = 1; // welcher Spieler am zug ist

    private Cell[][] mBoard = null;
    private String[] mKartenspeicher = initKartenSpeicher();
    private Cell[] mKartenChecker = new Cell[maxAuswahlKarten];

    ////////////////////////////////////////////////////////////////////////////
    // Konstruktor
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Initialisieren ein Board, das für ein Spiel bereit ist.
     */
    public Board() {


        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(grenzBreite,
                grenzBreite, grenzBreite, grenzBreite));
        setLayout(new GridLayout(anzahlZeilen, anzahlSpalten));

        mBoard = new Cell[anzahlZeilen][anzahlSpalten];

        for (int r = 0; r < anzahlZeilen; r++) {
            for (int s = 0; s < anzahlSpalten; s++) {
                mBoard[r][s] = new Cell(leereZelle);
                mBoard[r][s].addActionListener(this);
                add(mBoard[r][s]);
            }
        }

        init();
    }
    //////////////////////////////////////////////////////////////////////

    /**
     * Diese Methode initialisiert das Board mit einem neuen Kartensatz
     * d.h. Neues Spiel
     */
    public void init() {

        resetGefundeneBilder();
        resetBoardParam();
        ansehen();
        mKartenspeicher = initKartenSpeicher();
        setBilder();

    }

    /**
     * Diese Methode reinitialisiert das Board mit dem aktuellen Kartensatz,
     * d.h. Neustart
     */
    public void reInit() {

        resetGefundeneBilder();
        resetBoardParam();
        ansehen();
        setBilder();

    }

    /**
     * Diese Methode prüft, ob das Board gelöst ist oder nicht.
     *
     * @return true, wenn das Board gelöst ist, false, wenn noch Karten übrig sind, die
     * müssen übereinstimmen
     */
    public boolean istGeloest() {

        //Keine Überprüfung auf null, die Methode kann nicht aus .
        //eine Instanz und der Konstruktor initialisiert das mBoard
        //aufgerufen werden

        for (int r = 0; r < anzahlZeilen; r++) {
            for (int s = 0; s < anzahlSpalten; s++) {
                if (!mBoard[r][s].istLeer()) {
                    return false;
                }
            } // Spaltem-Loop
        } // Zeilen-Loop

        return true;
    }

    /**
     * Diese Methode fügt eine ausgewählte Karte zur
     * ausgewählten Kartenliste hinzu
     *
     * @param eineKarte
     */

    private void auswahlHinzufuegen(Cell eineKarte) {

        if (eineKarte != null) {
            if (!gewaehlteKarten.contains(eineKarte)) {
                gewaehlteKarten.add(eineKarte);
            }
        } else {
            error("auswahlHinzufuegen( Cell ) erhielt null.", true);
        }

    }

    /**
     * Diese Methode ist die Aktion, die ausgeführt wird,
     * wenn eine Karte angeklickt wird
     *
     * @param ae
     */
    public void actionPerformed(ActionEvent ae) {

        if (ae == null) {
            error("actionPermormed(ActionEvent) erhielt null", false);
            return;
        }

        if (!(ae.getSource() instanceof Cell)) {
            return;
        }

        if (!istKarteValid((Cell) ae.getSource())) {
            return;
        }

        ++ausgewaehlteKarten;

        if (ausgewaehlteKarten <= maxAuswahlKarten) {
            Point gridLoc = getCellOrt((Cell) ae.getSource());
            setKarteSichtbar(gridLoc.x, gridLoc.y);
            mKartenChecker[ausgewaehlteKarten - 1] = getCellAmOrt(gridLoc);
            auswahlHinzufuegen(getCellAmOrt(gridLoc));
        }

        if (ausgewaehlteKarten == maxAuswahlKarten) {

            if (!gleicheCellPosition(mKartenChecker[ersteKarte].getLocation(),
                    mKartenChecker[zweiteKarte].getLocation())) {

                setAusgewaehlteKarten(mKartenChecker[ersteKarte], mKartenChecker[zweiteKarte]);
            } else {
                --ausgewaehlteKarten;
            }
        }
    }

    //Diese Methode gibt die Position eines Cell-Objekts auf dem Board zurück
    private Cell getCellAmOrt(Point point) {
        if (point == null) {
            error("getCellAmLOrt( Point ) erhielt null", true);
            return null;
        }

        return mBoard[point.x][point.y];
    }

    // Diese Methode setzt eine Karte an einem bestimmten Ort auf sichtbar
    private void setKarteSichtbar(int x, int y) {

        mBoard[x][y].setSelected(true);
        showCardImages();
    }

    // Diese Methode lässt den Spieler kurz die karten sehen
    private void ansehen() {

        Action showImagesAction = new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                showCardImages();
            }
        };

        Timer timer = new Timer(spaehen, showImagesAction);
        timer.setRepeats(false);
        timer.start();
    }

    // Diese Methode setzt die bilder auf den Board
    private void setBilder() {

        ImageIcon anImage;

        for (int r = 0; r < anzahlZeilen; r++) {
            for (int s = 0; s < anzahlSpalten; s++) {

                URL file = getClass().getResource(
                        standartBildOrdner
                                + standartBildPrefix
                                + mKartenspeicher[s
                                + (anzahlSpalten * r)]
                                + standartBildSuffix);

                if (file == null) {
                    System.err.println(TAG
                            + "setImages() file nicht gefunden");
                    System.exit(-1);
                }

                anImage = new ImageIcon(file);

                mBoard[r][s].setIcon(anImage);

            } // Spalten-Loop
        } // Zeilen-Loop
    }

    // Diese Methode zeigt ein bestimmtes Bild an einem bestimmten Ort
    private void showBild(int x, int y) {

        URL file = getClass().getResource(
                standartBildOrdner + standartBildPrefix
                        + mKartenspeicher[y + (anzahlSpalten * x)]
                        + standartBildSuffix);

        if (file == null) {
            System.err.println(TAG
                    + "showImage(int, int) file nicht gefunden.");
            System.exit(-1);
        }

        ImageIcon anImage = new ImageIcon(file);
        mBoard[x][y].setIcon(anImage);

    }

    // Diese Methode sttet alle Bilder auf dem Board
    private void showCardImages() {

        // Für jede Karte auf dem Board
        for (int r = 0; r < anzahlZeilen; r++) {
            for (int s = 0; s < anzahlSpalten; s++) {

                // Ist eine Karte Selektiert
                if (!mBoard[r][s].isSelected()) {

                    // Wenn ausgewählt, überprüfen ob die Karte vom Benutzer übereinstimmt
                    if (mBoard[r][s].isMatched()) {
                        mBoard[r][s].setIcon(new ImageIcon(getClass()
                                .getResource(leeresBildPfad)));
                        mBoard[r][s].setTyp(leereKarteArt);
                    } else {
                        // Es ist nicht gleich wieder verstecken
                        mBoard[r][s].setIcon(new ImageIcon(getClass()
                                .getResource(verborgenesBildPfad)));
                        mBoard[r][s].setTyp(verborgeneKarteArt);
                    }

                } else {
                    // Keine Karte wurde ausgewählt
                    showBild(r, s);

                    String type = mKartenspeicher[s
                            + (anzahlSpalten * r)];
                    int parsedType = Integer.parseInt(type);

                    mBoard[r][s].setTyp(parsedType);

                } // wurden Karten ausgewählt
            } // inner loop - Spalten
        } // outer loop - Zeilen
    }

    // Diese Methode generiert zufällige Bilder

    private String generiereZufallBildFileName(int max, int min) {

        Random random = new Random();
        Integer aNumber = (min + random.nextInt(max));

        if (aNumber > 0 && aNumber < 10) {
            return "0" + aNumber;
        } else {
            return aNumber.toString();
        }
    }

    // Diese Methode erstellt ein Array von Zeichenfolgen,
    // das zufällige Bilder enthält, die in Paaren gruppiert sind.

    private String[] initKartenSpeicher() {

        String[] cardStorage = new String[maxAnzahlKarten];
        String[] firstPair = new String[anzahlPaare];
        String[] secondPair = new String[anzahlPaare];

        firstPair = zufallListeGen();

        for (int i = 0; i < anzahlPaare; i++) {
            cardStorage[i] = firstPair[i];
        }

        Collections.shuffle(Arrays.asList(firstPair));

        for (int j = 0; j < anzahlPaare; j++) {
            secondPair[j] = firstPair[j];
        }

        for (int k = anzahlPaare; k < maxAnzahlKarten; k++) {
            cardStorage[k] = secondPair[k - anzahlPaare];
        }

        return cardStorage;
    }

    //diese Methode soll eine Liste von
    // Paar-Bildern ohne Wiederholung generieren
    private String[] zufallListeGen() {

        String[] generatedArray = new String[anzahlPaare];
        ArrayList<String> generated = new ArrayList<String>();

        for (int i = 0; i < anzahlPaare; i++) {
            while (true) {
                String next = generiereZufallBildFileName(maxAnzahlKarten,
                        minAnzahlKarten);

                if (!generated.contains(next)) {
                    generated.add(next);
                    generatedArray[i] = generated.get(i);
                    break;
                }
            }
        }

        return generatedArray;
    }

    // Diese Methode ruft die Position einer Zelle auf dem Brett ab
    // und gibt diesen bestimmten Punkt zurück
    private Point getCellOrt(Cell einCell) {

        if (einCell == null) {
            error("getCellLocation(Cell) received null", true);
            return null;
        }

        Point p = new Point();

        for (int column = 0; column < anzahlZeilen; column++) {

            for (int row = 0; row < anzahlSpalten; row++) {

                if (mBoard[column][row] == einCell) {
                    p.setLocation(column, row);
                    return p;
                }
            }
        }
        return null;
    }

    //Diese Methode prüft, ob 2 Karten gleich sind
    private boolean gleicheCellPosition(Point ersteCell, Point zweiteCell) {

        if (ersteCell == null || zweiteCell == null) {
            if (zweiteCell == ersteCell) {
                return true;
            }
            if (ersteCell == null) {
                error("gleicheCellPosition(Point, Point) erhielt (null, ??)",
                        true);
            }
            if (zweiteCell == null) {
                error("gleicheCellPosition(Point, Point) erhielt (??, null)",
                        true);
            }
            return false;
        }
        if (ersteCell.equals(zweiteCell)) {
            return true;
        }
        return false;
    }


    // Diese Methode prüft, ob 2 ausgewählte Karten gleich sind,
    // also ersetzt sie sie durch eine leere Zelle oder
    // wenn sie unterschiedlich sind, dreht sie sie zurück,
    // sie prüft auch, ob das Board gelöst ist
    private void setAusgewaehlteKarten(Cell firstCell, Cell secondCell) {

        if (firstCell == null || secondCell == null) {

            if (firstCell == null) {
                error("setSelectedCards(Cell, Cell) received (null, ??)", true);
            }
            if (secondCell == null) {
                error("setSelectedCards(Cell, Cell) received (??, null)", true);
            }
            return;
        }

        if (firstCell.sameType(secondCell)) {

            firstCell.setMatched(true);
            secondCell.setMatched(true);
            firstCell.setSelected(false);
            secondCell.setSelected(false);
            showBild(getCellOrt(secondCell).x,
                    getCellOrt(secondCell).y);
            ansehen();
            if (spieler == 1) {
                spieler1Punkte = spieler1Punkte + 10;
            } else {
                spieler2Punkte = spieler2Punkte + 10;
            }
            anzeigeErgebnis();
        } else {

            firstCell.setMatched(false);
            secondCell.setMatched(false);
            firstCell.setSelected(false);
            secondCell.setSelected(false);
            showBild(getCellOrt(secondCell).x,
                    getCellOrt(secondCell).y);
            ansehen();
            if (spieler == 1) {
                spieler = 2;
            } else {
                spieler = 1;
            }
        }
        resetAusgewaehlteKarten();
    }

    // Diese Methode prüft, ob eine ausgewählte Karte gültig ist. Der Benutzer darf
    // nicht wieder leere Zellen auswählen
    private boolean istKarteValid(Cell aCard) {

        if (aCard == null) {
            error("isCardValid(Cell) erhielt null", false);
            return false;
        }

        if (!aCard.istLeer()) {
            return true;
        } else {
            return false;
        }

    }

    // Diese Methode zeigt das Resultat nach dem Spiel an
    private void anzeigeErgebnis() {

        @SuppressWarnings("serial")
        Action showImagesAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (istGeloest()) {


                    JOptionPane.showMessageDialog(null,
                            "Das Ergebnis:\n"
                                    + "\n Spieler 1: " + spieler1Punkte
                                    + "\n Spieler 2: " + spieler2Punkte
                                    + "", "Ergebnis",
                            JOptionPane.INFORMATION_MESSAGE);
                } // Wenn spiel Beendet
            } // actionPerformed()
        };

        Timer timer = new Timer(sichtbar, showImagesAction);
        timer.setRepeats(false);
        timer.start();

    }

    // Diese methode setzt alle Gefunden Bilder bei neuem
    //Spiel zurück
    private void resetGefundeneBilder() {
        for (int r = 0; r < anzahlZeilen; r++) {
            for (int s = 0; s < anzahlSpalten; s++) {
                if (mBoard[r][s].isMatched()) {
                    mBoard[r][s].setMatched(false);
                } // if
            } // Für Spalten
        } // Für Zeilen
    }


    /**
     * Error Reporter
     */
    private static void error(String message, boolean crash) {
        System.err.println(TAG + message);
        if (crash) {
            System.exit(-1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getter Methoden für Punkte und Spieler
    ////////////////////////////////////////////////////////////////////////////


    public int getSpieler1Punkte() {
        return spieler1Punkte;
    }

    public int getSpieler2Punkte() {
        return spieler2Punkte;
    }

    public static int getSpieler() {
        return spieler;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Zurücksetzer: Setzen die Werte für ein erneutes Spiel zurück
    ////////////////////////////////////////////////////////////////////////////


    //Diese Methode setzt die anzahl auswählbare Karten auf 0 zurück
    //nachdem 2 ausgewählt und überprüft wurden
    private static void resetAusgewaehlteKarten() {
        ausgewaehlteKarten = 0;
    }

    //setzt die Punkte zurück
    private static void resetPunkte() {
        spieler1Punkte = 0;
        spieler2Punkte = 0;
    }

    //setzt den Spieler auf spieler 1 zurück
    private static void resetSpieler() {
        spieler = 1;
    }

    // Diese Methode setzt die Parameter zurück
    // genutz wenn man erneut spielern will oder das Spiel zurücksetzt
    private static void resetBoardParam() {

        resetSpieler();
        resetPunkte();
    }

    /**
     * Diese Methode regelt die Einstellungen den die Spieler
     * am Beginn bestimmen.
     *
     * @return Array in dem die Werte für Spalten, Zeilen
     * und Maximale anzahl Karten im Feld bestimmt
     */
    private int[] Einstellungen() {
        SpinnerNumberModel zeilenSpinner = new SpinnerNumberModel(5, 1, 9, 1);
        SpinnerNumberModel spaltenSpinner = new SpinnerNumberModel(4, 2, 10, 2);
        JSpinner r = new JSpinner(zeilenSpinner);
        JSpinner s = new JSpinner(spaltenSpinner);
        JLabel einstell = new JLabel();
        int zeilen = 0;
        int spalten = 0;
        int[] e = new int[]{};
        while (!(zeilen >= 1 && zeilen <= 9 && spalten >= 1 && spalten <= 10)) {
            Object[] message = {
                    "Anzahl Zeilen 1-9: ", r,
                    "Anzahl Spalten 2-10 in 2er Schritten:", s,

            };
            int option = JOptionPane.showConfirmDialog(einstell, message, "Willkommen", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {


                zeilen = (Integer) r.getValue();
                spalten = (Integer) s.getValue();
                int max = zeilen * spalten;
                e = new int[]{zeilen, spalten, max};
            } else {
                System.exit(0);
            }
        }
        return e;
    }

}
