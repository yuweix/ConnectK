#ConnectK

##Project Description
ConnectK is an enhanced version of Connect4 [the board game](https://en.wikipedia.org/wiki/Connect_Four). ConnectK's width (**w**), height (**h**), and **K** are all configurable. ConnectK is also more interesting as it supports 2 different gravity modes: **ON | OFF**. With gravity off, the pieces stay wherever they are placed. With gravity on, they fall till they meet another piece or hit the bottom. 

##Getting Started
### JAVA SHELL
####1. Running ConnectK.jar:
You can run it from the directory the file is in using the commandline/terminal with the command:
```shell
java -jar ConnectK.jar
```
Alternatively, you can simply doubleclick the jar to run it.
	
####2. Playing Connect K with ConnectK.jar:
After starting up ConnectK, the game will default to a two player (human vs. human) game of connect 5.

>You can change the settings of the game by selecting File->New from the drop down menu on the upper left. The width, height, K, and gravity of the board can be modified by changing the values in the 4 boxes labeled width, height, K, and gravity. 
	
Additionally, you can choose to import AI opponents. 3 AI opponents are provided for you in the `SampleAI` directory. Click Add AI and navigate to 'ConnectKSource_java/SampleAI/AverageAI/AverageAI.class'. Click open, and new game to start a game against the average AI. 

>You can add multiple AIs and watch 2 of them play against eachother.

######The template:
 An overloaded method and a constructor are provided. 
	 
 The constructor will be called with a player (1 or 2) and a blank BoardModel containing the dimensions (width, height) and rules (gravity, and k) for the game. 
	 
 The method getMove will be called with an updated gamestate (i.e. blank if it is a new game, has a piece for each move that has been played since) and should return a move in the form of a java.awt.Point(). 
	 
 GetMove can also be called with an additional parameter: deadline. deadline represents the amount of time in milliseconds that you have to return a move. 

#######BoardModel: 
BoardModel contains the rules, dimensions, and pieces in the game. 
	
The pieces are stored in a 2D array of bytes with the indices [x][y] representing column and row, respectively. The lower left corner is represented by (0,0) while the upper left, upper right, and bottom right corners in a R by C gameboard are represented by (0, R-1), (C-1, R-1), (C-1,0) respectively. Each slot in the array contains either a 0,1,2 which represent that the space is empty, occupied by player 1, or occupied by player 2, respectively. Note that these integer values are different from the C++ version.
	
	
There are getters available for the rules and dimensions as well as getters for the last move placed i.e. getLastMove()
	
	There are also many convenience methods available such as toString() which outputs a string representation of the game board. Please refer to BoardModel.java in the file "ConnectKSource.zip".	
	
Although most of the member variables are public, it is recommended to use the getters (e.g. getSpace(int x, int y) instead of directly accessing 'pieces[x][y]'. 



