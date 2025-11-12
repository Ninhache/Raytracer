package fr.ninhache.raytracer.scene.exception;

/**
 * Exception levée lors d'une erreur de parsing de fichier de scène.
 *
 * <p>Contient des informations contextuelles pour faciliter le débogage :
 * numéro de ligne, contenu de la ligne, raison de l'erreur.
 */
public class ParseException extends Exception {

    private final int lineNumber;
    private final String lineContent;

    public final static String NUMBER_OF_ARGS = "Nombre d'arguments invalide";

    /**
     * Crée une exception de parsing avec contexte.
     *
     * @param message description de l'erreur
     * @param lineNumber numéro de ligne où l'erreur s'est produite (0 si non applicable)
     * @param lineContent contenu de la ligne en erreur (null si non applicable)
     */
    public ParseException(String message, int lineNumber, String lineContent) {
        super(formatMessage(message, lineNumber, lineContent));
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    /**
     * Crée une exception de parsing sans contexte de ligne.
     *
     * @param message description de l'erreur
     */
    public ParseException(String message) {
        this(message, 0, null);
    }

    /**
     * Crée une exception de parsing avec cause.
     *
     * @param message description de l'erreur
     * @param lineNumber numéro de ligne
     * @param lineContent contenu de la ligne
     * @param cause exception sous-jacente
     */
    public ParseException(String message, int lineNumber, String lineContent, Throwable cause) {
        super(formatMessage(message, lineNumber, lineContent), cause);
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
    }

    private static String formatMessage(String message, int lineNumber, String lineContent) {
        StringBuilder sb = new StringBuilder(message);
        if (lineNumber > 0) {
            sb.append(" (ligne ").append(lineNumber).append(")");
        }
        if (lineContent != null && !lineContent.isEmpty()) {
            sb.append("\n  → ").append(lineContent);
        }
        return sb.toString();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }
}