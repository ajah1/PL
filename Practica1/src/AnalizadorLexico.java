import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AnalizadorLexico {
	
	RandomAccessFile  _fichero;
	static char EOF = '$';
	
	
	public AnalizadorLexico(RandomAccessFile entrada) {
		_fichero = entrada;
	}

	// Codificacion de la tabla de transiciones
	public int delta(int p_estado, int c) {
		
		switch (p_estado) {
		case 1: if (c == ':') return 8; 
				else if ((c >= '0' && c <= '9') || 
						(c >= 'a' && c <= 'z')  ||
						(c >= 'A' && c <= 'Z')) return 2;
				else if (c == '(') return 13;
				else if (c == ')') return 14;
				else if (c == ',') return 15;
				else if (c == '[') return 16;
				else if (c == ']') return 17;
				else if (c == ';') return 18;
				else if (c == '.') return 19;
				else if (c == '-' || c == '+') return 22;
		case 4: if (c >= '0' && c <= '9') return 4;
				else if (c == '.') return 6;
				else return 5;
		case 5: return -1; //////////////////////// MIRAR ESTADO FINAL
		case 6: if (c >= '0' && c <= '9') return 7;
		case 7: if (c >= '0' && c <= '9') return 7;
				else return 12;
		case 8: if (c == '=') return 10;
				else return 9;
		case 9: return -1;//////////////////////// MIRAR ESTADO FINAL
		case 10: return -1; //////////////////////// MIRAR ESTADO FINAL
		case 13: return -1; //////////////////////// MIRAR ESTADO FINAL
		case 14:
		}
		
		return 0;
	}
	
	public char leer() {
		char currentChar;
		
		try {
			currentChar = (char)_fichero.readByte();
			return currentChar;
		} catch (EOFException e) {
			return EOF;   // constante estática de la clase
		}
		catch (IOException e) {  // error lectura
			System.out.println("ERROR LEERCARACTER:" + e);
		}
		return ’ ’;
	}
	
	// leera caracteres de la entrada hasta formar un token y lo devolvera
	public Token siguienteToken() {
		
		int estado = 1;
		int c = leer();
		do {
			int nuevo = delta(estado,c);
			if (nuevo == ERROR) {
				errorLexico(...);
			}
			if (esFinal(nuevo))
			{
				devolverCaracteres(nuevo);
				return tokenAsociado(nuevo);
			} else {
				estado = nuevo;
				c = leer();
			}
		} while (true);
		
		return null;
	}
}
