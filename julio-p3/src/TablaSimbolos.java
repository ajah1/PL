
import java.util.ArrayList;

// OJO: case insensitive (estilo Pascal)

public class TablaSimbolos {

   public TablaSimbolos padre;
   public ArrayList<Simbolo> simbolos;
   
   
   public TablaSimbolos(TablaSimbolos padre)
   {
      this.padre = padre;
      simbolos = new ArrayList<Simbolo>();
   }

   public boolean buscarAmbito(Simbolo s)
   {
     for (Simbolo ss:simbolos)
       if (ss.nombre.equalsIgnoreCase(s.nombre))
          return true;
     return false;
   }
   
   public boolean anyadir(Simbolo s)
   {
     if (buscarAmbito(s))  // repetido en el ámbito
       return false;
     simbolos.add(s);
     return true;
   }
   
   Simbolo buscar(String nombre)
   {
     for (Simbolo s:simbolos)
       if (s.nombre.equalsIgnoreCase(nombre)) return s;
       
     if (padre != null)
       return padre.buscar(nombre);
     else
       return null;
   }
   
   String mutar(String nombre) {
	   if (padre != null) {
		   return "_" + padre.mutar(nombre);
	   } else {
		   return "";
	   }
   }
   
   
   int contarAmbitos() {
	   if (padre != null) {
		   return 1 + padre.contarAmbitos();	   
	   } else {
		   return 1;
	   }
   }
   
   int numeroDeAmbito(String nombre) {
	   Simbolo s = new Simbolo(nombre, 0, "");
	   if (buscarAmbito(s)) {
		   return 1;
	   } else if (padre != null) {
		   return 1 + padre.numeroDeAmbito(nombre);
	   } else {
		   return 0;
	   }
   }
   
   String mutarVar(String nombre) {
	   int ambitos_totales = contarAmbitos();
	   int ambitos_encontrada = numeroDeAmbito(nombre);
	   
	   String mutar = "";
	   int mutaciones = (ambitos_totales - ambitos_encontrada);
	   for (int i = 0; i < mutaciones; ++i) {
		   mutar += "_";
	   }
	   return mutar;
   }
   
   
   
  /* String mutarVar(String nombre) {
	   Simbolo s = new Simbolo(nombre, 0, "");
	   if (buscarAmbito(s)) {
		   return "";
	   } else if (padre != null) {
		   return "_" + padre.mutarVar(nombre);
	   } else {
		   return "";
	   }
   }
   */

}
