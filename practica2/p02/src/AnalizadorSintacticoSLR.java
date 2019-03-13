
public class AnalizadorSintacticoSLR {

	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
	public AnalizadorSintacticoSLR(AnalizadorLexico p_al) {
		_lexico = p_al;
		_flag = true;
		_token = _lexico.siguienteToken();
		_reglas = new StringBuilder();
	}
	
	
	///////////////////////////////////////////////////////////////
	// Predicciones
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
		if (_token.tipo == Token.ID) {
			addR(L);
			V();
			Lp();
		} else {
			es(Token.ID);
		}
	}
	public final void V() {
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
	final void F() {
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
	
	public void analizar () {
		
	}
	
	public void addR (int p_r) {
		//System.out.println("AÑADE LA REGLA-->" +p_r+ "<--" +_token.lexema);
		_reglas.append(" ").append(p_r);
	}
	
	///////////////////////////////////////////////////////////////
	// Coódigo de la regla
	///////////////////////////////////////////////////////////////
	public static final int
		S 		= 1,
		D 		= 2,
		L 		= 3,
		V 		= 4,
		TIPO1 	= 5,
		TIPO2 	= 6,
		B 		= 7,
		SI1 	= 8,
		SI2 	= 9,
		I1 		= 10,
		I2 		= 11,
		I3 		= 12,
		E1  	= 13,
		E2  	= 14,
		F1 		= 15,
		F2 		= 16,
		F3 		= 17;
}












