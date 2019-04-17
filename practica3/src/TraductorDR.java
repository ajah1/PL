import java.util.ArrayList;

public class TraductorDR {
	
	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
	ArrayList<Token> _asigI = null;
	
	public TraductorDR(AnalizadorLexico p_al) {
		_lexico = p_al;
		_flag = true;
		_token = _lexico.siguienteToken();
		_reglas = new StringBuilder();
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final String S() { // S −→ program id pyc B
		if (_token.tipo == Token.PROGRAM) {
			addR(S);
			e(Token.PROGRAM);
			e(Token.ID);
			e(Token.PYC);
			return "int main()\n" + B(null);
		} else {
			errorSint(Token.PROGRAM);
		}
		return "S";
	}
	public final String D(TablaSimbolos p_tabla) { // D −→ var L endvar
		if (_token.tipo == Token.VAR) {
			addR(D);
			e(Token.VAR);
			String ltrad = L(p_tabla);
			e(Token.ENDVAR);
			return ltrad + "\n";
		} else {
			errorSint(Token.VAR);
		}
		return "D";
	}
	
	public final String L(TablaSimbolos p_tabla) { // L −→ V Lp 
		//System.out.println("ENTRA EN L");
		if (_token.tipo == Token.ID) {
			addR(L);
			return V(p_tabla) + Lp(p_tabla);
		} else {
			errorSint(Token.ID);
		}
		return "L";
	}
	public final String Lp(TablaSimbolos p_tabla) {	//////// EPSILON //////// Lp −→ V Lp¡
		if (_token.tipo == Token.ID) {
			addR(LP1);
			return V(p_tabla) + Lp(p_tabla);
		} else if (_token.tipo == Token.ENDVAR) {
			// REGLA PARA VACIO
			addR(LP2);
			return "";
		} else {
			errorSint(Token.ID, Token.ENDVAR);
		}
		return "Lp";
	}
	
	public final int obtenerTipoSimbolo (String p_tipo) {
		switch (p_tipo) {
		case "int":
			return Token.NUMENTERO;
		case "float":
			return Token.NUMREAL;
		case "array":
			return Token.ARRAY;
		case "*":
			return Token.POINTER;
		default:
			return -1000;
		}
	}
	
	public final String V(TablaSimbolos p_tabla) { // V −→ id dosp C pyc 
		//System.out.println("ENTRA EN V");
		if (_token.tipo == Token.ID) {
			Token taux = new Token(_token);
			String tlexema = _token.lexema;
			addR(V);
			e(Token.ID);
			e(Token.DOSP);
			String [] ctrad = C().split("@");
			
			//if (p_tabla.buscar(tlexema)==null) {
			int tipoSimbolo = obtenerTipoSimbolo(ctrad[0]);
			Simbolo s = new Simbolo(taux.lexema, tipoSimbolo, "tradvacia");
			if (!p_tabla.buscarAmbito(s)) {
				p_tabla.anyadir(s);
			} else {
				errorSema(ERRYADECL, taux);
			}
			
			e(Token.PYC);
			
			if (ctrad.length == 1)
				return  ctrad[0] + " " +tlexema +  ";\n";
			else {
				String out = ctrad[0] + " "+ tlexema;// + ctrad[1] +  
				for (int i = ctrad.length-1 ; i >= 1; --i) out += ctrad[i];
				return out + ";\n";
			}
				
		} else {
			errorSint(Token.ID);
		}
		return "V";
	}
	public final String C() {
		// C −→ A C 
		// C −→ P
		if (_token.tipo == Token.ARRAY) {
			addR(C1);
			String atrad = A();
			String ctrad = C();
			//System.out.println("A:  " + atrad + "  C:  " + ctrad);
			
			return ctrad +"@"+ atrad;
		} else if (_token.tipo == Token.POINTER 
				|| _token.tipo == Token.INTEGER
				|| _token.tipo == Token.REAL) {
			addR(C2);
			return P();
		} else {
			errorSint(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL );
		}
		return "C";
	}
	public final String A() { // A −→ array cori R cord of 
		if (_token.tipo == Token.ARRAY) {
			addR(A);
			e(Token.ARRAY);
			e(Token.CORI);
			String rtrad = R();
			e(Token.CORD);
			e(Token.OF);
			return rtrad;
		} else {
			errorSint(Token.ARRAY);
		}
		return "A";
	}
	public final String R() { // R −→ G Rp 
		if (_token.tipo == Token.NUMENTERO) {
			addR(R);
			String gtrad = G();
			String rptrad =  Rp();
			//System.out.println("R -> G: " + gtrad + " " + rptrad);
			return gtrad +rptrad;
		} else {
			errorSint(Token.NUMENTERO);
		}
		return "R";
	}
	public final String Rp() {	//////// EPSILON //////// Rp −→ coma G Rp 
		if (_token.tipo == Token.COMA) {
			addR(RP1);
			e(Token.COMA);
			//G();
			//Rp();
			//return gtrad + rptrad;
			return G() + Rp();
		} if (_token.tipo == Token.CORD) {
			// REGLA PARA VACIO
			addR(RP2);
			return "";
		} else {
			errorSint(Token.CORD, Token.COMA);
		}
		return "Rp";
	}
	public final String G() { // G −→ numentero ptopto numentero
		if (_token.tipo == Token.NUMENTERO) {
			String nizq = _token.lexema;
			addR(G);
			e(Token.NUMENTERO);
			e(Token.PTOPTO);
			String nder = _token.lexema;
			e(Token.NUMENTERO);
			int izq = Integer.parseInt(nizq);
			int der = Integer.parseInt(nder);
			Integer res = der - izq + 1;
			//System.out.println(izq + " " + der);
			return "["+String.valueOf(res)+"]";
		} else {
			errorSint(Token.NUMENTERO);
		}
		return "G";
	}
	public final String P() {
		// P −→ pointer of P 
		// P −→ Tipo 
		if (_token.tipo == Token.POINTER) {
			addR(P1);
			e(Token.POINTER);
			e(Token.OF);
			return  P() + "*";
		} else if (_token.tipo == Token.INTEGER || _token.tipo == Token.REAL) {
			addR(P2);
			return Tipo();
		} else {
			errorSint(Token.POINTER, Token.INTEGER, Token.REAL);
		}
		return "P";
	}
	public final String Tipo() {
		if (_token.tipo == Token.INTEGER ) {
			addR(TIPO1);
			e(Token.INTEGER);
			return "int";
		} else if (_token.tipo == Token.REAL) {
			addR(TIPO2);
			e(Token.REAL);
			return "float";
		} else {
			errorSint(Token.INTEGER, Token.REAL);
		}
		return "Tipo";
	}
	public final String B(TablaSimbolos p_tabla) { // B −→ begin D SI end
		TablaSimbolos tabla = new TablaSimbolos(p_tabla); 
		//if (p_tabla != null)
			//tabla = new TablaSimbolos(null);
		if (_token.tipo == Token.BEGIN ) {
			addR(B);
			e(Token.BEGIN);
			String dtrad = D(tabla);
			String sitrad = SI(tabla);
			e(Token.END);
			return "{\n" + dtrad + sitrad + "\n}";
		} else {
			errorSint(Token.BEGIN);
		}
		return "B";
	}
	public final String SI(TablaSimbolos p_tabla) { // SI −→ I M 
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			addR(SI);
			return I(p_tabla) + M(p_tabla);
		} else {
			errorSint(Token.ID, Token.WRITE, Token.BEGIN);
		}
		return "SI";
	}
	public final String M(TablaSimbolos p_tabla) {		//////// EPSILON //////// M −→ pyc I M 
		//System.out.println("M");
		if (_token.tipo == Token.PYC) {
			addR(M1);
			e(Token.PYC);
			String itrad = I(p_tabla);
			String mtrad = M(p_tabla);
			String out = "\n" + itrad;
				out += mtrad;
			return out;
		} if (_token.tipo == Token.END) {
			// REGLA VACIO
			addR(M2);
			return "";
		} else {
			errorSint(Token.PYC, Token.END);
		}
		return "M";
	}
	
	// anyadir si es necesario los itor()
	// devuelve el tipo de la asignacion
	// true => integer 
	/*
	 * 1. Asignacion de un solo token (otra variable)
	 */
	public final void procesarAux(Token p_token, 
			TablaSimbolos p_tabla,
			StringBuilder p_s) {
		
		// añadir el ;
		if (!_asigI.get(_asigI.size()-1).lexema.contentEquals(";")) {
			Token taux = new Token();
			taux.lexema = ";";
			taux.tipo =  Token.PYC;
			_asigI.add(taux);
		}
		
		int tipoVariable = p_tabla.buscar(p_token.lexema).tipo;
		
		boolean parar = false;
		int i = 0;
		
		// Casos para cuando la asginacion se iguala a una variable
		if (_asigI.size() <= 2) {
			int ttoken = p_tabla.buscar(p_token.lexema).tipo;
			
			int tasig;
			if (_asigI.get(0).tipo == Token.ID) {
				tasig = p_tabla.buscar(_asigI.get(0).lexema).tipo;
			} else {
				tasig = _asigI.get(0).tipo;
			}
			
			
			if (ttoken == Token.NUMREAL && tasig == Token.NUMENTERO) {
				p_s.append("itor("+_asigI.get(0).lexema+");");
			} else if (ttoken == Token.NUMENTERO && tasig == Token.NUMREAL) {
				errorSema(ERRTIPOS, p_token);
			}
			return;
		}
		
		// Tremenda basura :D que estoy haciendo
		Token tizq 			= _asigI.get(i);
		Token toperando 	= _asigI.get(i+1);
		Token tder 			= _asigI.get(i+2);
		int opizq = _asigI.get(i).tipo;
		int opder = _asigI.get(i+2).tipo;
		
		int tipo_exp = -200;
		
		// Si son un variable (ID) obtener el tipo de la tabla de simbolos
		if (opizq == Token.ID) opizq = p_tabla.buscar(tizq.lexema).tipo;
		if (opder == Token.ID) opder = p_tabla.buscar(tder.lexema).tipo;
		// Procesar la expresion "izq operando der"
		if (opizq == opder) {	// 7 mod 2
			tipo_exp = Token.NUMENTERO;
			if (opizq != tipoVariable) {
				tipo_exp = Token.NUMREAL;
				p_s.append("itor(" + tizq.lexema 
						+ " " +toperando.lexema
						+ " " +tder.lexema + ")");
			}
		}
		// Caso en que solo tiene una expresion
		if (i+3 == _asigI.size()-1) {
			p_s.append(";");
			return;
		}
		i+=4;
		
		while (!parar) {
			
			toperando = _asigI.get(i-1);
			tder = _asigI.get(i);
			opder = _asigI.get(i).tipo;
			if (tipo_exp == Token.NUMREAL) {
				tipo_exp = Token.NUMREAL;
				if (opder == Token.NUMENTERO) {
					p_s.append(" " +toperando.lexema+"r "+"itor("+tder.lexema+")");
				} else { // es Real opera Real
					p_s.append(" " +toperando.lexema+"r "+tder.lexema);
				}

			}
			
			// llegamos al final??
			if (i+1 == _asigI.size()-1) break;
			
			i+=2;
		}
		p_s.append(";");
	}
	
	// El toke almacena la información de la variable
	public final String procesarAsigI(Token p_token, TablaSimbolos p_tabla) {
		StringBuilder s = new StringBuilder();
		procesarAux(p_token, p_tabla, s);
		
		_asigI.clear();
		_asigI = null;
		
		return s.toString();
	}
	
	public final String I(TablaSimbolos p_tabla) {
		/*
		 * I −→ id asig E 
		 * I −→ write pari E pard 
		 * I −→ B 
		*/ 
		if (_asigI == null)
			_asigI = new ArrayList<Token>(); 
		
		if (_token.tipo == Token.ID) {
			String tid = _token.lexema;
			Token token_aux = new Token(_token);
			addR(I1);
			e(Token.ID);
			e(Token.ASIG);
			if (p_tabla.buscar(tid)==null) {
				errorSema(this.ERRNODECL, token_aux);
			}
			E(p_tabla);
			String procesado = procesarAsigI(token_aux, p_tabla);
			String g = "";//p_tabla.mutar(tid);
			return "  "+ g+tid.toLowerCase() + " = " + procesado;
			
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			String etrad = E(p_tabla);
			e(Token.PARD);
			_asigI.clear();
			_asigI = null;
			return "  printf(" + etrad + ");";
		} else if (_token.tipo == Token.BEGIN) {
			addR(I3);
			return B(p_tabla);
		} else {
			errorSint(Token.ID,Token.BEGIN, Token.WRITE);
		}
		return "I";
	}
	

	
	public final String E(TablaSimbolos p_tabla) { // E −→ T Ep 
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(E);
			String ttrad = T(p_tabla);
			String eptrad = Ep(p_tabla);
	
			return ttrad + eptrad;
		} else {
			errorSint(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "E";
	}
	public final String Ep(TablaSimbolos p_tabla) {	//////// EPSILON //////// Ep −→ opas T Ep 
		if (_token.tipo == Token.OPAS) {
			String tlexema = _token.lexema;
			//Token token_aux = new Token (_token);
			
			addR(EP1);
			e(Token.OPAS);
			
			String ttrad = T(p_tabla);
			String eptrad = Ep(p_tabla);
			String out = tlexema + " " + ttrad;
			
			if (eptrad != "" && eptrad != " ") 
				out += " " + eptrad;
			return out;
		} else if (_token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(EP2);
			return "";
		} else {
			errorSint(Token.OPAS, Token.PARD, Token.PYC, Token.END);
		}
		return "Ep";
	}
	public final String T(TablaSimbolos p_tabla) {	// T −→ F Tp 
		if (_token.tipo == Token.NUMENTERO
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			Token token_aux = new Token(_token);
			addR(T);
			if (!_token.lexema.contains("end")) {
				if (_token.lexema.toLowerCase().contains("mod"))
					_token.lexema = "%";
				else if (_token.lexema.toLowerCase().contains("div"))
					_token.lexema = "/";
				_asigI.add(_token);
			}
			String ftrad = F();
			if (!_token.lexema.contains("end")) {
				if (_token.lexema.toLowerCase().contains("mod"))
					_token.lexema = "%";
				else if (_token.lexema.toLowerCase().contains("div"))
					_token.lexema = "/";
				_asigI.add(_token);
			}
			String tptrad = Tp(p_tabla);
			String out = ftrad; //+" "+ tptrad;
			
			Character c = ftrad.charAt(0);
			if (Character.isLetter(c) && p_tabla.buscar(token_aux.lexema)==null) {
				errorSema(ERRNODECL, token_aux);
			}
			
			if (tptrad != "" && tptrad != " ")
				out += " "+ tptrad;
			return out;
		} else {
			errorSint(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
		return "T";
	}
	public final String Tp(TablaSimbolos p_tabla) {	//////// EPSILON //////// Tp −→ opmul F Tp 
		if (_token.tipo == Token.OPMUL) {
			//System.out.println("Tp->" +_token.lexema);
			String tlexema = _token.lexema;
			addR(TP1);
			e(Token.OPMUL);
			if (!_token.lexema.contains("end")) {
				if (_token.lexema.toLowerCase().contains("mod"))
					_token.lexema = "%";
				else if (_token.lexema.toLowerCase().contains("div"))
					_token.lexema = "/";
				_asigI.add(_token);
			}
			String ftrad = F();
			if (!_token.lexema.contains("end")) {
				if (_token.lexema.toLowerCase().contains("mod"))
					_token.lexema = "%";
				else if (_token.lexema.toLowerCase().contains("div"))
					_token.lexema = "/";
				_asigI.add(_token);
			}
			String tptrad = Tp(p_tabla);
			String out = tlexema + " " + ftrad; //+ " " + tptrad;
			if (tptrad != "")
				out += " " + tptrad;
			return  out;
		} else if (_token.tipo == Token.OPAS
				|| _token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(TP2);
			return "";
		} else {
			errorSint(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
		}
		return "Tp";
	}
	public final String F() {
		/*
		 * F −→ numentero 
		 * F −→ numreal 
		 * F −→ id
		 */
		if (_token.tipo == Token.NUMENTERO) {
			String trad = _token.lexema;
			addR(F1);
			e(Token.NUMENTERO);
			return trad;
		} else if (_token.tipo == Token.NUMREAL) {
			String trad = _token.lexema;
			addR(F2);
			e(Token.NUMREAL);
			return trad;
		} else if (_token.tipo == Token.ID) {
			String trad = _token.lexema;
			addR(F3);
			e(Token.ID);
			return trad;
		} else {
			errorSint(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "F";
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	private final int ERRYADECL=1,ERRNOSIMPLE=2,ERRNODECL=3,ERRTIPOS=4,ERRNOENTEROIZQ=5,ERRNOENTERODER=6,ERRRANGO=7;
	private void errorSema(int nerror,Token tok)
	{
		int columna = tok.columna;
		if (nerror == ERRTIPOS) columna+=2;
		System.err.print("Error semantico ("+tok.fila+","+columna+"): en '"+tok.lexema+"', ");
		
		switch (nerror) {
		  case ERRYADECL: System.err.println("ya existe en este ambito"); break;
		  case ERRNOSIMPLE: System.err.println("debe ser de tipo entero o real"); break;
		  case ERRNODECL: System.err.println("no ha sido declarado"); break;
		  case ERRTIPOS: System.err.println("tipos incompatibles entero/real"); break;
		  case ERRNOENTEROIZQ: System.err.println("el operando izquierdo debe ser entero"); break;
		  case ERRNOENTERODER: System.err.println("el operando derecho debe ser entero"); break;
		  case ERRRANGO: System.err.println("rango incorrecto"); break;
		}

		System.exit(-1);
	}

	
	public final void e(int tokEsperado) {
		//System.out.print("EMPAREJAR:-->" +tokEsperado);
		if (_token.tipo == tokEsperado) {
			_token = _lexico.siguienteToken();
			//System.out.println("--->" +_token.lexema+ "<---");	
		}
		else {
			errorSint(tokEsperado);
		}
	}

	public void errorSint(int ... args) {
		//System.out.println("ERROR SINTACTICO");
        if (_token.tipo == Token.EOF) {
            System.err.print(
            	"Error sintactico: encontrado fin de fichero, esperaba");
        } else {
            System.err.print("Error sintactico "
            	+"("+_token.fila+"," +_token.columna+ "):"
            	+" encontrado '" +_token.lexema+ "', esperaba");
        }
        
        for (int i = 0; i < args.length; ++i) {
            _token.tipo=args[i];
            System.err.print(" "+ _token.toString());
        }
        System.err.print(" \n");
        
		System.exit(-1);
	}

	public void comprobarFinFichero() {
        if (_token.tipo != Token.EOF)
        	errorSint(Token.EOF);
        if(_flag)
            System.out.println(_reglas);
	}
	
	public void addR (int p_r) {
		//System.out.println("AÑADE LA REGLA-->" +p_r+ "<--" +_token.lexema);
		_reglas.append(" ").append(p_r);
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static final int
		S 		= 1,
		D 		= 2,
		L 		= 3,
		LP1 	= 4,
		LP2 	= 5,
		V 		= 6,
		C1 		= 7,
		C2 		= 8,
		A 		= 9,
		R 		= 10,
		RP1 	= 11,
		RP2 	= 12,
		G 		= 13,
		P1 		= 14,
		P2 		= 15,
		TIPO1 	= 16,
		TIPO2 	= 17,
		B 		= 18,
		SI = 19,
		M1 = 20,
		M2 = 21,
		I1 = 22,
		I2 = 23,
		I3 = 24,
		E  = 25,
		EP1 = 26,
		EP2 = 27,
		T 	= 28,
		TP1 = 29,
		TP2 = 30,
		F1 	= 31,
		F2 	= 32,
		F3 	= 33;
}