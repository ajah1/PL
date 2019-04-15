

public class TraductorDR {
	
	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
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
	
	public int obtenerTipo (String token_tipo) {
		switch(token_tipo) {
		case "int": return Simbolo.ENTERO;
		case "float": 	return Simbolo.REAL;
		case "array": 	return Simbolo.ARRAY;
		case "pointer": return Simbolo.PUNTERO;
		default: return -1000;
		}
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
			Simbolo s = new Simbolo(taux.lexema, -1000, "tradvacia");
			if (!p_tabla.buscarAmbito(s)) {
				p_tabla.anyadir(new Simbolo(tlexema, -1000,"tradvacia"));
			} else {
				errorSema(ERRYADECL, taux);
			}
			
			e(Token.PYC);
			//System.out.println("id: " + tlexema);
			if (ctrad.length == 1)
				return ctrad[0] + " " + tlexema +  ";\n";
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
	public final String I(TablaSimbolos p_tabla) {
		/*
		 * I −→ id asig E 
		 * I −→ write pari E pard 
		 * I −→ B 
		*/ 
		if (_token.tipo == Token.ID) {
			String tid = _token.lexema;
			Token token_aux = new Token(_token);
			addR(I1);
			e(Token.ID);
			e(Token.ASIG);
			if (p_tabla.buscar(tid)==null) {
				errorSema(this.ERRNODECL, token_aux);
			}
			return "  " + tid.toLowerCase() + " = " + E(p_tabla) + ";";
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			String etrad = E(p_tabla);
			e(Token.PARD);
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
			//System.out.println("@@@" + ttrad + eptrad + "@@@");
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
			String ftrad = F();
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
			String ftrad = F();
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
		System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
		
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