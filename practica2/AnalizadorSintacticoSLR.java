import java.util.Stack;

public class AnalizadorSintacticoSLR {
	
	String	[][] _lr;
	int  	[][] _ir;
	AnalizadorLexico _l;
	int _filas 		= 38;
	int _columnas 	= 18;
	Stack<String> _pila, _reglas;
	
	public AnalizadorSintacticoSLR (AnalizadorLexico p_l) {
		_l = p_l;
		_pila = new Stack<String>();
		_reglas = new Stack<String>();
		inicializarTabla();
	}
	
	public String Accion (String p_s, Token p_a) {
		int f = Integer.parseInt(p_s);
		int c = Conv(p_a.tipo);
		return _lr[f][c];
	}
	
	public String Ir_A (String p_p, String p_A) {
		int f = Integer.parseInt(p_p);
		int c = ConvGo(p_A);
		return Integer.toString(_ir[f][c]);
	}
	
	public void analizar () {
		String s = "", aux = "", valor = "";
		String tipo = "", A = "", p = "";
//		push(0)
		_pila.push("0");
//		a := siguienteToken()
		Token a = _l.siguienteToken();
		while (true) {
//			sea s el estado en la cima de la pila
			s = _pila.lastElement();
//			SI Accion[s, a] = dj ENTONCES
			aux = Accion(s, a);
			//System.out.print("aux: " +aux);
			tipo 	= Character.toString(aux.charAt(0));
			valor 	= aux.substring(1,aux.length());
			// valor puede tener mas de un digito
			if (valor == "1") valor += Character.toString(aux.charAt(2));
			
			/*System.out.print("  tipo: " +tipo+ "   valor: " +valor);
			System.out.println("   lexema: " +a.lexema);*/
			if (tipo.equals("s")) {
//				push(j)
				_pila.push(valor);
//				a := siguienteToken()
				a = _l.siguienteToken();
			}
//			SI NO SI Accion[s, a] = rk ENTONCES
			else if (tipo.equals("r")) {
//				PARA i := 1 HASTA Longitud_Parte_Derecha(k) HACER pop()
//				sea A la parte izquierda de la regla k
				A = Desapilar(valor);
//				sea p el estado en la cima de la pila
				p = _pila.lastElement();
//				push(Ir_A[p, A])
				_pila.push(Ir_A(p, A));
			}
//			SI NO SI Accion[s, a] = aceptar ENTONCES
			else if (tipo.contentEquals("$")) {
				break;
			}
			else {
				error(a, s);
			}
		}
		ImprimirReglas();
	}
	
	public void ImprimirReglas() {
		for (int i = _reglas.size()-1; i >= 0; --i) {
			System.out.print(_reglas.get(i) + " ");
		}
	}
	
	public String Desapilar (String p_estado) {
		
		int e = Integer.valueOf(p_estado);
		int pop = 10000;
		
		_reglas.push(p_estado);
		
		if (e == 4||e == 6||e == 7||e == 10||e == 13||
				e == 15||e == 16||e == 17||e == 18) {
			pop = 1;
		} else if (e == 3) {
			pop = 2;
		} else if (e == 2||e == 9||e == 11||e == 14) {
			pop = 3;
		} else if (e == 1||e == 5||e == 8||e == 12){
			pop = 4;
		}
		
		//System.out.println("pila.size(): " +_pila.size() +"  desapilar: " +pop);
		for (int i = 0; i < pop; ++i) { _pila.pop(); }
		
		// Dada la K, devuelve la parte izquierda (el no terminal)
		return ConvIzq(e);
	}
	
	public int Conv (int tipo) {
		switch (tipo) {
		case Token.PROGRAM: return program;
		case Token.ID: 		return id;
		case Token.PYC:		return pyc;
		case Token.VAR: 	return var;
		case Token.ENDVAR: 	return endvar;
		case Token.DOSP: 	return dosp;
		case Token.INTEGER: return integer;
		case Token.REAL: 	return real;
		case Token.BEGIN: 	return begin;
		case Token.END: 	return end;
		case Token.ASIG: 	return asig;
		case Token.WRITE: 	return write;
		case Token.PARI: 	return pari;
		case Token.PARD: 	return pard;
		case Token.OPAS: 	return opas;
		case Token.NUMENTERO: return numentero;
		case Token.NUMREAL: return numreal;
		case Token.EOF: 	return dolar;
		default: 			return 10000;
		}
	}
	public int ConvGo (String left) {
		switch (left) {
		case "S": 	return S;
		case "D": 	return D;
		case "L": 	return L;
		case "V": 	return V;
		case "Tipo": return TIPO;
		case "B": 	return B;
		case "SI": 	return SI;
		case "I": 	return I;
		case "E": 	return E;
		case "F": 	return F;
		default: return 10000;
		}
	}
	public String ConvIzq (int k) {
		switch (k) {
		case 1: return "S";
		case 2: return "D";
		case 3: return "L";
		case 4: return "L";
		case 5: return "V";
		case 6: return "Tipo";
		case 7: return "Tipo";
		case 8: return "B";
		case 9: return "SI";
		case 10: return "SI";
		case 11: return "I";
		case 12: return "I";
		case 13: return "I";
		case 14: return "E";
		case 15: return "E";
		case 16: return "F";
		case 17: return "F";
		case 18: return "F";
		default: return "10000";
		}
	}
	
	public void inicializarTabla () {
		_ir = new int[_filas][10];
		for (int i = 0; i < _filas; ++i) {
			for (int j = 0; j < 10; ++j) {
				_ir[i][j] = 10000;
			}
		}
		_ir[0][S] 	= 1;
		_ir[4][B] 	= 5;
		_ir[6][D] 	= 7;
		_ir[7][B] 	= 13;	_ir[7][SI] = 9;	_ir[7][I] = 10;
		_ir[8][L] 	= 14;	_ir[8][V]  = 15;
		_ir[14][V] 	= 22;
		_ir[18][B] 	= 13;	_ir[18][I] = 24;
		_ir[19][E] 	= 25;	_ir[19][F] = 26;
		_ir[20][E] 	= 30;	_ir[20][F] = 26;
		_ir[23][TIPO] = 31;
		_ir[34][F] 	= 37;
		
		_lr  = new String[_filas][_columnas];
		for (int i = 0; i < _filas; ++i) {
			for (int j = 0; j < _columnas; ++j) {
				_lr[i][j] = "  ";
			}
		}
		_lr[0][program] = "s2";
		_lr[1][dolar] 	= "$$";
		_lr[2][id] 		= "s3";
		_lr[3][pyc] 	= "s4";
		_lr[4][begin] 	= "s6";
		_lr[5][dolar] 	= "r1";
		_lr[6][var] 	= "s8";
		_lr[7][id] 		= "s11";_lr[7][begin] = "s6";_lr[7][write] = "s12";
		_lr[8][id] 		= "s16";
		_lr[9][pyc] 	= "s18";_lr[9][end] = "s17";
		_lr[10][pyc] 	= "r10";_lr[10][end] = "r10";
		_lr[11][asig] 	= "s19";
		_lr[12][pari] 	= "s20";
		_lr[13][pyc] 	= "r13";_lr[13][end] = "r13";
		_lr[14][id] 	= "s16";_lr[14][endvar] = "s21";
		_lr[15][id] 	= "r4";_lr[15][endvar] = "r4";
		_lr[16][dosp] 	= "s23";
		_lr[17][pyc]	= "r8";_lr[17][end] = "r8";_lr[17][dolar] = "r8";
		_lr[18][id]		= "s11";_lr[18][begin] = "s6";_lr[18][write] = "s12";
		_lr[19][id] 	= "s29";_lr[19][numentero] = "s27";_lr[19][numreal] = "s28";
		_lr[20][id]	 	= "s29";_lr[20][numentero] = "s27";_lr[20][numreal] = "s28";
		_lr[21][id] 	= "r2";_lr[21][begin] = "r2";_lr[21][write] = "r2";
		_lr[22][id] 	= "r3";_lr[22][endvar] = "r3";
		_lr[23][integer] = "s32";_lr[23][real] = "s33";
		_lr[24][pyc] 	= "r9";_lr[24][end] = "r9";
		_lr[25][pyc] 	= "r11";_lr[25][end] = "r11";_lr[25][opas] = "s34";
		_lr[26][pyc] 	= "r15";_lr[26][end] = "r15";_lr[26][pard] = "r15";_lr[26][opas] = "r15";
		_lr[27][pyc] 	= "r16";_lr[27][end] = "r16";_lr[27][pard] = "r16";_lr[27][opas] = "r16";
		_lr[28][pyc] 	= "r17";_lr[28][end] = "r17";_lr[28][pard] = "r17";_lr[28][opas] = "r17";
		_lr[29][pyc] 	= "r18";_lr[29][end] = "r18";_lr[29][pard] = "r18";_lr[29][opas] = "r18";
		_lr[30][pard] 	= "s35";_lr[30][opas] = "s34";
		_lr[31][pyc] 	= "s36";
		_lr[32][pyc] 	= "r6";
		_lr[33][pyc] 	= "r7";
		_lr[34][id] 	= "s29";_lr[34][numentero] = "s27";_lr[34][numreal] = "s28";
		_lr[35][pyc] 	= "r12";_lr[35][end] = "r12";
		_lr[36][id] 	= "r5";_lr[36][endvar] = "r5";
		_lr[37][pyc] 	= "r14";_lr[37][end] = "r14";_lr[37][pard] = "r14";_lr[37][opas] = "r14";
	}
	
	public static final int
	// Ir_A indices de las columnas
		S 		= 0,
		D 		= 1,
		L 		= 2,
		V 		= 3,
		TIPO 	= 4,
		B 		= 5,
		SI 		= 6,
		I 		= 7,
		E 		= 8,
		F 		= 9,
	// _lr indices de las columnas
		program 	= 0,
		id 			= 1,
		pyc 		= 2,
		var 		= 3,
		endvar 		= 4,
		dosp 		= 5,
		integer 	= 6,
		real 		= 7,
		begin 		= 8,
		end 		= 9,
		asig 		= 10,
		write 		= 11,
		pari  		= 12,
		pard  		= 13,
		opas 		= 14,
		numentero 	= 15,
		numreal 	= 16,
		dolar 		= 17;
	
	public void printError (Token p_a, int [] esperados) {
		//System.out.println("ERROR SINTACTICO");
        if (p_a.tipo == Token.EOF) {
            System.err.print(
            	"Error sintactico: encontrado fin de fichero, esperaba");
        } else {
            System.err.print("Error sintactico "
            	+"("+p_a.fila+"," +p_a.columna+ "):"
            	+" encontrado '" +p_a.lexema+ "', esperaba");
        }
        for (int i = 0; i < esperados.length; ++i) {
        	p_a.tipo=esperados[i];
            System.err.print(" "+ p_a.toString());
        }
        System.err.print(" \n");
		System.exit(-1);
	}
	
	public void error(Token p_a, String p_s) {
		switch (Integer.parseInt(p_s)) {
		case 0: printError(p_a, new int[]{Token.PROGRAM});
		case 1: printError(p_a, new int[]{Token.EOF});
		case 2: printError(p_a, new int[]{Token.ID});
		case 3: printError(p_a, new int[]{Token.PYC});
		case 4: printError(p_a, new int[]{Token.BEGIN});
		case 5: printError(p_a, new int[]{Token.EOF});
		case 6: printError(p_a, new int[]{Token.VAR});
		case 7: printError(p_a, new int[]{Token.BEGIN, Token.WRITE, Token.ID});
		case 8: printError(p_a, new int[]{Token.ID});
		case 9: printError(p_a, new int[]{Token.PYC, Token.END});
		case 10: printError(p_a, new int[]{Token.PYC, Token.END});
		case 11: printError(p_a, new int[]{Token.ASIG});
		case 12: printError(p_a, new int[]{Token.PARI});
		case 13: printError(p_a, new int[]{Token.PYC, Token.END});
		case 14: printError(p_a, new int[]{Token.ENDVAR, Token.ID});
		case 15: printError(p_a, new int[]{Token.ENDVAR, Token.ID});
		case 16: printError(p_a, new int[]{Token.DOSP});
		case 17: printError(p_a, new int[]{Token.PYC, Token.END, Token.EOF});
		case 18: printError(p_a, new int[]{Token.BEGIN, Token.WRITE, Token.ID});
		case 19: printError(p_a, new int[]{Token.NUMENTERO, Token.NUMREAL, Token.ID});
		case 20: printError(p_a, new int[]{Token.NUMENTERO, Token.NUMREAL, Token.ID});
		case 21: printError(p_a, new int[]{Token.BEGIN, Token.WRITE, Token.ID});
		case 22: printError(p_a, new int[]{Token.ENDVAR, Token.ID});
		case 23: printError(p_a, new int[]{Token.INTEGER, Token.REAL});
		case 24: printError(p_a, new int[]{Token.PYC, Token.END});
		case 25: printError(p_a, new int[]{Token.PYC,  Token.OPAS, Token.END});
		case 26: printError(p_a, new int[]{Token.PARD, Token.PYC,  Token.OPAS, Token.END});
		case 27: printError(p_a, new int[]{Token.PARD, Token.PYC,  Token.OPAS, Token.END});
		case 28: printError(p_a, new int[]{Token.PARD, Token.PYC,  Token.OPAS, Token.END});
		case 29: printError(p_a, new int[]{Token.PARD, Token.PYC,  Token.OPAS, Token.END});
		case 30: printError(p_a, new int[]{Token.PARD, Token.OPAS});
		case 31: printError(p_a, new int[]{Token.PYC});
		case 32: printError(p_a, new int[]{Token.PYC});
		case 33: printError(p_a, new int[]{Token.PYC});
		case 34: printError(p_a, new int[]{Token.NUMENTERO, Token.NUMREAL,Token.ID});
		case 35: printError(p_a, new int[]{Token.PYC, Token.END});
		case 36: printError(p_a, new int[]{Token.ENDVAR, Token.ID});
		case 37: printError(p_a, new int[]{Token.PARD, Token.PYC,  Token.OPAS, Token.END});
		}
	}
	
}
