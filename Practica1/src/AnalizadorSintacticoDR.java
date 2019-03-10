
public class AnalizadorSintacticoDR {

	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
	public AnalizadorSintacticoDR(AnalizadorLexico p_al) {
		_lexico = p_al;
		_flag = true;
		_token = _lexico.siguienteToken();

	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final void S() {
		if (_token.tipo == Token.PROGRAM) {
			e(Token.PROGRAM);
			e(Token.ID);
			e(Token.PYC);
			B();
			addR(S);
		} else {
			es(Token.PROGRAM);
		}
	}
	public final void D() {
		if (_token.tipo == Token.VAR) {
			e(Token.VAR);
			L();
			e(Token.ENDVAR);
			addR(D);
		} else {
			es(Token.VAR);
		}
	}
	public final void L() {
		if (_token.tipo == Token.ID) {
			V();
			Lp();
			addR(L);
		} else {
			es(Token.ID);
		}
	}
	public final void Lp() {	//////// EPSILON ////////
		if (_token.tipo == Token.ID) {
			V();
			Lp();
			addR(LP1);
		} else if (_token.tipo == Token.ENDVAR) {
			// REGLA PARA VACIO
			addR(LP1);
		} else {
			es(Token.ID, Token.ENDVAR);
		}
	}
	public final void V() {
		if (_token.tipo == Token.ID) {
			e(Token.ID);
			e(Token.DOSP);
			C();
			e(Token.PYC);
			addR(V);
		} else {
			es(Token.ID);
		}
	}
	public final void C() {
		if (_token.tipo == Token.ARRAY) {
			A();
			C();
			addR(C1);
		} else if (_token.tipo == Token.POINTER 
				|| _token.tipo == Token.INTEGER
				|| _token.tipo == Token.REAL) {
			P();
			addR(C2);
		} else {
			es(Token.ARRAY, Token.POINTER, Token.INTEGER, Token.REAL );
		}
	}
	public final void A() {
		if (_token.tipo == Token.ARRAY) {
			e(Token.ARRAY);
			e(Token.CORI);
			R();
			e(Token.CORD);
			e(Token.OF);
			addR(A);
		} else {
			es(Token.ARRAY);
		}
	}
	public final void R() {
		if (_token.tipo == Token.NUMENTERO) {
			G();
			Rp();
			addR(R);
		} else {
			es(Token.NUMENTERO);
		}
	}
	public final void Rp() {	//////// EPSILON ////////
		if (_token.tipo == Token.COMA) {
			e(Token.COMA);
			G();
			Rp();
			addR(RP1);
		} if (_token.tipo == Token.CORD) {
			// REGLA PARA VACIO
			addR(RP2);
		} else {
			es(Token.COMA, Token.CORD);
		}
	}
	public final void G() {
		if (_token.tipo == Token.NUMENTERO) {
			e(Token.NUMENTERO);
			e(Token.PTOPTO);
			e(Token.NUMENTERO);
			addR(G);
		} else {
			es(Token.NUMENTERO);
		}
	}
	public final void P() {		//////// EPSILON ////////
		if (_token.tipo == Token.POINTER) {
			e(Token.POINTER);
			e(Token.OF);
			P();
			addR(P1);
		} else if (_token.tipo == Token.INTEGER || _token.tipo == Token.REAL) {
			Tipo();
			addR(P2);
		} else {
			es(Token.POINTER, Token.INTEGER, Token.REAL);
		}
	}
	public final void Tipo() {
		if (_token.tipo == Token.INTEGER ) {
			e(Token.INTEGER);
			addR(TIPO1);
		} else if (_token.tipo == Token.REAL) {
			e(Token.REAL);
			addR(TIPO2);
		} else {
			es(Token.INTEGER, Token.REAL);
		}
	}
	public final void B() {
		if (_token.tipo == Token.BEGIN ) {
			e(Token.BEGIN);
			D();
			SI();
			e(Token.END);
			addR(B);
		} else {
			es(Token.BEGIN);
		}
	}
	public final void SI() {
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			I();
			M();
			addR(SI);
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
	}
	public final void M() {		//////// EPSILON ////////
		if (_token.tipo == Token.PYC) {
			e(Token.PYC);
			M();
			I();
			addR(M1);
		} if (_token.tipo == Token.END) {
			// REGLA VACIO
			addR(M2);
		}else {
			es(Token.PYC);
		}
	}
	public final void I() {
		if (_token.tipo == Token.ID) {
			e(Token.ID);
			e(Token.ASIG);
			E();
			addR(I1);
		} else if (_token.tipo == Token.WRITE) {
			e(Token.WRITE);
			e(Token.PARI);
			E();
			e(Token.PARD);
			addR(I2);
		} else if (_token.tipo == Token.BEGIN) {
			B();
			addR(I3);
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
	}
	public final void E() {
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			T();
			Ep();
			addR(E);
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
	}
	public final void Ep() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPAS) {
			e(Token.OPAS);
			T();
			Ep();
			addR(EP1);
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
			e(Token.OPMUL);
			F();
			Tp();
			addR(T);
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
	}
	public final void Tp() {	//////// EPSILON ////////
		if (_token.tipo == Token.OPMUL) {
			e(Token.OPMUL);
			F();
			Tp();
			addR(TP1);
		} else if (_token.tipo == Token.OPAS
				|| _token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(TP2);
		} else {
			es(Token.OPMUL, Token.OPAS, Token.ID, Token.PYC, Token.END);
		}
	}
	public final void F() {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO) {
			e(Token.NUMENTERO);
			addR(F1);
		} else if (_token.tipo == Token.NUMREAL) {
			e(Token.NUMREAL);
			addR(F2);
		} else if (_token.tipo == Token.ID) {
			e(Token.ID);
			addR(F3);
		} else {
			es(Token.NUMREAL, Token.NUMENTERO, Token.ID);
		}
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final void e(int tokEsperado) {
		if (_token.tipo == tokEsperado)
			_token = _lexico.siguienteToken();
		else {
			es(tokEsperado);
		}
	}

	public void es(int ... args) {
        if (_token.tipo == Token.EOF) {
            System.err.print(
            	"Error sintactico: encontrado fin de fichero, esperaba");
        } else {
            System.err.print("Error sintactico"
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












