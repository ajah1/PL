

public class TraductorDR {

	public class Pair<Ti, Td> {
		public  Ti _ti;
		public  Td _td;
		
		public Pair (Ti ti, Td td) {
			_ti = ti;
			_td = td;
		}
	}
	
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
			return "int main()\n" + B();
		} else {
			es(Token.PROGRAM);
		}
		return "S";
	}
	public final String D() { // D −→ var L endvar
		if (_token.tipo == Token.VAR) {
			addR(D);
			e(Token.VAR);
			String ltrad = L();
			e(Token.ENDVAR);
			return ltrad + "\n";
		} else {
			es(Token.VAR);
		}
		return "D";
	}
	public final String L() { // L −→ V Lp 
		//System.out.println("ENTRA EN L");
		if (_token.tipo == Token.ID) {
			addR(L);
			return V() + Lp();
		} else {
			es(Token.ID);
		}
		return "L";
	}
	public final String Lp() {	//////// EPSILON //////// Lp −→ V Lp¡
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
	public final String V() { // V −→ id dosp C pyc 
		//System.out.println("ENTRA EN V");
		if (_token.tipo == Token.ID) {
			String tlexema = _token.lexema;
			addR(V);
			e(Token.ID);
			e(Token.DOSP);
			String [] ctrad = C().split("@");
			e(Token.PYC);
			if (ctrad.length == 1)
				return ctrad[0] + " " + tlexema +  ";\n";
			else {
				String out = ctrad[0] + " "+ tlexema;// + ctrad[1] +  
				for (int i = ctrad.length-1 ; i >= 1; --i) out += ctrad[i];
				return out + ";\n";
			}
				
		} else {
			es(Token.ID);
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
			es(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL );
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
			es(Token.ARRAY);
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
			es(Token.NUMENTERO);
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
			es(Token.CORD, Token.COMA);
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
			es(Token.NUMENTERO);
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
	public final String B() { // B −→ begin D SI end 
		if (_token.tipo == Token.BEGIN ) {
			addR(B);
			e(Token.BEGIN);
			String dtrad = D();
			String sitrad = SI();
			e(Token.END);
			return "{\n" + dtrad + sitrad + "\n}";
		} else {
			es(Token.BEGIN);
		}
		return "B";
	}
	public final String SI() { // SI −→ I M 
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			addR(SI);
			return I() + M();
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
		return "SI";
	}
	public final String M() {		//////// EPSILON //////// M −→ pyc I M 
		//System.out.println("M");
		if (_token.tipo == Token.PYC) {
			addR(M1);
			e(Token.PYC);
			String itrad = I();
			String mtrad = M();
			String out = "\n" + itrad;
				out += mtrad;
			return out;
		} if (_token.tipo == Token.END) {
			// REGLA VACIO
			addR(M2);
			return "";
		} else {
			es(Token.PYC, Token.END);
		}
		return "M";
	}
	public final String I() {
		/*
		 * I −→ id asig E 
		 * I −→ write pari E pard 
		 * I −→ B 
		*/ 
		if (_token.tipo == Token.ID) {
			String tid = _token.lexema;
			addR(I1);
			e(Token.ID);
			e(Token.ASIG);
			return "  " + tid + " = " + E() + ";";
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			String etrad = E();
			e(Token.PARD);
			return "  printf(" + etrad + ");";
		} else if (_token.tipo == Token.BEGIN) {
			addR(I3);
			return B();
		} else {
			es(Token.ID,Token.BEGIN, Token.WRITE);
		}
		return "I";
	}
	public final String E() { // E −→ T Ep 
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(E);
			String ttrad = T();
			String eptrad = Ep();
			//System.out.println("@@@" + ttrad + eptrad + "@@@");
			return ttrad + eptrad;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "E";
	}
	public final String Ep() {	//////// EPSILON //////// Ep −→ opas T Ep 
		if (_token.tipo == Token.OPAS) {
			String tlexema = _token.lexema;
			addR(EP1);
			e(Token.OPAS);
			String ttrad = T();
			String eptrad = Ep();
			//System.out.println("|||"+eptrad+"|||");
			String out = tlexema + " " + ttrad; //+ " " + eptrad;
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
			es(Token.OPAS, Token.PARD, Token.PYC, Token.END);
		}
		return "Ep";
	}
	public final String T() {	// T −→ F Tp 
		if (_token.tipo == Token.NUMENTERO
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(T);
			String ftrad = F();
			String tptrad = Tp();
			String out = ftrad; //+" "+ tptrad;
			if (tptrad != "" && tptrad != " ")
				out += " "+ tptrad;
			return out;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
		return "T";
	}
	public final String Tp() {	//////// EPSILON //////// Tp −→ opmul F Tp 
		if (_token.tipo == Token.OPMUL) {
			//System.out.println("Tp->" +_token.lexema);
			String tlexema = _token.lexema;
			addR(TP1);
			e(Token.OPMUL);
			String ftrad = F();
			String tptrad = Tp();
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
			es(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
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
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "F";
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	private final int ERRYADECL=1,ERRNOSIMPLE=2,ERRNODECL=3,ERRTIPOS=4,ERRNOENTEROIZQ=5,ERRNOENTERODER=6,ERRRANGO=7;
	private void errorS(int nerror,Token tok)
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