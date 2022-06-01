enum BoardCell
{
	EMPTY,
	RED,
	YELLOW
}


public class Board {

		BoardCell[][] grid;
		int width; 
		int height;
		
		public Board(int width, int height)
		{
			this.width = width;
			this.height = height;
			grid = new BoardCell[width][height];
			for(int i = 0; i < width; i++)
			{
				for(int j = 0; j < height; j++)
				{
					grid[i][j] = BoardCell.EMPTY;
				}
			}
		}
		
		public boolean validateMove(int col)
		{
			return grid[col][height-1] == BoardCell.EMPTY;
		}
		
		public boolean dropPiece(int col, BoardCell color)
		{
			if(col >= width || col < 0) {return false;}
			if(!validateMove(col))
			{
				return false;
			}
			int j = 0;
			while(grid[col][j] != BoardCell.EMPTY)
			{
				j++;
			}
			
			grid[col][j] = color;
			
			return true;
		}
		
		public BoardCell checkForWin()
		{
			
			if(checkHorizontal(BoardCell.RED))
			{
				return BoardCell.RED;
			}
			if(checkHorizontal(BoardCell.YELLOW))
			{
				return BoardCell.YELLOW; 
			}
			if(checkVertical(BoardCell.RED))
			{
				return BoardCell.RED;
			}
			if(checkVertical(BoardCell.YELLOW)) 
			{
				return BoardCell.YELLOW; 
			}
			if(checkDiagonal(BoardCell.RED))
			{
				return BoardCell.RED;
			}
			if(checkDiagonal(BoardCell.YELLOW))  
			{
				return BoardCell.YELLOW; 
			}
			
			
			return BoardCell.EMPTY;
			
		}
		
		private boolean checkHorizontal(BoardCell color)
		{
			
			int count = 0;
			for(int j = 0; j < height; j++)
			{
				count = 0;
				for(int i = 0; i < width; i ++)
				{
					if(grid[i][j] == color)
					{
						count++;
					}
					else
					{
						count = 0;
					}
					if(count == 4)
					{
						return true;
					}
				}
			}
			
			return false;
			
		}
		private boolean checkVertical(BoardCell color)
		{
			
			int count = 0;
			for(int i = 0; i < width; i++)
			{
				count = 0;
				for(int j = 0; j < height; j ++)
				{
					if(grid[i][j] == color)
					{
						count++;
					}
					else
					{
						count = 0;
					}
					if(count == 4)
					{
						return true;
					}
				}
			}
			
			return false;
			
		}
		
		private boolean checkDiagonal(BoardCell color)
		{
			
			//left Diagonal
			
			for(int i = 0; i < width - 3; i++)
			{
				for(int j = 0; j < height - 3; j++)
				{
					int count = 0;
					for(int k = 0; k < 4; k++)
					{
						if(grid[i+k][j+k] == color)
						{
							count++;
						}
					}
					if(count == 4)
					{
						return true;
					}
				}
			}
			
			//right Diagonal
			for(int i = 3; i < width; i++)
			{
				for(int j = 0; j < height - 3; j++)
				{
					int count = 0;
					for(int k = 0; k < 4; k++)
					{
						if(grid[i-k][j+k] == color)
						{
							count++;
						}
					}
					if(count == 4)
					{
						return true;
					}
				}
			} 
			
			
			
			return false;
		}
		
	
}
