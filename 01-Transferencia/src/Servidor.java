
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static final int PORT = 9999;
    public static final String HOST = "localhost";

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Socket connectar() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");

        Socket socket = serverSocket.accept();          // (C) bloqueja fins que arriba un client
        System.out.println("Connexio acceptada: " + socket.getInetAddress());

        // ORDRE: primer OUTPUT (+flush), després INPUT per evitar deadLock
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        return socket;
    }

    public void enviarFitxers() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) in.readObject();        // rebre el nom
            System.out.println("Nomfitxer rebut: " + nomFitxer);

            byte[] contingut = null;
            try {
                Fitxer fitxer = new Fitxer(nomFitxer);
                contingut = fitxer.getContingut();              // llegeix el fitxer del disc
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");

                out.writeObject(fitxer);                        // enviar l'objecte Fitxer
                out.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } catch (IOException e) {
                System.out.println("Error llegint el fitxer del client: " + e.getMessage());
            }

            // Si el nom és buit/nul (o no s'ha pogut llegir el fitxer), sortim del bucle
            if (nomFitxer == null || nomFitxer.isBlank() || contingut == null) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                break;
            }
        }
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null) {
            socket.close();                             // tancar el socket
            System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = null;
        try {
            socket = servidor.connectar();
            servidor.enviarFitxers();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                servidor.tancarConnexio(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
