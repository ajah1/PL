import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

class plp3 {
    public static void main(String[] args) {
      try {
        RandomAccessFile entrada = new RandomAccessFile("entrada.txt","r");
        AnalizadorLexico al = new AnalizadorLexico(entrada);
        TraductorDR tdr = new TraductorDR(al);

        String trad = tdr.S(); // simbolo inicial de la gramatica
        tdr.comprobarFinFichero();
        System.out.println(trad);
      }
      catch (FileNotFoundException e) {
        System.out.println("Error, fichero no encontrado: " + args[0]);
      }
    }
}

