/*
 * Regadera: programa para enviar listas de mails vía ssmtp
 * Copyleft Sergio A. Alonso - escuelaint @ gmail.com
 * 
 * Links utilizados
 * - http://www.forosdelweb.com/f45/ejecutar-comando-linux-desde-java-498304/
 * - http://www.chuidiang.com/chuwiki/index.php?title=Lectura_y_Escritura_de_Ficheros_en_Java
 *
 * Ver archivo de ejemplo en /src -> alcaldesa.html
 * Para crear comodamente el archivo HTML, lo ideal es utilizar el composer de
 * seamonkey, amaya o kompozer, o vim
 *
 * Ejemplo de inyección de correo con HTML simple, que vincula a imagen alojada en algun server:
 *

From: Informes Fundación Islas Malvinas - <info@eim.edu.ar>
Subject: Pequeños y Teens Chefs: a clase!
Content-Type: text/html;charset=UTF-8
Content-Transfer-Encoding: 7bit

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta content="text/html;charset=UTF-8" http-equiv="Content-Type">
</head>
<body>
 Escuela de Cocina para Niños y Adolescentes<br>
 Haga click <a href="http://www.eim.esc.edu.ar/site/descargas/peqyteens.jpg"">aqui</a> si no puede ver el siguiente mensaje.
<img style="width: 550px; height: 2054px;" src="http://www.eim.esc.edu.ar/site/descargas/peqyteens.jpg"><br>
¡¡Agradecemos su difusión¡¡</p>
<p></p>
<p>Si desea desuscribirse, a nuestro boletin de noticias, haga click <a
 href="http://spreadsheets.google.com/viewform?hl=es&amp;formkey=dEpMQ0VOYnlPUmZQekNwTV9Tdk4zOFE6MA">aqui</a></p>
</body>
</html>
 *
 * No olvidar incluir opcion de desuscripcion. Yo uso un formulario / hoja de calculo
 * de google docs, que es lo mas comodo. En este caso:
 *
 * http://spreadsheets.google.com/viewform?hl=es&formkey=dEpMQ0VOYnlPUmZQekNwTV9Tdk4zOFE6MA
 *
 * Los HTML a enviar deben tener un encabezado que el server de correo pueda aceptar, como
 *
 * From: Informes Fundación Islas Malvinas - <info@eim.edu.ar>
 * Subject: Eje turístico: visita de Alcaldesa de Viña del Mar
 * Content-Type: text/html;charset=UTF-8
 * Content-Transfer-Encoding: 7bit
 * 
 * Si se poseen bases de datos muy grandes, para no abusar del server SMTP, se
 * recomienda tambien leer http://bunker-blog.blogspot.com/2009/11/sexpy.html
 * 
 */
package regadera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 *
 * @author s
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        /**
         * Comienza lectura de archivo
         */
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        //    try {
        // Apertura del fichero y creacion de BufferedReader para poder
        // hacer una lectura comoda (disponer del metodo readLine()).
        archivo = new File("regadera/limpios.txt");
        fr = new FileReader(archivo);
        br = new BufferedReader(fr);

        // Lectura del fichero
        String linea;
        Vector v = new Vector();

        while ((linea = br.readLine()) != null) {
            System.out.println("Alimentando un Vector v con: " + linea);
            v.addElement(linea);
        }

        try {

            //Me envio una copia...
            //String[] command = {"sh", "-c", "ssmtp -v escuelaint@gmail.com < regadera/peqchefs.html"};


            for (int i = 0; i < v.size(); i++) {
                // aqui sacamos el objeto que se encuentra en el indice o posicion i-esima
                Object objeto = v.elementAt(i);

                // si queremos convertirlo a algun tipo, nos toca hacer un casting (Tipo), asi:
                // ojo! esto solo lo puedes hacer si agregaste objetos de tipo String en el vector
                String str = (String) objeto;
                //System.out.println("Leyendo desde vector: " + str);

                /**
                 * Comienza ejecución de comando en consola
                 */
                try {
                    //String command = "ls -lh ";

                    String[] command = {"sh", "-c", "ssmtp -v " + str + " < regadera/peqchefs.html"};

                    //guardo en el log lo que guardé...
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter("regadera/traza.log", true));
                        out.write(str + "\n");
                        out.close();
                    } catch (IOException e) {
                    }

                    //corro el proceso
                    final Process process = Runtime.getRuntime().exec(command);
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                InputStream is = process.getInputStream();
                                byte[] buffer = new byte[1024];
                                for (int count = 0; (count = is.read(buffer)) >= 0;) {
                                    System.out.write(buffer, 0, count);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    new Thread() {

                        public void run() {
                            try {
                                InputStream is = process.getErrorStream();
                                byte[] buffer = new byte[1024];
                                for (int count = 0; (count = is.read(buffer)) >= 0;) {
                                    System.err.write(buffer, 0, count);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    int returnCode = process.waitFor();
                    System.out.println("Return code = " + returnCode);

                    System.out.println("Enviado mail: " + str + " " + (i + 1));//para que no empiece a contar desde el 0




                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    System.out.println("Reteniendo siguiente mensaje...");
                    //Thread.sleep(30 * 1000);
                    Thread.sleep(60 * 1000);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }



            }//fin for

            //escribir.close();
//            } catch (EOFException e) { //fin escritura de archivo
//
//                System.out.println("Final de Stream");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }

        System.out.println("Programa terminado");





    }//fin public static void main(String[] args) {
}//Fin public class Main {


