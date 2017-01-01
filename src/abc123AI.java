import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;

public class abc123AI extends CKPlayer {
	
	private int domain;
	private byte opponent;
	
	private final int MAX = Integer.MAX_VALUE;
	private final int MIN = Integer.MIN_VALUE;

	private final int WIN_VAL = 1000;
	private final int LOSE_VAL = -1000;
	
	private final int CANCEL_VAL = -1234567;//cancel value for time
	
	//groups of different length rows, columns, or diagonals
	int[] groups;
	int[] oppGroups;
	
	//Variables for threat spaces
	Point[] threats;
	int threatsSize;
	Point[] oppThreats;
	int oppThreatsSize;
	
	
	public abc123AI(byte player, BoardModel state) {
		super(player, state);
		teamName = "abc123";
		domain=state.getHeight()*state.getWidth();
		opponent = (byte)(player == 1 ? 2 : 1);
	}
	
	//check the surroundings if one of the directions have the space to win
	//return true if one or more directions have the space to win, false otherwise
	private boolean surrounding(BoardModel state, byte pl, int x, int y){
		int tempx = x;
		int tempy = y;
		boolean potential = true;
		
		//check left and right
		int j = 0;
		while (j < state.getkLength()){
			if (tempx >= 0 && tempx < state.getWidth() && tempx - (state.getkLength() - 1) >= 0){
				for(int i = 0;i < state.getkLength() && potential;++i,--tempx){
					if (state.getSpace(tempx, tempy) == pl)
						potential = false;
				}
				if (potential)
					return true;
			}
			potential = true;
			++j;
			tempx = x + j;
			tempy = y;
		}
		
		//check up and down
		tempx = x;
		tempy = y;
		potential = true;
		j = 0;
		while (j < state.getkLength()){
			if (tempy >= 0 && tempy < state.getHeight() && tempy - (state.getkLength() - 1) >= 0){
				for(int i=0;i < state.getkLength() && potential;++i,--tempy){
					if (state.getSpace(tempx, tempy) == pl)
						potential = false;
				}
				if (potential)
					return true;
			}
			potential = true;
			++j;
			tempx = x;
			tempy = y + j;
		}
		
		//check up-left to down-right diagonal
		tempx = x;
		tempy = y;
		potential = true;
		j = 0;
		while (j < state.getkLength()){
			if (tempy >= 0 && tempy < state.getHeight() && tempy - (state.getkLength() - 1) >= 0
					&& tempx >= 0 && tempx < state.getWidth() && state.getWidth() - (tempx + 1) >= state.getkLength()){
				for(int i=0;i < state.getkLength() && potential;++i,--tempy,++tempx){
					if (state.getSpace(tempx, tempy) == pl)
						potential = false;
				}
				if (potential)
					return true;
			}
			potential = true;
			++j;
			tempx = x - j;
			tempy = y + j;
		}
		
		//check up-right to down-left diagonal
		tempx = x;
		tempy = y;
		potential = true;
		j = 0;
		while (j < state.getkLength()){
			if (tempy >= 0 && tempy < state.getHeight() && tempy - (state.getkLength() - 1) >= 0
					&& tempx >= 0 && tempx < state.getWidth() && tempx - (state.getkLength() - 1) >= 0){
				for(int i=0;i < state.getkLength() && potential;++i,--tempy,--tempx){
					if (state.getSpace(tempx, tempy) == pl)
						potential = false;
				}
				if (potential)
					return true;
			}
			potential = true;
			++j;
			tempx = x + j;
			tempy = y + j;
		}
		
		return false;
	}

	private boolean crossingSec(BoardModel state, byte pl, int x, int y){
		int potentials = 0;
		boolean checkP=true;
		
		//initial the temp x & y
		int tempx = x;
		int tempy = y;
		
		//check left
		if (x >= state.getkLength() - 1){
			checkP = true;
			int count = 0;
			--tempx;
			for(int i=0;(i < state.getkLength() - 1) && checkP;++i,--tempx){
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;
		
		//check down-left diagonal
		if (y >= state.getkLength() - 1 && x >= state.getkLength() - 1){
			checkP = true;
			int count = 0;
			--tempy;
			--tempx;
			for(int i = 0;(i < state.getkLength() - 1) && checkP;++i,--tempy,--tempx){
				if (state.getSpace(tempx, tempy) == pl)
					checkP= false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;

		//check down
		if (y >= state.getkLength() - 1) {
			checkP = true;
			int count = 0;
			--tempy;
			for (int i = 0; (i < state.getkLength() - 1) && checkP; ++i, --tempy) {
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte) (pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2) && checkP)
				++potentials;
		}
		
		// change it back
		tempx = x;
		tempy = y;

		// check down-right diagonal
		if (y >= state.getkLength() - 1 && state.getWidth() - (x + 1) >= state.getkLength()) {
			checkP = true;
			int count = 0;
			--tempy;
			++tempx;
			for (int i = 0; (i < state.getkLength() - 1) && checkP; ++i, --tempy, ++tempx) {
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte) (pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2) && checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;
		
		//check right
		if (state.getWidth() - (x + 1) >= state.getkLength()){
			checkP = true;
			int count = 0;
			++tempx;
			for(int i=0;(i < state.getkLength() - 1) && checkP;++i,++tempx){
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;
		
		//check up-right diagonal
		if (state.getHeight() - (y + 1) >= state.getkLength() && 
				state.getWidth() - (x + 1) >= state.getkLength()){
			checkP = true;
			int count = 0;
			++tempy;
			++tempx;
			for(int i = 0;(i < state.getkLength() - 1) && checkP;++i,++tempy,++tempx){
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;
		
		//check up
		if (state.getHeight() - (y + 1) >= state.getkLength()){
			checkP = true;
			int count = 0;
			++tempy;
			for(int i = 0;(i < state.getkLength() - 1) && checkP;++i,++tempy){
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//change it back
		tempx = x;
		tempy = y;
		
		//check up-left diagonal
		if (state.getHeight() - (y + 1) >= state.getkLength() && x >= state.getkLength() - 1){
			checkP = true;
			int count = 0;
			++tempy;
			--tempx;
			for(int i =0;(i < state.getkLength() - 1) && checkP;++i,++tempy,--tempx){
				if (state.getSpace(tempx, tempy) == pl)
					checkP = false;
				if (state.getSpace(tempx, tempy) == (byte)(pl == 1 ? 2 : 1))
					++count;
			}
			if ((count == state.getkLength() - 2)&&checkP)
				++potentials;
		}
		
		//return the result
		if (potentials >= 2)
			return true;
		return false;
	}
	
	//check for all available space
	private Point[] availableSpace(boolean gravity, BoardModel state, boolean maxCalled){
		if (gravity){
			Point[] myPoints = new Point[state.getWidth()];
			int mid = state.getWidth() / 2;
			int limit = state.getWidth() % 2 == 0 ? mid + 1 : mid;
			int count = 1;
			
			myPoints[0] = new Point(mid, state.getHeight() - 1);
			for (int i = 1; i <= limit; ++i){
				if (!(mid - i < 0)){
					myPoints[count] = new Point(mid - i, state.getHeight() - 1);
					++count;
				}
				if (!(mid + i >= state.getWidth())){
					myPoints[count] = new Point(mid + i, state.getHeight() - 1);
					++count;
				}
			}
			return myPoints;
		 }
		 else{
			 int inc = 0;
			 
			 for (int i = 0; i < state.getWidth(); ++i){
				 for (int j = 0; j < state.getHeight(); ++j){
					 boolean add = false;
					 if (state.getSpace(i, j) == 0 && (surrounding(state, player, i, j)||
									 surrounding(state, opponent, i,j))){
						 int tempx = i - 1;
						 int tempy = j + 1;
						 
						 for (int k = 0; k < 3; ++k){
							 for (int l = 0; l < 3; ++l){
								 if ((tempx >= 0 && tempx < state.getWidth()
										 && tempy >= 0 && tempy < state.getHeight()
										 && !(tempy == j && tempx == i)
										 && state.getSpace(tempx, tempy) != 0)){
									add = true;
								 }
								 ++tempx;
							 }
							 --tempy;
							 tempx = i - 1;
						 }
						 if (add || (state.getkLength() >= 3 && crossingSec(state,player, i, j))
								 || (state.getkLength() >= 3 && crossingSec(state,opponent, i, j)))
							 ++inc;
						 
					 }
				 }
			 }
			 
			 Point[] myPoints = new Point[inc];
			 double[] euclids = new double[inc];
			 Point[] finalPoints = new Point[inc];
			 int size = inc;
			 inc = 0;
			 
			 for (int i = 0; i < state.getWidth(); ++i){
				 for (int j = 0; j < state.getHeight(); ++j){
					 boolean add = false;
					 if (state.getSpace(i, j) == 0 && (surrounding(state, player, i, j)||
							 surrounding(state, opponent, i,j))){
						 int tempx = i - 1;
						 int tempy = j + 1;
						 
						 for (int k = 0; k < 3; ++k){
							 for (int l = 0; l < 3; ++l){
								 if ((tempx >= 0 && tempx < state.getWidth()
										 && tempy >= 0 && tempy < state.getHeight()
										 && !(tempy == j && tempx == i)
										 && state.getSpace(tempx, tempy) != 0)){ 
									add = true;
								 }
								 ++tempx;
							 }
							 --tempy;
							 tempx = i - 1;
						 }
						 
						 if (add || (state.getkLength() >= 3 && crossingSec(state,player , i, j))
								 || (state.getkLength() >= 3 && crossingSec(state,opponent, i, j))){
							 myPoints[inc] = new Point(i, j);
							 ++inc;
						 }
					 }
				 }
			 }
			 
			//if no potential available put in no potential spaces
			 if (size <= 0){
				 inc = 0;
				 for (int i = 0; i < state.getWidth(); ++i){
					 for (int j = 0; j < state.getHeight(); ++j){
						 boolean add = false;
						 if (state.getSpace(i, j) == 0){
							 int tempx = i - 1;
							 int tempy = j + 1;
							 
							 for (int k = 0; k < 3; ++k){
								 for (int l = 0; l < 3; ++l){
									 if ((tempx >= 0 && tempx < state.getWidth()
											 && tempy >= 0 && tempy < state.getHeight()
											 && !(tempy == j && tempx == i)
											 && state.getSpace(tempx, tempy) != 0)){
										add = true;
									 }
									 ++tempx;
								 }
								 --tempy;
								 tempx = i - 1;
							 }
							 if (add || (state.getkLength() >= 3 && crossingSec(state,player, i, j))
									 || (state.getkLength() >= 3 && crossingSec(state,opponent, i, j)))
								 ++inc; 
						 }
					 }
				 }
				 
				 myPoints = new Point[inc];
				 euclids = new double[inc];
				 finalPoints = new Point[inc];
				 size = inc;
				 inc = 0;
				 
				 for (int i = 0; i < state.getWidth(); ++i){
					 for (int j = 0; j < state.getHeight(); ++j){
						 boolean add = false;
						 if (state.getSpace(i, j) == 0){
							 int tempx = i - 1;
							 int tempy = j + 1;
							 
							 for (int k = 0; k < 3; ++k){
								 for (int l = 0; l < 3; ++l){
									 if ((tempx >= 0 && tempx <state.getWidth()
											 && tempy >= 0 && tempy < state.getHeight()
											 && !(tempy == j && tempx == i)
											 && state.getSpace(tempx, tempy) != 0)){ 
										add = true;
									 }
									 ++tempx;
								 }
								 --tempy;
								 tempx = i - 1;
							 }
							 if (add || (state.getkLength() >= 3 && crossingSec(state, player,i, j))
									 || (state.getkLength() >= 3 && crossingSec(state,opponent, i, j))){
								 myPoints[inc] = new Point(i, j);
								 ++inc;
							 }
						 }
					 }
				 }
			 }
			 
			 //find the shortest straight distance (euclids)
			 
			 //initialize euclids
			 Point middle = new Point(state.getWidth() / 2, state.getHeight() / 2);
			 for (int i = 0; i < size; ++i){
				 euclids[i] = Math.sqrt(Math.pow((myPoints[i].x - middle.x), 2) 
						 + Math.pow((myPoints[i].y - middle.y), 2));
			 }
			 
			 //initialize finalPoints
			 int k = 0;
			 int solidSize = size;
			 while (k < solidSize){
				 int smallest = 0;
				 for (int i = 1; i < size; ++i){
					 if (euclids[i] < euclids[smallest])
						 smallest = i;
				 }
				 finalPoints[k] = myPoints[smallest];
				 for (int i = smallest; i < size - 1; ++i){
					 myPoints[i] = myPoints[i + 1];
					 euclids[i] = euclids[i + 1];
				 }
				 ++k;
				 --size;
			 }
			 return finalPoints;
		 }
	}
	
	private void gravityThreatHelper (BoardModel state, byte pl){
		boolean plcheck=true;
		if(pl!=player) plcheck=false;
		
		//check horizontal
		Point lastEmpty = new Point(0, 0);
		for (int j = 0; j < state.getHeight(); ++j){
			for (int i = 0; i <= state.getWidth() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int k = i;
				while (k < i + state.getkLength() && go){
					if (state.getSpace(k, j) == pl)
						++num;
					else if (state.getSpace(k, j) == (byte)(pl == 1? 2 : 1))
						go = false;
					else
						lastEmpty = new Point(k, j);
					++k;
				}
				if (num == state.getkLength() - 1 && lastEmpty.y - 1 >= 0 
						&& state.getSpace(lastEmpty.x, lastEmpty.y - 1) == 0 && go){
					boolean add = true;
					if (plcheck) {
						for (int l = 0; l < threatsSize; ++l) {
							if (lastEmpty.equals(threats[l]))
								add = false;
						}
						if (add) {
							threats[threatsSize] = lastEmpty;
							++threatsSize;
						}
					}
					else{
						for (int l = 0; l < oppThreatsSize; ++l)
						{
							if (lastEmpty.equals(oppThreats[l]))
								add = false;
						}
						if (add)
						{
							oppThreats[oppThreatsSize] = lastEmpty;
							++oppThreatsSize;
						}
					}
				}
				
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check Vertical
		for (int j = 0; j < state.getWidth(); ++j){
			for (int i = 0; i <= state.getHeight() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int k = i;
				while (k < i + state.getkLength() && go){
					if (state.getSpace(j, k) == pl)
						++num;
					else if (state.getSpace(j, k) == (byte)(pl == 1? 2 : 1))
						go = false;
					else
						lastEmpty = new Point(j, k);
					++k;
				}
				if (num == state.getkLength() - 1 && lastEmpty.y - 1 >= 0 
						&& state.getSpace(lastEmpty.x, lastEmpty.y - 1) == 0 && go){
					boolean add = true;
					if (plcheck) {
						for (int l = 0; l < threatsSize; ++l) {
							if (lastEmpty.equals(threats[l]))
								add = false;
						}
						if (add) {
							threats[threatsSize] = lastEmpty;
							++threatsSize;
						}
					} 
					else {
						for (int l = 0; l < oppThreatsSize; ++l){
							if (lastEmpty.equals(oppThreats[l]))
								add = false;
						}
						if (add){
							oppThreats[oppThreatsSize] = lastEmpty;
							++oppThreatsSize;
						}
					}
				}
				
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check my bottom left to top right diagonals
		for (int j = 0; j <= state.getHeight() - state.getkLength(); ++j){
			for (int i = 0; i <= state.getWidth() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int tempHeight = j;
				int tempWidth = i;

				while (tempHeight < j + state.getkLength() && tempWidth < i + state.getkLength() && go){
					if (state.getSpace(tempWidth, tempHeight) == pl)
						++num;
					else if (state.getSpace(tempWidth, tempHeight) == (byte)(pl == 1? 2 : 1))
						go = false;
					else
						lastEmpty = new Point(tempWidth, tempHeight);
					++tempHeight;
					++tempWidth;
				}
				if (num == state.getkLength() - 1 && lastEmpty.y - 1 >= 0 
						&& state.getSpace(lastEmpty.x, lastEmpty.y - 1) == 0 && go){
					boolean add = true;
					if (plcheck) {
						for (int k = 0; k < threatsSize; ++k) {
							if (lastEmpty.equals(threats[k]))
								add = false;
						}
						if (add) {
							threats[threatsSize] = lastEmpty;
							++threatsSize;
						}
					}
					else{
						for (int k = 0; k < oppThreatsSize; ++k){
							if (lastEmpty.equals(oppThreats[k]))
								add = false;
						}
						if (add){
							oppThreats[oppThreatsSize] = lastEmpty;
							++oppThreatsSize;
						}
					}
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check my bottom right to top left diagonals
		for (int j = 0; j <= state.getHeight() - state.getkLength(); ++j){
			for (int i = state.getkLength() - 1; i < state.getWidth(); ++i){
				int num = 0;
				boolean go = true;
				int tempHeight = j;
				int tempWidth = i;
				while (tempHeight < j + state.getkLength() && tempWidth >= i - (state.getkLength() - 1) && go){
					if (state.getSpace(tempWidth, tempHeight) == pl)
						++num;
					else if (state.getSpace(tempWidth, tempHeight) == (byte)(pl == 1? 2 : 1))
						go = false;
					else
						lastEmpty = new Point(tempWidth, tempHeight);
					++tempHeight;
					--tempWidth;
				}				
				if (num == state.getkLength() - 1 && lastEmpty.y - 1 >= 0 
						&& state.getSpace(lastEmpty.x, lastEmpty.y - 1) == 0 && go){
					boolean add = true;
					if (plcheck) {
						for (int k = 0; k < threatsSize; ++k) {
							if (lastEmpty.equals(threats[k]))
								add = false;
						}
						if (add) {
							threats[threatsSize] = lastEmpty;
							++threatsSize;
						}
					}
					else{
						for (int k = 0; k < oppThreatsSize; ++k){
							if (lastEmpty.equals(oppThreats[k]))
								add = false;
						}
						if (add){
							oppThreats[oppThreatsSize] = lastEmpty;
							++oppThreatsSize;
						}
					}
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
	}
	
	private void noGravityHelper(BoardModel state, byte pl){
		boolean plcheck=true;
		if(pl!=player) plcheck=false;
		
		//check horizontal
		for (int j = 0; j < state.getHeight(); ++j){
			for (int i = 0; i <= state.getWidth() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int k = i;
				while (k < i + state.getkLength() && go){
					if (state.getSpace(k, j) == pl)
						++num;
					else if (state.getSpace(k, j) == (byte)(pl == 1? 2 : 1))
						go = false;
					++k;
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check vertical
		for (int j = 0; j < state.getWidth(); ++j){
			for (int i = 0; i <= state.getHeight() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int k = i;
				while (k < i + state.getkLength() && go){
					if (state.getSpace(j, k) == pl)
						++num;
					else if (state.getSpace(j, k) == (byte)(pl == 1? 2 : 1))
						go = false;
					++k;
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check bottom left to top right diagonals
		for (int j = 0; j <= state.getHeight() - state.getkLength(); ++j){
			for (int i = 0; i <= state.getWidth() - state.getkLength(); ++i){
				int num = 0;
				boolean go = true;
				int tempHeight = j;
				int tempWidth = i;
				while (tempHeight < j + state.getkLength() && tempWidth < i + state.getkLength() && go){
					if (state.getSpace(tempWidth, tempHeight) == pl)
						++num;
					else if (state.getSpace(tempWidth, tempHeight) == (byte)(pl == 1? 2 : 1))
						go = false;
					++tempHeight;
					++tempWidth;
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}
		
		//Check bottom right to top left diagonals
		for (int j = 0; j <= state.getHeight() - state.getkLength(); ++j){
			for (int i = state.getkLength() - 1; i < state.getWidth(); ++i){
				int num = 0;
				boolean go = true;
				int tempHeight = j;
				int tempWidth = i;
				while (tempHeight < j + state.getkLength() && tempWidth >= i - (state.getkLength() - 1) && go){
					if (state.getSpace(tempWidth, tempHeight) == pl)
						++num;
					else if (state.getSpace(tempWidth, tempHeight) == (byte)(pl == 1? 2 : 1))
						go = false;
					++tempHeight;
					--tempWidth;
				}
				if (go && num != 0)
					if(plcheck)
						++groups[num];
					else
						++oppGroups[num];
			}
		}		
	}
	

//////Heuristic evaluation function for alpha beta///////////////////
	private int hFunc(BoardModel state){
		int total = 0;
		int oppTotal = 0;
		
		//groups of different length rows, columns, or diagonals
		groups = new int[state.getkLength() + 1];
		oppGroups = new int[state.getkLength() + 1];
		
		if (state.gravity){		
			//Variables for threat spaces
			threats = new Point[domain];
			threatsSize = 0;
			oppThreats = new Point[domain];
			oppThreatsSize = 0;
			
			
			//Even odd strategy variables
			//AI odd and even threats
			int oddUnshared = 0;
			int evenUnshared = 0;
			int oddShared = 0;
			int evenShared = 0;
			//Opponent odd and even threats
			int oppOddUnshared = 0;
			int oppEvenUnshared = 0;
			int oppOddShared = 0;
			int oppEvenShared = 0;

			//check each directions
			
			gravityThreatHelper(state,player);
			gravityThreatHelper(state,opponent);
			
			//Even odd strategy for how many threats
			
			//each total threats for AI 
			boolean[] totalOddUnshared = new boolean[state.getWidth()];
			boolean[] totalEvenUnshared = new boolean[state.getWidth()];
			boolean[] totalOddShared = new boolean[state.getWidth()];
			boolean[] totalEvenShared = new boolean[state.getWidth()];
			
			for (int i = 0; i < threatsSize; ++i){
				boolean shared = false;
				for (int j = 0; j < oppThreatsSize; ++j)
					if (threats[i].x == oppThreats[j].x && threats[i].y >= oppThreats[j].y)
						shared = true;
				if (shared){
					if ((threats[i].y + 1) % 2 == 1 && !totalOddShared[threats[i].x]){
						++oddShared;
						totalOddShared[threats[i].x] = true;
					}
					else if ((threats[i].y + 1) % 2 == 0 && !totalEvenShared[threats[i].x]){
						++evenShared;
						totalEvenShared[threats[i].x] = true;
					}
				}
				else{
					if ((threats[i].y + 1) % 2 == 1 && !totalOddUnshared[threats[i].x]){
						++oddUnshared;
						totalOddUnshared[threats[i].x] = true;
					}
					else if ((threats[i].y + 1) % 2 == 0 && !totalEvenUnshared[threats[i].x]){
						++evenUnshared;
						totalEvenUnshared[threats[i].x] = true;
					}
				}
			}
			
			//each total threats for opponent
			boolean[] oppTotalOddUnshared = new boolean[state.getWidth()];
			boolean[] oppTotalEvenUnshared = new boolean[state.getWidth()];
			boolean[] oppTotalOddShared = new boolean[state.getWidth()];
			boolean[] oppTotalEvenShared = new boolean[state.getWidth()];
			
			for (int i = 0; i < oppThreatsSize; ++i){
				boolean shared = false;
				for (int j = 0; j < threatsSize; ++j)
					if (oppThreats[i].x == threats[j].x && oppThreats[i].y >= threats[j].y)
						shared = true;
				if (shared){
					if ((oppThreats[i].y + 1) % 2 == 1 && !oppTotalOddShared[oppThreats[i].x]){
						++oppOddShared;
						oppTotalOddShared[oppThreats[i].x] = true;
					}
					else if ((oppThreats[i].y + 1) % 2 == 0 && !oppTotalEvenShared[oppThreats[i].x]){
						++oppEvenShared;
						oppTotalEvenShared[oppThreats[i].x] = true;
					}
				}
				else{
					if ((oppThreats[i].y + 1) % 2 ==1 && !oppTotalOddUnshared[oppThreats[i].x]){
						++oppOddUnshared;
						oppTotalOddUnshared[oppThreats[i].x] = true;
					}
					else if ((oppThreats[i].y + 1) % 2 == 0 && !oppTotalEvenUnshared[oppThreats[i].x]){
						++oppEvenUnshared;
						oppTotalEvenUnshared[oppThreats[i].x] = true;
					}
				}
			}
			
			 // Even odd strategy, threats on certain rows will result int + or - value
		
			//AI is first player
			if (player == 1){
				if (state.getHeight() % 2 == 0){
					//AI total
					if (((oddUnshared - 1) == oppOddUnshared)||((oddUnshared == oppOddUnshared) 
									&&(oddShared%2==1))||(oppOddUnshared==0&&(oddShared+oddUnshared)%2==1))
						total += 10;
					//Opponent total
					if (((oddUnshared+oddShared)==0&&(oppEvenShared+oppEvenUnshared)>0)||((oppOddUnshared-2)==oddUnshared)
							|| ((oddUnshared == oppOddUnshared) && (oppOddShared % 2 == 0 && oppOddShared > 0))
							|| ((oppOddUnshared - 1) == oddUnshared && oppOddShared > 0)
							|| (oddUnshared == 0 && (oppOddUnshared == 1 && oppOddShared > 0))
							|| (((oppOddUnshared + oppOddShared) % 2 == 0 && (oppOddUnshared + oppOddShared) > 0) && oddUnshared == 0))
						oppTotal += 10;
				}
				else if (domain % 2 == 0 && state.getHeight() % 2 == 1){
					//AI total
					if (((evenUnshared - 1) == oppEvenUnshared)
							|| (evenShared % 2 == 1)
							|| ((evenShared + evenUnshared) == 1 && (oppOddShared + oppOddUnshared) == 1))
						total += 10;

					//Opponent total
					if (((oppOddShared + oppOddUnshared) > 0)
							|| (((oppEvenShared + oppEvenUnshared) % 2 == 0 && (oppEvenShared + oppEvenUnshared) > 0)
							&& (((oppEvenUnshared - 2) == evenUnshared)
									|| (oppEvenShared == evenShared))))
						oppTotal += 10;
				}
				else if (domain % 2 == 1){
					if (((oddShared+oddUnshared)>0)||(((evenShared + evenUnshared) % 2 == 0 
							&& (evenShared+evenUnshared)>0)&&((evenUnshared - 2 == oppEvenUnshared)
									|| (evenShared == oppEvenShared))))
						total += 10;
					
					if ((oppEvenUnshared - 1 == evenUnshared)
							|| (oppEvenShared % 2 == 1)
							|| ((oppEvenShared + oppEvenUnshared) == 1
							&& (oddShared + oddUnshared) == 1))
						oppTotal += 10;
				}
			}
			//AI is second player
			else{
				if (state.getHeight() % 2 == 0){
					//Opponnent total
					if (((oppOddUnshared-1)==oddUnshared)||((oppOddUnshared==oddUnshared)&&(oppOddShared % 2 == 1))||(oddUnshared==0&&(oppOddShared + oppOddUnshared)%2==1))
						oppTotal += 10;

					//AI total
					if(((oppOddUnshared+oppOddShared)==0&&(evenShared+evenUnshared) > 0)||((oddUnshared - 2) == oppOddUnshared)||((oppOddUnshared == oddUnshared)&&(oddShared % 2 == 0&&oddShared > 0))||((oddUnshared - 1)==oppOddUnshared&&oddShared > 0)||(oppOddUnshared == 0&&(oddUnshared == 1 && oddShared > 0))||(((oddUnshared + oddShared) % 2 == 0&&(oddUnshared + oddShared) > 0) && oppOddUnshared == 0))
						total += 10;
				}
				else if (domain % 2 == 0 && state.getHeight() % 2 == 1){
					//Opponent total
					if (((oppEvenUnshared - 1) == evenUnshared)||(oppEvenShared % 2 == 1)||((oppEvenShared + oppEvenUnshared) == 1&&(oddShared + oddUnshared) == 1))
						oppTotal += 10;

					//AI total
					if (((oddShared + oddUnshared) > 0)||(((evenShared + evenUnshared) % 2 == 0&&(evenShared + evenUnshared) > 0)&&(((evenUnshared - 2) == oppEvenUnshared)
									|| (evenShared == oppEvenShared))))
						total += 10;
				}
				else if (domain % 2 == 1){
					//Opponnent total
					if (((oppOddShared + oppOddUnshared) > 0)||(((oppEvenShared + oppEvenUnshared) % 2 == 0&&(oppEvenShared + oppEvenUnshared) > 0)&&((oppEvenUnshared - 2 == evenUnshared)||(oppEvenShared == evenShared))))
						oppTotal += 10;
					
					//AI total
					if ((evenUnshared - 1 == oppEvenUnshared)||(evenShared % 2 == 1)||((evenShared + evenUnshared) == 1&&(oppOddShared + oppOddUnshared) == 1))
						total += 10;
				}
			}
		}
		else{
			//check each direction
			
			noGravityHelper(state,player);
			noGravityHelper(state,opponent);
			
			/*
			 * We now check for junctions (spaces that can be included in more 
			 * than one grouping).
			 */
			//Check junctions
			//do i have a junction
			boolean crossSection = false;
			boolean emptyCrossSection = false;
			for (int i = 0; i < state.getWidth(); ++i)
				for (int j = 0; j < state.getHeight(); ++j){
					if (state.getSpace(i, j) == player)
						crossSection = crossingSec(state,player, i, j);
					else if (state.getSpace(i, j) == 0)
						emptyCrossSection = crossingSec(state,player, i, j);
				}
			if (crossSection)
				total += 10;
			else if (emptyCrossSection)
				total += 5;
			
			//do they have a junction
			boolean oppCrossSection = false;
			boolean oppEmptyCrossSection = false;
			for (int i = 0; i < state.getWidth(); ++i)
				for (int j = 0; j < state.getHeight(); ++j){
					if (state.getSpace(i, j) == (byte)(player == 1? 2 : 1))
						oppCrossSection = crossingSec(state,opponent, i, j);
					else if (state.getSpace(i, j) == 0)
						oppEmptyCrossSection = crossingSec(state,opponent, i, j);
				}
			
			if (oppCrossSection)
				oppTotal += 10;
			else if (oppEmptyCrossSection)
				oppTotal += 5;
		}
		
//////////get Total score for both player/////////////////////////////////////
		
		//get AI total
		int scale = 1;
		for (int i = 2; i < groups.length; ++i){
			total += (groups[i] * scale);
			++scale;
		}
		//get opponent total
		scale = 1;
		for (int i = 2; i < oppGroups.length; ++i){
			oppTotal += (oppGroups[i] * scale);
			++scale;
		}
		
		//return the evaluations
		if (oppGroups[state.getkLength()] > 0)
			return LOSE_VAL;
		else if (groups[state.getkLength()] > 0)
			return WIN_VAL;
		else{
			if (total - oppTotal == 0){
				if (player == 2)
					return 1;
				else
					return -1;
			}
			return total - oppTotal;
		}
	}
	
	public Point alphaBetaDeepening(BoardModel state, int deadline, long start){
		boolean checkLoop = true;
		Move best = new Move(0, new Point(0, 0));
		Move temp = new Move(0, new Point(0, 0));
		int i=1;
		Move[] mw = new Move[domain];
		int size = 0;
		
		while (checkLoop){
			temp = firstMax(state, MIN, MAX, i, deadline, start);
			mw[size] = temp;
			++size;
			if (temp.getV() == WIN_VAL)
				return temp.getP();
			if (temp.getV() == CANCEL_VAL){
				--size;
				if (best.getV() == LOSE_VAL){
					boolean doit = true;
					while (size - 1 >= 0 && doit){
						if (mw[size - 1].getV() != LOSE_VAL){
							best = mw[size - 1];
							doit = false;
						}
						--size;
					}
				}
				return best.getP();
			}
			if (temp.getV() == LOSE_VAL){
				boolean doit = true;				
				while (size - 1 >= 0 && doit){
					if (mw[size - 1].getV() != LOSE_VAL){
						best = mw[size - 1];
						doit = false;
					}
					--size;
				}
				return best.getP();
			}
			if (temp.getV() == 0)
				return temp.getP();
			best = temp;
			++i;
			
			long end = System.currentTimeMillis() - start + 50;
			if (end >= deadline)
				checkLoop = false;
		}
		if (best.getV() == LOSE_VAL){
			boolean doit = true;
			while (size - 1 >= 0 && doit){
				if (mw[size - 1].getV() != LOSE_VAL){
					best = mw[size - 1];
					doit = false;
				}
				--size;
			}
		}
		return best.getP();
	}
	
	private Move firstMax(BoardModel state, int alpha, int beta, int limit, int deadline, long start){
		int maxResult = MIN;
		Point[] mw = availableSpace(state.gravity, state, true);
		Move best = new Move(0, new Point(0, 0));

		for (int i = 0; i < mw.length; ++i){
			if (state.getSpace(mw[i]) == 0){
				byte win = state.clone().placePiece(mw[i], player).winner();
				
				long end = System.currentTimeMillis() - start;
				if (end >= deadline)
					return new Move(CANCEL_VAL, new Point(0, 0));
				
				if (win == player)
					return new Move(WIN_VAL, mw[i]);
				else if (win == opponent)
					maxResult = Math.max(maxResult, LOSE_VAL);
				else if (win == 0)
					maxResult = Math.max(maxResult, 0);
				else
					maxResult = Math.max(maxResult, minVal(state.clone().placePiece(mw[i], player), alpha, beta, limit - 1, deadline, start));
				
				if (maxResult == WIN_VAL)
					return new Move(maxResult, mw[i]);
				
				if (maxResult > alpha)
					best.setAll(maxResult, mw[i]);
				alpha = Math.max(alpha, maxResult);
			}
		}
		return best;
	}
	
	private int maxVal(BoardModel state, int alpha, int beta, int limit, int deadline, long start){
		if (limit <= 0)
			return hFunc(state);
		
		int maxResult = MIN;
		Point[] mw = availableSpace(state.gravity, state, true);
		
		for (int i = 0; i < mw.length; ++i){
			if (state.getSpace(mw[i]) == 0){
				byte win = state.placePiece(mw[i], player).winner();
				
				long end = System.currentTimeMillis() - start;
				if (end >= deadline)
					return CANCEL_VAL;
				
				if (win == player)
					return WIN_VAL;
				else if (win == (byte)(player == 1 ? 2 : 1))
					maxResult = Math.max(maxResult, LOSE_VAL);
				else if (win == 0)
					maxResult = Math.max(maxResult, 0);
				else
					maxResult = Math.max(maxResult, minVal(state.placePiece(mw[i], player), alpha, beta, limit - 1, deadline, start));
				
				if (maxResult >= beta)
					return maxResult;
				alpha = Math.max(alpha, maxResult);
			}
		}
		return maxResult;
	}
	
	private int minVal(BoardModel state, int alpha, int beta, int limit, int deadline, long start){
		if (limit <= 0)
			return hFunc(state);
		
		int minResult = MAX;
		Point[] mw = availableSpace(state.gravity, state, false);
		
		for (int i = 0; i < mw.length; ++i){
			if (state.getSpace(mw[i]) == 0){
				byte win = state.placePiece(mw[i], opponent).winner();
				
				long end = System.currentTimeMillis() - start;
				if (end >= deadline)
					return CANCEL_VAL;
				
				if (win == (byte)(player == 1 ? 2 : 1))
					return LOSE_VAL;
				else if (win == player)
					minResult = Math.min(minResult, WIN_VAL);
				else if (win == 0)
					minResult = Math.min(minResult, 0);
				else
					minResult = Math.min(minResult, maxVal(state.placePiece(mw[i], opponent), alpha, beta, limit - 1, deadline, start));
				
				if (minResult <= alpha)
					return minResult;
				beta = Math.min(beta, minResult);
			}
		}
		return minResult;
	}
	
	@Override
	public Point getMove(BoardModel state) {
		return getMove(state,5000);
	}

	
	@Override
	public Point getMove(BoardModel state, int deadline) {
		long start = System.currentTimeMillis();
		if (state.gravity){
			return alphaBetaDeepening(state, deadline, start);
		}
		else{
			if (state.spacesLeft == (domain))
				return new Point(state.getWidth() / 2, state.getHeight() / 2);
			else
				return alphaBetaDeepening(state, deadline, start);
		}
	}
}