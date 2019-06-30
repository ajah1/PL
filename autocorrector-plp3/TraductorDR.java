
public class TraductorDR {

	boolean _flag = false;
	AnalizadorLexico _lexico = null;
	Token _token = null;
	StringBuilder _reglas;
	
	public TraductorDR(AnalizadorLexico p_al) {
		_lexico = p_al;
		_flag = false;
		_token = _lexico.siguienteToken();
		_reglas = new StringBuilder();
	}
	
	boolean anyadido = false;
	boolean anyadido2 = false;
	private final int ERRYADECL=1,ERRNOSIMPLE=2,ERRNODECL=3,ERRTIPOS=4,
			ERRNOENTEROIZQ=5,ERRNOENTERODER=6,ERRRANGO=7; 
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public final String S() {
		if (_token.tipo == Token.PROGRAM) {
			TablaSimbolos tsimb = null;
			addR(S);
			e(Token.PROGRAM);
			e(Token.ID);
			e(Token.PYC);
			return "int main()\n"+B(tsimb);
		} else {
			es(Token.PROGRAM);
		}
		return "S";
	}
	public final String D(TablaSimbolos tsimb) {
		if (_token.tipo == Token.VAR) {
			addR(D);
			e(Token.VAR);
			String trad_l = L(tsimb);
			e(Token.ENDVAR);
			return trad_l + "\n";
		} else {
			es(Token.VAR);
		}
		return "D";
	}
	public final String L(TablaSimbolos tsimb) {
		//System.out.println("ENTRA EN L");
		if (_token.tipo == Token.ID) {
			addR(L);
			return V(tsimb) + Lp(tsimb);
		} else {
			es(Token.ID);
		}
		return "L";
	}
	public final String Lp(TablaSimbolos tsimb) {	//////// EPSILON ////////
		if (_token.tipo == Token.ID) {
			addR(LP1);
			return V(tsimb) + Lp(tsimb);
		} else if (_token.tipo == Token.ENDVAR) {
			// REGLA PARA VACIO
			addR(LP2);
			return "";
		} else {
			es(Token.ID, Token.ENDVAR);
		}
		return "Lp";
	}
	public final String V(TablaSimbolos tsimb) {
		//System.out.println("ENTRA EN V");
		if (_token.tipo == Token.ID) {
			Atributos at = new Atributos();
			Token token_id = new Token(_token);
			String lexema_id = _token.getLexema();
			addR(V);
			e(Token.ID);
			e(Token.DOSP);
			String trad_c = C(at);
			e(Token.PYC);
			
			
			if (tsimb.buscarAmbito(new Simbolo(lexema_id, -1, ""))) {
				errorSemantico(ERRYADECL, token_id);
			}
			
			if (trad_c.contains("[")) {
				tsimb.anyadir(new Simbolo(lexema_id, Simbolo.ARRAY, "campoDejarVacio"));
			} else {
				tsimb.anyadir(new Simbolo(lexema_id, ObtenerTipo(at.tipo), "campoDejarVacio"));	
			}
			return at.tipo + at.punteros + " " 
				/*tsimb.mutar(lexema_id) +*/ 
				+ tsimb.mutar(lexema_id)
				+ lexema_id 
				+ trad_c + ";\n";
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
			if (lim_inf > lim_sup) {
				errorSemantico(ERRRANGO, _token);
			}
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
	public final String B(TablaSimbolos tsimb) {
		if (_token.tipo == Token.BEGIN ) {
			TablaSimbolos ambitoHijo = null;
			if (tsimb == null) {
				tsimb = new TablaSimbolos(null);
				ambitoHijo = tsimb;
			} else {
				ambitoHijo = new TablaSimbolos(tsimb);
			}
			
			addR(B);
			e(Token.BEGIN);
			String trad_d = D(ambitoHijo);
			String trad_si = SI(ambitoHijo);
			e(Token.END);
			return "{\n" + trad_d + trad_si+"}\n";
		} else {
			es(Token.BEGIN);
		}
		return "SI";
	}
	public final String SI(TablaSimbolos tsimb) {
		if (_token.tipo == Token.ID 
				|| _token.tipo == Token.WRITE 
				|| _token.tipo == Token.BEGIN) {
			addR(SI);
			String trad_i = I(tsimb);
			String trad_m = M(tsimb);
			anyadido2 = false;
			return trad_i + trad_m;
		} else {
			es(Token.ID, Token.WRITE, Token.BEGIN);
		}
		return "SI";
	}
	public final String M(TablaSimbolos tsimb) {		//////// EPSILON ////////
		//System.out.println("M");
		if (_token.tipo == Token.PYC) {
			addR(M1);
			e(Token.PYC);
			return I(tsimb) + M(tsimb);
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
	
	public final String I(TablaSimbolos tsimb) {
		if (_token.tipo == Token.ID) {
			Token token_id = new Token(_token);
			String lexema_id = _token.lexema;
			addR(I1);
			e(Token.ID);
			Token token_asig = new Token(_token);
			e(Token.ASIG);
			
			String mutar = "";
			// Esta declarada en el ámbito
			if (tsimb.buscarAmbito(new Simbolo(lexema_id, -1, ""))) {
				mutar = tsimb.mutar(lexema_id);
			} else {
				mutar = tsimb.mutarVar(lexema_id);
			}
			Atributos at = new Atributos();
			Simbolo s_id = tsimb.buscar(lexema_id);
			if (s_id.tipo == Simbolo.ARRAY) {
				errorSemantico(ERRNOSIMPLE, token_id);
			}
			at.tipoAcumulado = TraducirTipo(s_id.tipo);
			at.esAsig = true;
			String trad_e = E(tsimb, at)+";\n";
			
			if (TraducirTipo(s_id.tipo).contentEquals("i") && at.tipoAcumulado.contentEquals("r")) {
				errorSemantico(ERRTIPOS, token_asig);
			}
			
			return "  "
			+ mutar	// Para los _
			+ lexema_id.toLowerCase() 
			+ " = " + trad_e;
		} else if (_token.tipo == Token.WRITE) {
			addR(I2);
			e(Token.WRITE);
			e(Token.PARI);
			Atributos at = new Atributos();
			String trad_e = E(tsimb, at);
			e(Token.PARD);
			String tt = "f";
			if (at.tipoAcumulado.contentEquals("i")) {
				tt = "d";
			}
			return "  printf(\"%"+tt+"\","+trad_e+");\n";
		} else if (_token.tipo == Token.BEGIN) {
			addR(I3); 
			return B(tsimb);
		} else {
			es(Token.ID,Token.BEGIN, Token.WRITE);
		}
		return "I";
	}
	
	public final String E(TablaSimbolos tsimb, Atributos at) {
		if (_token.tipo == Token.NUMENTERO 
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			at.t_traduccion = "";
			String tipo_id = at.tipoAcumulado;
			addR(E);
			String trad_t = T(tsimb, at);
			String trad_ep = Ep(tsimb, at);
			
			if (trad_ep.isEmpty() && at.esAsig) {
				if (tipo_id.contentEquals("r") && at.tipoAcumulado.contentEquals("i")) {
					at.t_traduccion =  "itor("+at.f_lexema+")";
				}
			} else if (!trad_ep.isEmpty()) {
				if (!at.antesAgrupado) {
					if (tipo_id.contentEquals("r") && at.tipoAcumulado.contentEquals("i")) {
						at.t_traduccion =  "itor("+at.t_traduccion+")";
					}
				}
			}
			
			return at.t_traduccion;
			//return trad_t + trad_ep;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "E";
	}
	
	public final String Ep(TablaSimbolos tsimb, Atributos at) {	//////// EPSILON ////////
		if (_token.tipo == Token.OPAS) {
			String lexema_operacion = _token.lexema;
			addR(EP1);
			e(Token.OPAS);
			
			String sufijo = "";
			
			/// GUARDAR E -> T
			Atributos at_copia = new Atributos(at);
			
			at.esUnSoloValor = true; // Es necsario reinicializarlo 
			at.t_traduccion = "";
			String trad_t = T(tsimb, at);
			
			//sufijo = "r";
			String atct = at_copia.t_traduccion;
			if (at_copia.esUnSoloValor && at.esUnSoloValor) {
				if (at_copia.tipoAcumulado.contentEquals(at.tipoAcumulado)) {
					sufijo = at_copia.tipoAcumulado;
				} else {
					sufijo = "r";
					if (at_copia.tipoAcumulado.contentEquals("i")) {
						atct = "itor(" + atct + ")";
						at.antesAgrupado = true;
					} else {
						 at.t_traduccion = "itor(" +  at.t_traduccion + ")";
						 at.antesAgrupado = true;
					}
					at.tipoAcumulado = "r";
				}
			} else if (!at_copia.esUnSoloValor && at.esUnSoloValor) {
				sufijo = at_copia.tipoAcumulado;
				at.antesAgrupado = false;
			} else if (at_copia.esUnSoloValor && !at.esUnSoloValor) {
				sufijo = at_copia.tipoAcumulado;
				at.antesAgrupado = false;
			}
			
			at.t_traduccion = atct + " " + lexema_operacion + sufijo+" "
					+ at.t_traduccion;
			
			String trad_ep = Ep(tsimb, at);
			
			
			return " "+lexema_operacion+" "+ trad_t + trad_ep;
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
	
	public final String T(TablaSimbolos tsimb, Atributos at) {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO
				|| _token.tipo == Token.NUMREAL
				|| _token.tipo == Token.ID) {
			addR(T);
			String trad_f = F(tsimb, at);
			String trad_tp = Tp(tsimb, at);
			return trad_f + trad_tp;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID, Token.OPMUL);
		}
		return "T";
	}
	
	public final String Tp(TablaSimbolos tsimb, Atributos at) {	//////// EPSILON ////////
		if (_token.tipo == Token.OPMUL) {
			Token token_opmul = new Token(_token);
			at.esUnSoloValor = false;
			at.vieneDeMul = true;
			String lexema_operacion = traducirOperacion(_token.lexema);
			addR(TP1);
			e(Token.OPMUL);
			
			// Antes de llamar a F y que pise f_tipo hay que guardarlo
			String f_tipo = at.f_tipo;
			String f_lexema = at.f_lexema;
			
			String trad_f = F(tsimb, at);
			
			
			if (lexema_operacion.contentEquals("%")) {
				
				if (!at.antesAgrupado) {
					if (f_tipo.contentEquals("r")) {
						errorSemantico(ERRNOENTEROIZQ, token_opmul);
					}
				}
				
				// Primera preparar operaciones
				if (f_tipo.contentEquals("i") && at.f_tipo.contentEquals("i") && at.tipoAcumulado.contentEquals("r")) {					
					at.t_traduccion = at.t_traduccion + 
							"itor("+f_lexema+ " " + lexema_operacion + " " + at.f_lexema + ")";
					at.tipoAcumulado = "r";
					at.antesAgrupado = true;
				}
				// Ver si es encesario itor
			} else if (lexema_operacion.contentEquals("/") || lexema_operacion.contentEquals("*")){
				// No podemos agrupos con el anterior para respetar el orden de las operaciones
				
				if (at.antesAgrupado) {
					String sufijo = "r";
					if (!at.tipoAcumulado.contentEquals(at.f_tipo)) {		// Distinto tipo, el int se pasa a float
						at.tipoAcumulado = sufijo;
						at.antesAgrupado = true;
						if (at.f_tipo.contentEquals("i")) {	// Operando derecho es el int
							at.t_traduccion = at.t_traduccion + 
									" "+lexema_operacion + sufijo+" itor(" + at.f_lexema + ")";
						} //else {	// Operando izquierdo es int por tanto algun tipo de ERROR}
					// Mismo tipo, añadir el sufijo y ya está
					} else {
						if (at.f_tipo.contentEquals("i")) { sufijo = "i"; }
						
						at.t_traduccion = at.t_traduccion + 
								" "+lexema_operacion + sufijo+ " "+at.f_lexema;
					}
				} else {
					
					if (lexema_operacion.contentEquals("/")) {
						if (at.f_tipo.contentEquals("r")) {
							errorSemantico(ERRNOENTERODER, token_opmul);
						}
					}
					
					String sufijo = "r";
					if (f_tipo.contentEquals(at.f_tipo) && f_tipo.contentEquals("i")) {
						sufijo = "i";
						if (at.tipoAcumulado.contentEquals("r") && at.tipoAcumulado.contentEquals("r")) {
							at.t_traduccion = at.t_traduccion +
									"itor("+
									f_lexema + " "+lexema_operacion + sufijo+ " "+at.f_lexema
									+")";
						}
					}
				}
			}
			
			String trad_tp = Tp(tsimb, at);
			
			return  " " + traducirOperacion(lexema_operacion) +" "+ trad_f + trad_tp;
			
		} else if (_token.tipo == Token.OPAS
				|| _token.tipo == Token.PARD
				|| _token.tipo == Token.PYC
				|| _token.tipo == Token.END) {
			// REGLA VACIO
			addR(TP2);
			at.vieneDeMul = false;
			if (!at.esUnSoloValor) {
				
			} else {
				at.t_traduccion = at.t_traduccion + at.f_lexema; 
				at.tipoAcumulado = at.f_tipo;
				at.esUnSoloValor = true;
			}

			return "";
		} else {
			es(Token.PYC, Token.END, Token.PARD, Token.OPAS, Token.OPMUL);
		}
		return "Tp";
	}
	
	public final String F(TablaSimbolos tsimb, Atributos at) {		//////// EPSILON ////////
		if (_token.tipo == Token.NUMENTERO) {
			String lexema_id = _token.lexema;
			addR(F1);
			e(Token.NUMENTERO);
			at.f_tipo = "i";
			at.f_lexema = lexema_id;
			return lexema_id;
		} else if (_token.tipo == Token.NUMREAL) {
			String lexema_id = _token.lexema;
			addR(F2);
			e(Token.NUMREAL);
			at.f_tipo = "r";
			at.f_lexema = lexema_id;
			return lexema_id;
		} else if (_token.tipo == Token.ID) {
			String lexema_id = _token.lexema;
			Token token_id = new Token(_token);
			addR(F3);
			e(Token.ID);
			
			Simbolo s_id = tsimb.buscar(lexema_id);
			if (s_id == null) {
				errorSemantico(ERRNODECL, token_id);
			}
			
			at.f_tipo = TraducirTipo(s_id.tipo);
			at.f_lexema = tsimb.mutarVar(lexema_id)+lexema_id.toLowerCase();
			return at.f_lexema;
		} else {
			es(Token.NUMENTERO, Token.NUMREAL, Token.ID);
		}
		return "F";
	}
	
	public String TraducirTipo(int tipo) {
		switch(tipo) {
		case Simbolo.ENTERO: return "i";
		case Simbolo.REAL: return "r";
		default: return "ERROTRADUCIRTIPO";
		}
	}
	
	public int ObtenerTipo(String tipo) {
		switch (tipo) {
		case "int": return Simbolo.ENTERO;
		case "float": return Simbolo.REAL;
		default: return -1;
		}
	}
	
	public String traducirOperacion (String operacion)  {
		switch(operacion.toLowerCase()) {
		case "mod": return "%";
		case "div": return "/";
		default: return operacion;
		}
	}
	
  private void errorSemantico(int nerror,Token tok) {
    System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
		switch (nerror) {
		  case ERRYADECL: System.err.println("ya existe en este ambito");
		     break;
		  case ERRNOSIMPLE: System.err.println("debe ser de tipo entero o real");
		     break;
		  case ERRNODECL: System.err.println("no ha sido declarado");
		     break;
		  case ERRTIPOS: System.err.println("tipos incompatibles entero/real");
		     break;
		  case ERRNOENTEROIZQ: System.err.println("el operando izquierdo debe ser entero");
		     break;
		  case ERRNOENTERODER: System.err.println("el operando derecho debe ser entero");
		     break;
		  case ERRRANGO: System.err.println("rango incorrecto");
		         break;
    }
    System.exit(-1);
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