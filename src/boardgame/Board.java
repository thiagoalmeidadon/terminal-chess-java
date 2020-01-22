package boardgame;

public class Board {
	
	private int rows;
	private int columns;
	private Piece[][] pieces; // matriz de peças 
	
	
	public Board(int rows, int columns) {
		if(rows < 1 || columns < 1)
		{
			throw new BoardException("Erro no tabuleiro");
		}
		
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
		
	}


	public int getRows() {
		return rows;
	}


	public int getColumns() {
		return columns;
	}

	
	public Piece piece(int row, int column)
	{
		// programacao defensiva 
		if(!positionExists(row, column))
		{
			throw new BoardException("Posição fora do tabuleiro");
		}
		return pieces[row][column];
	}
	
	// sobrecarga do método 
	public Piece piece(Position position)
	{
		
		// programacao defensiva 
		if(!positionExists(position))
		{
			throw new BoardException("Posição fora do tabuleiro");
		}
		
		return pieces[position.getRow()][position.getColumn()];
	}
	
	
	public void placePiece(Piece piece, Position position)
	{
		
		if(thereIsAPiece(position))
		{
			throw new BoardException("Já tem uma peça nessa posição!");
		}
		
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	
	public Piece removePiece(Position position)
	{
		if (!positionExists(position))
		{
			throw new BoardException("Posição não existe");
		}
		if (piece(position) == null)
		{
			return null;
		}
		Piece aux = piece(position);
		
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	
	
	private boolean positionExists(int row, int column)
	{
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position)
	{
		return positionExists(position.getRow(), position.getColumn());
	}
	
	public boolean thereIsAPiece(Position position)
	{
		// programacao defensiva 
		if(!positionExists(position))
		{
			throw new BoardException("Posição fora do tabuleiro");
		}
		return piece(position) != null;
	}
	

}
