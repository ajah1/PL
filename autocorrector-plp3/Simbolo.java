
public class Simbolo {

  public static final int ENTERO=1, REAL=2, ARRAY=3, PUNTERO=4;


  public String nombre;
  public int tipo;        
  public String nomtrad;
  
  
  public Simbolo(String nombre,int tipo,String nomtrad)
  {
    this.nombre = nombre;
    this.tipo = tipo;
    this.nomtrad = nomtrad;
  }


  public Simbolo(Simbolo s_id) {
		this.nombre = s_id.nombre;
		this.tipo = s_id.tipo;
		this.nomtrad = s_id.nomtrad;
	}
  
}
