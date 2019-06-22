
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
	
	boolean anyadido = false;
	boolean anyadido2 = false;
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final String S() {
		if (_token.tipo == Token.PROGRAM) {
			addR(S);
			e(Token.PROGRAM);
			e(Token.ID);
			e(Token.PYC);
			return "int main()\n"+B();
		} else {
			es(Token.PROGRAM);
		}
		return "S";
	}
	public final String D() {
		if (_token.tipo == Token.VAR) {
			addR(D);
			e(Token.VAR);
			String trad_l = L();
			e(Token.ENDVAR);
			return trad_l;
		} else {
			es(Token.VAR);
		}
		return "D";
	}
	public final String L() {
		//System.out.println("ENTRA EN L");
		if (_token.tipo == Token.ID) {
			addR(L);
			return V() + Lp();
		} else {
			es(Token.ID);
		}
		return "L";
	}
	public final String Lp() {	//////// EPSILON ////////
		if (_token.tipo == Token.ID) {
			addR(LP1);
			return V() + Lp();
		} else if (_token.tipo == Token.ENDVAR) {
			// REGLA PARA VACIO
			addR(LP2);
			return "";
		} else {
			es(Token.ID, Token.ENDVAR);
		}
		return "Lp";
	}
	public final String V() {
		//System.out.println("ENTRA EN V");
		if (_token.tipo == Token.ID) {
			Atributos at = new Atributos();
			String lexema_id = _token.getLexema();
			addR(V);
			e(Token.ID);
			e(Token.DOSP);
			String trad_c = C(at);
			e(Token.PYC);
			return at.tipo + at.punteros + " " + lexema_id + trad_c + ";\n";
		} else {
			es(Token.ID);
		}
		return "V";
	}
	public final String C(Atributos at) {
		if (_token.tipo == Token.ARRAY) {
			addR(C1);
			return A() + C(at);
			
		} else if (_token.tipo == Token.POINTER 
				|| _token.tipo == Token.INTEGER
				|| _token.tipo == Token.REAL) {
			addR(C2);
			P(at);
			return "";
		} else {
			es(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL );
		}
		return "C";
	}
	public final String A() {
		if (_token.tipo == Token.ARRAY) {
			addR(A);
			e(Token.ARRAY);
			e(Token.CORI);
			String trad_r = R();
			e(Token.CORD);
			e(Token.OF);
			return trad_r;
		} else {
			es(Token.ARRAY);
		}
		return "A";
	}
	public final String R() {
		if (_token.tipo == Token.NUMENTERO) {
			addR(R);
			String trad_g = G();
			String trad_rp = Rp();
			anyadido = false;
			return trad_g + trad_rp;
		} else {
			es(Token.NUMENTERO);
		}
		return "R";
	}
	public final String Rp() {	//////// EPSILON ////////
		if (_token.tipo == Token.COMA) {
			addR(RP1);
			e(Token.COMA);
			return G() + Rp();
		} else if (_token.tipo == Token.CORD) {
			// REGLA PARA VACIO
			if (anyadido == false) { 
				//System.out.println("añade la regla 12");
				addR(RP2);
				anyadido = true;
			}
			return "";
		} else {
			es(Token.CORD, Token.COMA);
		}
		return "Rp";
	}
	public final String G() {
		if (_token.tipo == Token.NUMENTERO) {
			addR(G);
			int lim_inf = Integer.parseInt(_token.lexema);
			e(Token.NUMENTERO);
			e(Token.PTOPTO);
			int lim_sup = Integer.parseInt(_token.lexema);
			e(Token.NUMENTERO);
			return "["+ (lim_sup-lim_inf+1) +"]";
		} else {
			es(Token.NUMENTERO);
		}
		return "G";
	}
	public final String P(Atributos at) {		//////// EPSILON ////////
		if (_token.tipo == Token.POINTER) {
			addR(P1);
			e(Token.POINTER);
			e(Token.OF);
			at.punteros += "*";
			P(at);
			return "";
		} else if (_token.tipo == Token.INTEGER || _token.tipo == Token.REAL) {
			addR(P2);
			at.tipo = Tipo();
			return "";
		} else {
			es(Token.POINTER, Token.INTEGER, Token.REAL);
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
			es(Token.INTEGER, Token.REAL);
		}
		return "Tipo";
	}
	public final String B() {
		if (_token.tipo == Token.BEGIN ) {
			addR(B);
			e(Token.BEGIN);
			String trad_d = D();
			String trad_si = SI();
			e(Token.END);
			return "{\n" + trad_d + trad_si+"}\n";
		} else {
			es(Token.BEGIN);
		}
		return "SI";
	}
	public final String SI() {
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			addR(SI);
			String trad_i = I();
			String trad_m = M();
			anyadido2 = false;
			return trad_i + trad_m;
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
		return "SI";
	}
	public final String M() {		//////// EPSILON ////////
		//System.out.println("M");
		if (_token.tipo == Token.PYC) {
			addR(M1);
			e(Token.PYC);
			return I() + M();
		} if (_token.tipo == Token.END) {
			// REGLA VACIO
			//addR(M2);
			if (anyadido2 == false) { 
				addR(M2);
				anyadido2 = true;
			}
			return "";
		}else {
			es(Token.PYC, Token.END);
		}
		return "M";
	}
	public final String I() {
		if (_token.tipo == Token.ID) {
			String lexema_id = _token.lexema;
			addR(I1);
			e(Token.ID);
			e(Token.ASIG);
			return "  " + lexema_id + " = " + E()+";\n";
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			String trad_e = E();
			e(Token.PARD);
			return "  printf("+trad_e+");\n";
		} else if (_token.tipo == Token.BEGIN) {
			addR(I3);
			return B();
		} else {
			es(Token.ID,Token.BEGIN, Token.WRITE);
		}
		return "I";
	}
	public final String E() {
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(E);
			return T() + Ep();
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "E";
	}
	public final String Ep() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPAS) {
			String lexema_operacion = _token.lexema;
			addR(EP1);
			e(Token.OPAS);
			return " "+lexema_operacion+" "+ T() + Ep();
		} else if (_token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(EP2);
			return "";
		} else {
			es(Token.OPAS, Token.PARD, Token.PYC, Token.END);
		}
		return "Ep";
	}
	public final String T() {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(T);
			//e(Token.OPMUL);
			return F() + Tp();
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
		return "T";
	}
	public final String Tp() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPMUL) {
			String lexema_operacion = _token.lexema;
			addR(TP1);
			e(Token.OPMUL);
			String trad_f = F();
			String trad_tp = Tp();
			if (trad_tp == "") {
				return trad_f + trad_tp;
			} else {
				return  " " + lexema_operacion +" "+ trad_f + trad_tp;
			}
		} else if (_token.tipo == Token.OPAS
				|| _token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(TP2);
			return "";
		} else {
			es(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
		}
		return "Tp";
	}
	public final String F() {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO) {
			String lexema_id = _token.lexema;
			addR(F1);
			e(Token.NUMENTERO);
			return lexema_id;
		} else if (_token.tipo == Token.NUMREAL) {
			String lexema_id = _token.lexema;
			addR(F2);
			e(Token.NUMREAL);
			return lexema_id;
		} else if (_token.tipo == Token.ID) {
			String lexema_id = _token.lexema;
			addR(F3);
			e(Token.ID);
			return lexema_id;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "F";
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final void e(int tokEsperado) {
		//System.out.print("EMPAREJAR:-->" +tokEsperado);
		if (_token.tipo == tokEsperado) {
			_token = _lexico.siguienteToken();
			//System.out.println("--->" +_token.lexema+ "<---");	
		}
		else {
			es(tokEsperado);
		}
	}

	public void es(int ... args) {
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
        	es(Token.EOF);
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












