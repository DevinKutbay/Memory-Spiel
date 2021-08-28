import javax.swing.JButton;

/**
 * Eine Zelle auf dem Brett, die die abstrahierte Darstellung ihrer Identität enthält
 * einen getTyp() / setTyp() Logik. Die Identität kann entweder eine versteckte Karte sein, eine Karte
 * das ein Bild oder eine Zelle enthält, die abgeglichen wurde und daher berücksichtigt wird
 * und nicht mehr sichtbar ist.
 *
 * @author Devin Kutbay
 * @date 03.07.2021
 */
public class Cell extends JButton {

    // Debug
    private static final String TAG = "Cell: ";

    // Zellen (Cell) typen
    private static final int maxTypReichweite = 102;
    private static final int minTypReichweite = 0;
    private static final int leereZelleTyp = 101;

    private boolean mIstSelected = false; //Sind auf englisch mit weniger ä
    private boolean mIstMatched = false; // hatte keine lust weitere ae, undsoweiter zu schreiben
    private int mTyp = leereZelleTyp;

    ////////////////////////////////////////////////////////////////////////////
    // Konstruktor
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Erstellt eine Zelle für den spezifischen Typen
     */
    public Cell(int aTyp) {
        super();
        mTyp = aTyp;
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Diese Methode ist der Getter für den typen
     *
     * @return ein int-Wert, der ein bestimmtes repräsentiert
     * Karte, ein leerer Zellenplatz oder eine Karte,
     * die derzeit ausgeblendet ist.
     */
    public int getTyp() {
        return mTyp;
    }

    /**
     * Legt den Typ dieser Zelle fest. Der Bereich liegt zwischen
     * minTypReichweite und maxTypReichweite
     *
     * @param einTyp
     */
    public void setTyp(int einTyp) {
        if (einTyp > maxTypReichweite || einTyp < minTypReichweite) {
            error("setTyp(int) gemeldet \"Invalider typ Code\"", true);
        }
        mTyp = einTyp;
    }

    /**
     * Diese Methode prüft, ob 2 Zellen vom gleichen Typ sind
     *
     * @param anderer
     * @return true, wenn die angegebene Zelle dasselbe Bild teilt,
     * false, wenn die Zellen nicht verknüpft sind.
     */
    public boolean sameType(Cell anderer) {

        if (anderer == null) {
            error("gleicherTyp(Cell) erhielt null", false);
            return false;
        }

        if (this.getTyp() == anderer.getTyp()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Diese Methode prüft, ob der Typ dieser Zelle leer ist (leere Zelle)
     *
     * @return true, wenn diese Zelle als leer angesehen wird, false
     * wenn diese Zelle noch nicht mit einer anderen Zelle gekoppelt wurde
     */
    public boolean istLeer() {
        if (this.mTyp != leereZelleTyp) {
            return false;
        }
        return true;
    }

    /**
     * Diese Methode setzt die Zelle auf ausgewählt
     *
     * @param ausgewaehlt
     */
    public void setSelected(boolean ausgewaehlt) {
        mIstSelected = ausgewaehlt;
    }

    /**
     * Diese Methode setzt die Zelle auf Übereinstimmungen
     *
     * @param stimmt
     */
    public void setMatched(boolean stimmt) {

        mIstMatched = stimmt;
    }

    /**
     * Diese Methode prüft, ob eine Zelle ausgewählt ist
     *
     * @return true, wenn der Benutzer gerade diese Zelle auswählt,
     * false, wenn die Zelle nicht ausgewählt wurde
     */
    public boolean isSelected() {

        if (mIstSelected == true) {
            return true;
        }

        return false;
    }

    /**
     * Diese Methode prüft, ob eine Zelle übereinstimmt
     *
     * @return true, wenn die Zelle zuvor mit ihrer Schwesterzelle gekoppelt wurde,
     * false, wenn sie noch vom Spieler gepaart werden muss
     */
    public boolean isMatched() {

        if (mIstMatched == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Error Reporter
     */
    private static void error(String message, boolean crash) {
        System.err.println(TAG + message);
        if (crash) System.exit(-1);
    }

}
