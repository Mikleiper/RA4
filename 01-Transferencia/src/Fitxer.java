
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class Fitxer implements Serializable {

    private static final long serialVersionUID = 1L; //número de versión de la clase. Sirve para que, al deserializar, Java confirme que la clase del emisor y la del receptor son compatibles.

    private String nom;
    private byte[] contingut;

    // Constructor amb nom
    public Fitxer(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    // Llegeix el fitxer del disc i carrega 'contingut'
    public byte[] getContingut() throws IOException {
        File f = new File(nom);
        if (!f.exists()) {
            throw new IOException("El fitxer no existeix: " + nom);
        }
        this.contingut = Files.readAllBytes(f.toPath());  //  llegir tots els bytes
        return this.contingut;
    }
}
