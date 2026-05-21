
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

    public static final String DIR_ARRIBADA = "D:\\Users\\Miguel\\Temp";

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public void connectar() throws IOException {
        System.out.println("Connectant a -> " + Servidor.HOST + ":" + Servidor.PORT);
        socket = new Socket(Servidor.HOST, Servidor.PORT);

        // ORDRE: primer OUTPUT (+flush), després INPUT
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connexio acceptada: " + socket.getInetAddress());
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = sc.nextLine();              // llegir línia de consola

            // Enviem sempre el nom al servidor (també "sortir")
            out.writeObject(nomFitxer);
            out.flush();

            // Comprovem si volem sortir
            if (nomFitxer == null || nomFitxer.isBlank() || nomFitxer.equals("sortir")) {
                System.out.println("Sortint...");
                break;
            }

            // Rebem l'objecte Fitxer del servidor
            Fitxer fitxer = (Fitxer) in.readObject();

            // Guardem el contingut a DIR_ARRIBADA, mantenint el nom del fitxer
            String nomBase = new File(nomFitxer).getName();
            var rutaDesti = Paths.get(DIR_ARRIBADA, nomBase);
            System.out.println("Nom del fitxer a guardar: " + rutaDesti);
            Files.write(rutaDesti, fitxer.getContingut());  // escriure els bytes al disc
            System.out.println("Fitxer rebut i guardat com: " + rutaDesti);
        }
    }

    public void tancarConnexio() throws IOException {
        if (socket != null) {
            socket.close();
            System.out.println("Connexio tancada.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxers();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                client.tancarConnexio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
