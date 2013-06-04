/*
 * 
 * Links utilizados
 * - http://www.forosdelweb.com/f45/ejecutar-comando-linux-desde-java-498304/
 * - http://www.chuidiang.com/chuwiki/index.php?title=Lectura_y_Escritura_de_Ficheros_en_Java
 *
 * Para crear comodamente el archivo HTML, lo ideal es utilizar el composer de
 * seamonkey, amaya o kompozer, o vim
 *
 * Ejemplo de inyección de correo con HTML simple, que vincula a imagen alojada en algun server:
 *

From: Bla - <bla@blah.com.ar>
Subject: Novedades!
Content-Type: text/html;charset=UTF-8
Content-Transfer-Encoding: 7bit

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta content="text/html;charset=UTF-8" http-equiv="Content-Type">
</head>
<body>

blah

</body>
</html>
 *
 * No olvidar incluir opcion de desuscripcion. Yo uso un formulario / hoja de calculo
 * de google docs, que es lo mas comodo. En este caso:
 *
 *
 * Los HTML a enviar deben tener un encabezado que el server de correo pueda aceptar, como
 *
 * From: Informes Fulano - <info@***.edu.ar>
 * Subject: bla
 * Content-Type: text/html;charset=UTF-8
 * Content-Transfer-Encoding: 7bit
 *
 * No sea rata: el From debe referirse a una cuenta paga, al menos de aquellas que 
 * valen u$s 0,7 mensuales, en un hosting lo mas amable posible.
 * 
 * Este programa no esta creado para emitir SPAM, sino mantenerse justo por debajo de 
 * la cantidad de correos que incluye nuestro plan de hosting.
 *
 * No abuse del SMTP de su hosting, y consulte a la mesa de ayuda cuantos correos diarios 
 * puede enviar. Luego, ajuste el valor Thread.sleep(60 * 1000);
 * En forma predefinido, 60*1000 es un mail por minuto, es decir, 24 hs * 60 m = 1440 
 * correos diarios. Para no abusar del SMTP, mantengase por debajo de la cantidad permitida. 
 * 
 * Si posee bases de datos muy grandes, evite mandar correos duplicados: se
 * recomienda tambien leer http://bunker-blog.blogspot.com/2009/11/sexpy.html
 *
 * No embeba imagenes; no abuse del SMTP, solo vincule a imagenes externas
 * Si son imagenes muy grandes, el browser mostrará una versión mas pequeña, y hay usuarios
 * que no saben usar la lupa para agrandarla, de modo que en el <img use dimensiones, o 
 * apunte a un html externo que contenga la foto: el navegador no la redimensionará.
 * y por cierto, use la proiedad alt de <img por si el cliente de correo no muestra imagenes.
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
    archivo = new File("regadera/limpitos.txt");
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

      //anda esto? configurar ssmtp.conf
      //y disparar en consola algo tipo
      //ssmtp -v escuelaint@gmail.com < regadera/htmls/panadero.html

      //Me envio una copia...
      //String[] command = {"sh", "-c", "ssmtp -v escuelaint@gmail.com < regadera/htmls/panadero.html"};


      for (int i = 0; i < v.size(); i++) {
        // aqui sacamos el objeto que se encuentra en el indice o posicion i-esima
        Object objeto = v.elementAt(i);

        // si queremos convertirlo a algun tipo, nos toca hacer un casting (Tipo), asi:
        // ojo! esto solo lo puedes hacer si agregaste objetos de tipo String en el vector
        String str = (String) objeto;

        //Le limpio espacios...
        String strLimpio = str.trim();
        //System.out.println("Leyendo desde vector: " + strLimpio);

        /**
         * Comienza ejecución de comando en consola
         */
        try {
          //String command = "ls -lh ";

          //String[] command = {"sh", "-c", "ssmtp -v " + strLimpio + " < regadera/htmls/peqchefs.html"};
          String[] command = {"sh", "-c", "ssmtp -v " + strLimpio + " < regadera/htmls/cotidiana.html"};

          //guardo en el log lo que guardé...
          try {
            BufferedWriter out = new BufferedWriter(new FileWriter("regadera/traza.log", true));
            out.write(strLimpio + "\n");
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

          System.out.println("Enviado mail: " + strLimpio + " " + (i + 1));//para que no empiece a contar desde el 0




        } catch (Exception e) {
          e.printStackTrace();
        }

        try {
          System.out.println("Reteniendo siguiente mensaje...");
          //Thread.sleep(60 * 1000); //uno por minuto, 1440 cada 24 hs
          Thread.sleep(90 * 1000);//90: cada un minuto y medio

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

