package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
	List<Piece> piecesOnTheBoard = new ArrayList<>();
	List<Piece> capturedPieces = new ArrayList<Piece>();
	
	public ChessMatch()
	{
		board = new Board(8,8);
		turn = 1;
		currentPlayer = Color.BRANCO;
		initialSetup();
	}
	
	public int getTurn()
	{
		return turn;
	}
	
	public boolean getCheck() 
	{
		return check;
	}
	
	
	public boolean getCheckMate()
	{
		return checkMate;
	}
	
	public Color getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public ChessPiece[][] getPieces()
	{
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		
		for (int i=0; i<board.getRows(); i++)
		{
			for(int j=0; j<board.getColumns(); j++)
			{
				// casting pro ChessPiece 
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition)
	{
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
		
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition)
	{
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		
		Piece capturedPiece = makeMove(source, target);
		
		if (testCheck(currentPlayer))
		{
			undoMove(source, target, capturedPiece);
			throw new ChessException("Voce nao pode se colocar em check!");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false ;
		
		if(testCheckMate(opponent(currentPlayer)))
		{
			checkMate = true;
		} else {
			nextTurn();
		}
		
		
		
		return (ChessPiece)capturedPiece;
	}
	
	
	private void validateSourcePosition(Position position)
	{
		if(!board.thereIsAPiece(position))
		{
			throw new ChessException("Não existe peça na posição de origem!");
		}
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor())
		{
			throw new ChessException("A peca escolhida nao e sua!");
		}
		
		if(!board.piece(position).isThereAnyPossibleMove())
		{
			throw new ChessException("Não existe movimentos possíveis para esta peça!");
		}
	}
	
	private void validateTargetPosition(Position source, Position target)
	{
		if(!board.piece(source).possibleMove(target))
			throw new ChessException("A peca escolhida nao pode se mover nessa posição!");
	}
	
	
	private void nextTurn()
	{
		turn++;
		currentPlayer = (currentPlayer == Color.BRANCO) ? Color.PRETO : Color.BRANCO;
	}
	
	
	private Piece makeMove(Position source, Position target)
	{
		Piece p = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p,  target);
		
		if(capturedPiece != null)
		{
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		return capturedPiece;
		
	}
	
	
	private void undoMove(Position source, Position target, Piece capturedPiece)
	{
		Piece p = board.removePiece(target);
		board.placePiece(p, source);
		
		
		if (capturedPiece != null)
		{
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
	}
	
	
	
	private Color opponent(Color color)
	{
		return (color == Color.BRANCO) ? Color.PRETO : Color.BRANCO;
	}
	
	private ChessPiece king(Color color)
	{
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for ( Piece p : list)
		{
			if(p instanceof King)
			{
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("Não tem um Rei na cor " + color + " no tabuleiro!");
	}
	
	
	private boolean testCheck(Color color)
	{
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces)
		{
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()])
			{
				return true;
			}
		} 
		return false;
	}
	
	
	private boolean testCheckMate(Color color) 
	{
		if (!testCheck(color))
			return false;
		
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list)
		{
			boolean[][] mat = p.possibleMoves();
			for(int i=0; i<board.getRows(); i++)
			{
				for(int j=0; j<board.getColumns(); j++)
				{
					if(mat[i][j])
					{
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck)
							return false;
							
					}
				}
			}
		}
		return true;
	}
	
	
	private void placeNewPiece(char column, int row, ChessPiece piece)
	{
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	
	
	
	
	// metodo responsavel por inciiar a partida colocando as peças no tabuleiro
	private void initialSetup()
	{
		placeNewPiece('h', 7, new Rook(board, Color.BRANCO));
		placeNewPiece('d', 1, new Rook(board, Color.BRANCO));
		placeNewPiece('e', 1, new King(board, Color.BRANCO));
		
		placeNewPiece('b', 8, new Rook(board, Color.PRETO));
		placeNewPiece('a', 8, new King(board, Color.PRETO));
		
		// versao anterior
		// board.placePiece(new King(board , Color.BRANCO), new Position(7,4));
	}
	
}
