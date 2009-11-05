package regadera;

import java.io.*;

public class WriterLog {

    public static void main(String[] args) throws IOException {



        // Depositar Datos en un Archivo de Texto
        try {

            PrintWriter escribir = new PrintWriter(
                    new BufferedWriter(new FileWriter("Archivo_Stream.txt")));


            escribir.println(": ");
            escribir.close();
        } catch (EOFException e) {
            System.out.println("Final de Stream");
        }
    }
}
