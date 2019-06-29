
public class Atributos {
	public String tipo;
	public String punteros;
	public String lexema;
	
	
	public String tipoAcumulado;
	public String f_tipo;
	public String f_lexema;
	public String t_traduccion;
	
	public boolean antesAgrupado;
	public boolean vieneDeMul;
	public boolean esUnSoloValor;
	public boolean esAsig;
	
	public Atributos() {
		tipo = "tipoVacio";
		lexema = "lexemaVacio";
		punteros = "";
		
		// Al final se usará en Ep y para comprobar si es necesaria conversión con el id
		tipoAcumulado = "";
		t_traduccion = "tradVacia";
		f_tipo = "";
		f_lexema = "";
		antesAgrupado = false;
		vieneDeMul = false;
		esUnSoloValor = true;
		esAsig = false;
	}
}
