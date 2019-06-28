
public class Atributos {
	public String tipo;
	public String punteros;
	public String lexema;
	
	
	public String tipoAcumulado;
	public String f_tipo;
	public String f_lexema;
	
	public Atributos() {
		tipo = "tipoVacio";
		lexema = "lexemaVacio";
		punteros = "";
		
		// Al final se usará en Ep y para comprobar si es necesaria conversión con el id
		tipoAcumulado = "";
		f_tipo = "";
		f_lexema = "";
	}
}
