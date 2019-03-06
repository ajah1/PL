import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalizadorLexico {

	private static final char EOF = (char)-1;
	RandomAccessFile _entrada;
	int _fila 		= 1;
	int _columna 	= 1;
	int _pos 		= 0;
	int _tipo		= 1;
	String _lexema 	= "";
	int [] _finales =  {3, 6, 7, 9, 10, 11, 12, 13, 15, 16, 19, 23, 24, 25};
	int [] _finalRol = {3, 10, 19, 23, 24, 25};
	
	public AnalizadorLexico(RandomAccessFile p_entrada) {
		_entrada = p_entrada;
	}
	
	public boolean esFinal (int p_nuevo) {
		for (int f : _finales) {
			if (p_nuevo == f) return true;
		}
		return false;
	}
	
	public Token siguienteToken() {
		int nuevo;
		int estado = 1;
		char c = leerCaracter();
		++_pos;
		do {
			nuevo = delta(estado,c);
			if (nuevo == -1) {
				//System.out.println("ENCUENTRA UN -1 CON EL CARACTER--->" +c+ "<--");
				if (errorLexico(c) == false) {
					return new Token(_fila, _columna, Character.toString(c), Token.EOF);
				} else {
					c = leerCaracter();
					estado = 1;
					nuevo = delta(estado, c);
				}
			}
			else if (esFinal(nuevo)) {
				estado = nuevo;
				devolverCaracteres(nuevo, c);
				Token t = new Token(_fila, (_columna - _lexema.length()), _lexema, _tipo);
				_lexema = "";
				return t;
			} else {
				if (nuevo == 1) _lexema = "";
				else 			_lexema += c;
				
				estado = nuevo;
				c = leerCaracter();
				++_columna;
				++_pos;
			}
		} while (true);
	}
	
    public void rollBack(){
        try {
            --_pos;
            _entrada.seek(_pos);
            
        } catch (IOException ex) {
            Logger.getLogger(AnalizadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public boolean esFinalRol (int p_nuevo) {
		for (int f : _finalRol) {
			if (p_nuevo == f) return true;
		}
		return false;
	}
	
	public void devolverCaracteres(int e, char c) {
		// Hacer rollback en los estados finales que lo necesitan
		if (esFinalRol(e)) {
			rollBack();
			if (e == 25) // Necesita dos rollback
				rollBack();
		}
		
		_columna++;
		_tipo = Token.nombreToken.indexOf(c);
		
		// Obtener el lexema
		if (e == 24)  		_lexema=_lexema.substring(0, _lexema.length());
		else if (e == 25)  	_lexema=_lexema.substring(0, _lexema.length()-1);
		else if (e == 6 || e == 7 || e == 9 || e == 11 || e == 12 || 
				e == 13 || e == 15 || e == 16 || e == 17) {
			_lexema += c;
		}
	}
	
	public String obtenerLexema (int tipo) {
		if (tipo == 19 || tipo == 23 || tipo == 24) {
			return _lexema.substring(0, _lexema.length());
		} else if (tipo == 25){
			return _lexema.substring(0, _lexema.length());
		} else {
			return Token.nombreToken.get(tipo);
		}
	}
	
	public int palabraReservada(String p_lexema) {
		String l = p_lexema.toLowerCase();
		switch (l) {
			case "div": return Token.OPMUL;
			case "mod": return Token.OPMUL;
			default: return Token.ID;
		}
	}

	
	public boolean errorLexico (char c) {
		if (c == ' ' || c == '\t') {
			++_columna;
			++_pos;
		} else if (c ==  '\n') {
			_columna = 1;
			++_pos;
			++_fila;
		} else if (c == (char)-1){
			return false;
		} else {
            System.err.println ("Error lexico (" +_fila+ "," 
            					+_columna+ "): caracter '" 
            					+c+ "' incorrecto");
            System.exit(-1);
		}
		return true;
	}
	
	public int delta (int p_estado, int c) {
		switch (p_estado) {
		// Estado inicial
		case 1:
			if (c=='(') return 2;
			else if (c==')') return 6;
			else if (c==',') return 7;
			else if (c==':') return 8;
			else if (c=='[') return 11;
			else if (c==']') return 12;
			else if (c==';') return 13;
			else if (c=='.') return 14;
			else if (c=='+' || c=='-') return 16;
			else if (c=='*' || c=='/') return 17;
			else if ((c>='a' && c<='z')||(c>='A'&&c<='Z')) return 18;
			else if (c>='0' && c<='9') return 20;
			else  return -1;
		// Estados con más de una transición
		case 2: if (c=='*') return 4;
				else 		return 3;
		case 4: if (c=='*') return 5;
				else		return 4;
		case 5: if (c==')') return 1;
				else		return 5;
		case 8: if (c=='=') return 9;
				else return 10;
		case 14: if (c=='.') 	return 15;
				 else 			return -1;
		case 18: 
			if ((c>='a' && c<='z')||(c>='A'&&c<='Z')) return 18;
			else if (c>='0' && c<='9') return 18;
			else  return 19;
		case 20: if (c>='0' && c<='9') return 20;
				 if (c=='.') return 21;
				 else return 24;
		case 21: if (c>='0' && c<='9') return 22;
				 else return 25;
		case 22: if (c>='0' && c<='9') return 22;
				 else return 23;
		// Estados de aceptación
		case 3:  return -1;
		case 6:  return -1;
		case 7:  return -1;
		case 9:  return -1;
		case 10: return -1;
		case 11: return -1;
		case 12: return -1;
		case 13: return -1;
		case 15: return -1;
		case 16: return -1;
		case 19: return -1;
		case 23: return -1;
		case 24: return -1;
		case 25: return -1;
		default: return -1;
		}
	}
	
    public char leerCaracter() {
        char currentChar;
        try {
            currentChar = (char)_entrada.readByte();
            return currentChar;
        } catch (EOFException e) {
                return EOF; // constante estática de la clase
        } catch (IOException e) { }
        return ' ';
    }
    
}