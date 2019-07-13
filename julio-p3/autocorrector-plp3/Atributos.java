
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
	public boolean aux;
	
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
	
	public Atributos(Atributos a) {
		tipoAcumulado = a.tipoAcumulado;
		t_traduccion = a.t_traduccion;
		f_tipo = a.f_tipo;
		f_lexema = a.f_lexema;
		antesAgrupado = a.antesAgrupado;
		vieneDeMul = a.vieneDeMul;
		esUnSoloValor = a.esUnSoloValor;
		esAsig = a.esAsig;
	}
}
