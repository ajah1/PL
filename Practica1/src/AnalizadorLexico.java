import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AnalizadorLexico {

	private static final char EOF = '$';
	RandomAccessFile _entrada;
	int _fila 		= 1;
	int _columna 	= 1;
	int _pos 		= 0;
	
	public AnalizadorLexico(RandomAccessFile p_entrada) {
		_entrada = p_entrada;
	}
	
	public Token siguienteToken() {
		
		return null;
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
		case 22: if (c>='0' && c<='9') return 23;
				 else return 22;
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
