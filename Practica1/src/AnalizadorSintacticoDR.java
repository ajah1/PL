
public class AnalizadorSintacticoDR {

	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
	public AnalizadorSintacticoDR(AnalizadorLexico p_al) {
		_lexico = p_al;
		_flag = true;
		_token = _lexico.siguienteToken();
		_reglas = new StringBuilder();
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final void S() {
		if (_token.tipo == Token.PROGRAM) {
			addR(S);
			e(Token.PROGRAM);
			e(Token.ID);
			e(Token.PYC);
			B();
		} else {
			es(Token.PROGRAM);
		}
	}
	public final void D() {
		if (_token.tipo == Token.VAR) {
			addR(D);
			e(Token.VAR);
			L();
			e(Token.ENDVAR);
		} else {
			es(Token.VAR);
		}
	}
	public final void L() {
		//System.out.println("ENTRA EN L");
		if (_token.tipo == Token.ID) {
			addR(L);
			V();
			Lp();
		} else {
			es(Token.ID);
		}
	}
	public final void Lp() {	//////// EPSILON ////////
		if (_token.tipo == Token.ID) {
			addR(LP1);
			V();
			Lp();
		} else if (_token.tipo == Token.ENDVAR) {
			// REGLA PARA VACIO
			addR(LP2);
		} else {
			es(Token.ID, Token.ENDVAR);
		}
	}
	public final void V() {
		//System.out.println("ENTRA EN V");
		if (_token.tipo == Token.ID) {
			addR(V);
			e(Token.ID);
			e(Token.DOSP);
			C();
			e(Token.PYC);
		} else {
			es(Token.ID);
		}
	}
	public final void C() {
		if (_token.tipo == Token.ARRAY) {
			addR(C1);
			A();
			C();
		} else if (_token.tipo == Token.POINTER 
				|| _token.tipo == Token.INTEGER
				|| _token.tipo == Token.REAL) {
			addR(C2);
			P();
		} else {
			es(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL );
		}
	}
	public final void A() {
		if (_token.tipo == Token.ARRAY) {
			addR(A);
			e(Token.ARRAY);
			e(Token.CORI);
			R();
			e(Token.CORD);
			e(Token.OF);
		} else {
			es(Token.ARRAY);
		}
	}
	public final void R() {
		if (_token.tipo == Token.NUMENTERO) {
			addR(R);
			G();
			Rp();
		} else {
			es(Token.NUMENTERO);
		}
	}
	public final void Rp() {	//////// EPSILON ////////
		if (_token.tipo == Token.COMA) {
			addR(RP1);
			e(Token.COMA);
			G();
			Rp();
		} if (_token.tipo == Token.CORD) {
			// REGLA PARA VACIO
			addR(RP2);
		} else {
			es(Token.CORD, Token.COMA);
		}
	}
	public final void G() {
		if (_token.tipo == Token.NUMENTERO) {
			addR(G);
			e(Token.NUMENTERO);
			e(Token.PTOPTO);
			e(Token.NUMENTERO);
		} else {
			es(Token.NUMENTERO);
		}
	}
	public final void P() {		//////// EPSILON ////////
		if (_token.tipo == Token.POINTER) {
			addR(P1);
			e(Token.POINTER);
			e(Token.OF);
			P();
		} else if (_token.tipo == Token.INTEGER || _token.tipo == Token.REAL) {
			addR(P2);
			Tipo();
		} else {
			es(Token.POINTER, Token.INTEGER, Token.REAL);
		}
	}
	public final void Tipo() {
		if (_token.tipo == Token.INTEGER ) {
			addR(TIPO1);
			e(Token.INTEGER);
		} else if (_token.tipo == Token.REAL) {
			addR(TIPO2);
			e(Token.REAL);
		} else {
			es(Token.INTEGER, Token.REAL);
		}
	}
	public final void B() {
		if (_token.tipo == Token.BEGIN ) {
			addR(B);
			e(Token.BEGIN);
			D();
			SI();
			e(Token.END);
		} else {
			es(Token.BEGIN);
		}
	}
	public final void SI() {
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			addR(SI);
			I();
			M();
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
	}
	public final void M() {		//////// EPSILON ////////
		//System.out.println("M");
		if (_token.tipo == Token.PYC) {
			addR(M1);
			e(Token.PYC);
			I();
			M();
		} if (_token.tipo == Token.END) {
			// REGLA VACIO
			addR(M2);
		}else {
			es(Token.PYC, Token.END);
		}
	}
	public final void I() {
		if (_token.tipo == Token.ID) {
			addR(I1);
			e(Token.ID);
			e(Token.ASIG);
			E();
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			E();
			e(Token.PARD);
		} else if (_token.tipo == Token.BEGIN) {
			addR(I3);
			B();
		} else {
			es(Token.ID,Token.BEGIN, Token.WRITE);
		}
	}
	public final void E() {
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(E);
			T();
			Ep();
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
	}
	public final void Ep() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPAS) {
			addR(EP1);
			e(Token.OPAS);
			T();
			Ep();
		} else if (_token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(EP2);
		} else {
			es(Token.OPAS, Token.PARD, Token.PYC, Token.END);
		}
	}
	public final void T() {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(T);
			//e(Token.OPMUL);
			F();
			Tp();
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
	}
	public final void Tp() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPMUL) {
			addR(TP1);
			e(Token.OPMUL);
			F();
			Tp();
		} else if (_token.tipo == Token.OPAS
				|| _token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(TP2);
		} else {
			es(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
		}
	}
	public final void F() {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO) {
			addR(F1);
			e(Token.NUMENTERO);
		} else if (_token.tipo == Token.NUMREAL) {
			addR(F2);
			e(Token.NUMREAL);
		} else if (_token.tipo == Token.ID) {
			addR(F3);
			e(Token.ID);
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
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












