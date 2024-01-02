import javax.swing.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/***************************
 *@author: Tim Mah
 *@dueDate: December 11th, 2020
 *@description: Runs a single
 * player version of the code
 * breaking board game "Mastermind,"
 * implementing a GUI.
 ****************************/
public class Mastermind {
	//Instance Variables

	int[] bwPins = {-1, -1, -1, -1};//so that it doesn't start at 0, because 0 is the colour black
	int counter = -1;
	boolean hasWon = false;

	//Images of coloured buttons
	ImageIcon blackB;
	ImageIcon whiteB;
	ImageIcon blueB;
	ImageIcon greenB;
	ImageIcon yellowB;
	ImageIcon redB;

	//images of the black/white dots used as hints, BBBW is not included because it is an impossible scenario
	ImageIcon bbbb;
	ImageIcon bbb_;
	ImageIcon bb__;
	ImageIcon b___;
	ImageIcon wwww;
	ImageIcon www_;
	ImageIcon ww__;
	ImageIcon w___;
	ImageIcon wwbb;
	ImageIcon wwwb;
	ImageIcon wbb_;
	ImageIcon wwb_;
	ImageIcon wb__;
	ImageIcon ____;

	//main frame and panel
	JFrame main;
	JPanel mainP;

	//example panel, button and frame
	JPanel exP;
	JButton exB;
	JLabel exL;
	JFrame exF;

	//rules window components
	JFrame rulesF;
	JPanel rulesP;
	JButton rulesB;
	JLabel wriRule[];

	//reset button
	JButton reset;

	JButton[] sub;//to submit the player's guess
	boolean[] hasSub;//If that row has been submitted

	int[]board;// This will store numerical values, representing pins, only check 1 row at a time, write over it for the next row
	JButton[][] pins;//pins that the user changes to guess the code
	JLabel[] bw;//Tells number of correct pins

	JLabel[] hidCode;//hidden code, reviled when player cracks it, or fails to crack it
	int[] code;//contains the code that will be compared to

	/**
	 * Mastermind constructor.
	 */
	public Mastermind()
	{
		reset = new JButton("Reset");

		exP = new JPanel();//new panel for the example window
		exL = new JLabel();//label containing the picture of the example
		exB = new JButton("Example");//button to show example
		exF = new JFrame("Example");//Frame containing the example

		wriRule = new JLabel[10];//contains all the rules
		rulesF = new JFrame();//window for rules
		rulesP = new JPanel(new GridLayout(12,1));//panel, containing all the Labels
		rulesB = new JButton("Rules");//button to open the rules window/tab

		main = new JFrame();//main window
		mainP = new JPanel(new GridLayout(12, 6));//main panel with the game components

		sub = new JButton[10];//Check code button
		hasSub = new boolean[10];// if they have submitted a line of code

		board = new int[4];//a line of code
		pins = new JButton[10][6];//every pin on the board (40)
		bw = new JLabel[10];//Labels that have images of hints

		hidCode = new JLabel[4];//Label that will change to reveal the code at the end
		code = new int[4];//the numerical code

		initialize();
		mainPanel();
		rules();
		genCode();

		rulesB.addActionListener(new rulesListen());
		exB.addActionListener(new exListener());

		main.add(mainP);
		main.pack();
		main.setVisible(true);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addButtonsPins();
		addButtonsSub();
		reset.addActionListener(new resetListen());

	}//end constructor
	/**
	 * Sets up the main 
	 */
	public void mainPanel()
	{
		//Setting up the Main panel, 12x6

		//First Line
		mainP.setBackground(new Color(238,209,159));
		mainP.add(reset);//empty, to fill up a space
		mainP.add(new JLabel());
		JLabel title = new JLabel("Master");
		title.setFont(new Font("Ariel", Font.BOLD, 24));
		mainP.add(title);

		JLabel title2 = new JLabel("mind");
		title2.setFont(new Font("Ariel", Font.BOLD, 24));
		mainP.add(title2);
		mainP.add(new JLabel());
		mainP.add(rulesB);

		//Second Line
		for(byte b =0; b<hidCode.length; b++)
		{
			hidCode[b].setBackground(new Color(220, 184, 135));
			hidCode[b].setOpaque(true);
			hidCode[b].setBorder(null);
		}
		mainP.add(new JLabel());
		mainP.add(hidCode[0]);
		mainP.add(hidCode[1]);
		mainP.add(hidCode[2]);
		mainP.add(hidCode[3]);
		mainP.add(new JLabel());

		for(byte b = 0; b<10; b++)//add all the next rows
		{
			sub[b].setText("Check");
			mainP.add(sub[b]);
			//add all the pins for the row
			for(int i =0; i<4; i++)
			{
				mainP.add(pins[b][i]);
			}
			mainP.add(bw[b]);
		}
	}
	/**
	 * Initializes all of the instance variables.
	 */
	public void initialize()
	{
		//Initialize every array of objects
		for(byte b = 0; b<sub.length; b++)
		{
			sub[b] = new JButton();
		}

		for(byte b = 0; b<pins.length; b++)
		{
			for(byte i = 0; i<pins[b].length; i++)
			{
				pins[b][i] = new JButton();
			}
		}

		for(byte b = 0; b<bw.length; b++)
		{
			bw[b] = new JLabel();
		}	

		hidCode[0] = new JLabel();
		hidCode[1] = new JLabel("Hidden ", JLabel.RIGHT); //so that it appears centred
		hidCode[2] = new JLabel("Code", JLabel.LEFT);
		hidCode[3] = new JLabel();

		for(byte b = 0; b<board.length; b++)
		{
			board[b] = -1;
		}
	}//end initialize
	/**
	 * Puts a line of text in each wriRule label.
	 */
	public void rules()
	{
		String[] rule = {" ", "     Objective: To guess the code generated by the computer in the fewest steps", " ",
				"     Start on the topmost row, click on the buttons to change their colour (You can cycle through all 6 colours)",
				"     Once you have a code that you are satified with, press the \"check\" button.",
				"     Note: you will not be able to change it after submitting",
				"     The 4 dots on the right side will tell you which ones are correct.",
				"     A black dot means that one of the pieces is the correct colour, and in the correct spot,",
				"     while a white dot means that one of the pieces is the correct colour, but incorrect spot.",
		"     Use this information to try to figure out the code."};
		for(byte b = 0; b<wriRule.length; b++)
		{
			wriRule[b] = new JLabel();
			wriRule[b].setText(rule[b]);
		}
	}
	/**
	 * Sets the colours of the buttons in the main
	 * window.
	 * @param: row The row where the pins are.
	 * @param: pp The pin's position in the row.
	 */
	public void setPinColour(int row, int pp)
	{
		//sets pin colour
		if(board[pp] == 0)
		{

			//create Image
			blackB = new ImageIcon("Black Button.png");
			Image i = blackB.getImage();
			//resize said Image
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			blackB = new ImageIcon(i); 

			pins[row][pp].setIcon(blackB);
		}else if(board[pp] ==1)
		{

			whiteB = new ImageIcon("White Button.png");
			Image i = whiteB.getImage();
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			whiteB = new ImageIcon(i);

			pins[row][pp].setIcon(whiteB);
		}else if(board[pp] ==2)
		{
			blueB = new ImageIcon("Blue Button.png");
			Image i = blueB.getImage();
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			blueB = new ImageIcon(i);

			pins[row][pp].setIcon(blueB);
		}else if(board[pp] ==3)
		{
			greenB = new ImageIcon("Green Button.png");
			Image i = greenB.getImage();
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			greenB = new ImageIcon(i);

			pins[row][pp].setIcon(greenB);
		}else if(board[pp] ==4)
		{
			yellowB = new ImageIcon("Yellow Button.png");
			Image i = yellowB.getImage();
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			yellowB = new ImageIcon(i);

			pins[row][pp].setIcon(yellowB);
		}else
		{
			redB = new ImageIcon("Red Button.png");
			Image i = redB.getImage();
			i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
			redB = new ImageIcon(i);

			pins[row][pp].setIcon(redB);
		}

	}
	/**
	 * At the end of the game, it revels the generated code
	 * the player is trying to guess, or has guessed.
	 */
	public void showPinColourCode()
	{
		for(byte b = 0; b<4; b++)//for loop to run through all 4 colours of the code
		{
			//sets pin colour
			if(code[b] == 0)
			{

				//create Image
				blackB = new ImageIcon("Black Button.png");
				Image i = blackB.getImage();
				//resize said Image
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				blackB = new ImageIcon(i); 

				//if the revealed pin is black, the text needs to be visible
				Font white = new Font("Ariel", Font.PLAIN, 12);
				hidCode[b].setForeground(Color.WHITE);
				hidCode[b].setFont(white);

				hidCode[b].setIcon(blackB);
			}else if(code[b] ==1)
			{

				whiteB = new ImageIcon("White Button.png");
				Image i = whiteB.getImage();
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				whiteB = new ImageIcon(i);

				hidCode[b].setIcon(whiteB);
			}else if(code[b] ==2)
			{
				blueB = new ImageIcon("Blue Button.png");
				Image i = blueB.getImage();
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				blueB = new ImageIcon(i);

				hidCode[b].setIcon(blueB);
			}else if(code[b] ==3)
			{
				greenB = new ImageIcon("Green Button.png");
				Image i = greenB.getImage();
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				greenB = new ImageIcon(i);

				hidCode[b].setIcon(greenB);
			}else if(code[b] ==4)
			{
				yellowB = new ImageIcon("Yellow Button.png");
				Image i = yellowB.getImage();
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				yellowB = new ImageIcon(i);

				hidCode[b].setIcon(yellowB);
			}else
			{
				redB = new ImageIcon("Red Button.png");
				Image i = redB.getImage();
				i= i.getScaledInstance(81,29, Image.SCALE_SMOOTH);
				redB = new ImageIcon(i);

				hidCode[b].setIcon(redB);
			}
		}

	}
	/**
	 * When a button is pressed, it will cycle to the next colour.
	 * @param: row The row where the button is at.
	 * @param: pp The pin's position in said row.
	 */
	public void pinColour(int row, int pp)
	//cycle through colours, as long as the corresponding Submitted button has been pressed
	//need to know the the pin position (the 2nd [], we only care about the column)
	//the first [] is for HasSub, because that only cares about the rows
	//need to have a counter, to cycle through the colours
	/* 0 - Black
	 * 1 - White
	 * 2 - Blue
	 * 3 - Green
	 * 4 - Yellow
	 * 5 - Red
	 * 
	 * Can change it as long as hasSub is false
	 */
	{
		//Cycle through colours

		//set this equal to the array, bc it will be default at 0
		if(hasSub[row] == false && row == 0)
		{
			counter = board[pp];
			if(counter >4)//reset counter when it gets too high
			{
				counter = -1;
			}
			counter++;
			board[pp] = counter;
			setPinColour(row, pp);
		}else if(hasSub[row] == false && hasSub[row-1] == true)//as long as it hasn't been submitted, and the row before has been submitted
		{
			counter = board[pp];
			if(counter >4)//reset counter when it gets too high
			{
				counter = -1;
			}
			counter++;
			board[pp] = counter;
			setPinColour(row, pp);
		}
	}
	/**
	 * Adds all the Action Listeners to the pins.
	 */
	public void addButtonsPins()
	{
		pins[0][0].addActionListener(new pinListen00());
		pins[0][1].addActionListener(new pinListen01());
		pins[0][2].addActionListener(new pinListen02());
		pins[0][3].addActionListener(new pinListen03());

		pins[1][0].addActionListener(new pinListen10());
		pins[1][1].addActionListener(new pinListen11());
		pins[1][2].addActionListener(new pinListen12());
		pins[1][3].addActionListener(new pinListen13());

		pins[2][0].addActionListener(new pinListen20());
		pins[2][1].addActionListener(new pinListen21());
		pins[2][2].addActionListener(new pinListen22());
		pins[2][3].addActionListener(new pinListen23());

		pins[3][0].addActionListener(new pinListen30());
		pins[3][1].addActionListener(new pinListen31());
		pins[3][2].addActionListener(new pinListen32());
		pins[3][3].addActionListener(new pinListen33());

		pins[4][0].addActionListener(new pinListen40());
		pins[4][1].addActionListener(new pinListen41());
		pins[4][2].addActionListener(new pinListen42());
		pins[4][3].addActionListener(new pinListen43());

		pins[5][0].addActionListener(new pinListen50());
		pins[5][1].addActionListener(new pinListen51());
		pins[5][2].addActionListener(new pinListen52());
		pins[5][3].addActionListener(new pinListen53());

		pins[6][0].addActionListener(new pinListen60());
		pins[6][1].addActionListener(new pinListen61());
		pins[6][2].addActionListener(new pinListen62());
		pins[6][3].addActionListener(new pinListen63());

		pins[7][0].addActionListener(new pinListen70());
		pins[7][1].addActionListener(new pinListen71());
		pins[7][2].addActionListener(new pinListen72());
		pins[7][3].addActionListener(new pinListen73());

		pins[8][0].addActionListener(new pinListen80());
		pins[8][1].addActionListener(new pinListen81());
		pins[8][2].addActionListener(new pinListen82());
		pins[8][3].addActionListener(new pinListen83());

		pins[9][0].addActionListener(new pinListen90());
		pins[9][1].addActionListener(new pinListen91());
		pins[9][2].addActionListener(new pinListen92());
		pins[9][3].addActionListener(new pinListen93());
	}
	/**
	 * Add all the Action Listeners to the "Check" buttons.
	 */
	public void addButtonsSub()
	{
		sub[0].addActionListener(new subListener0());
		sub[1].addActionListener(new subListener1());
		sub[2].addActionListener(new subListener2());
		sub[3].addActionListener(new subListener3());
		sub[4].addActionListener(new subListener4());
		sub[5].addActionListener(new subListener5());
		sub[6].addActionListener(new subListener6());
		sub[7].addActionListener(new subListener7());
		sub[8].addActionListener(new subListener8());
		sub[9].addActionListener(new subListener9());
	}
	/**
	 * Checks to see if the row can be submitted, if it can,
	 * it will give out the hints, check if you cracked the code,
	 * and final resets the board[] array with the numerical
	 * representation of the code.
	 * @param: pos The vertical position of the code being checked,
	 * or submitted.
	 */
	public void submit(int pos)
	{
		if(pos != 0 && pos!=9)//different for first one and last
		{
			if(board[0] !=-1 && board[1] !=-1 && board[2] !=-1 && board[3] !=-1 && hasSub[pos-1] == true)//as long as every button has been pressed, 
				//but i need this to run if someone accidently hits submit for a line ahead
			{
				hasSub[pos] = true;//can't edit the pins anymore
				check();//This method calculates how many are right/partially right, and displays it, and if all of them are right, hasFalse becomes true
				showBWPins(pos);
				win();
				resetPin(pos);
			}
		}else if(pos == 9 )
		{
			if(board[0] !=-1 && board[1] !=-1 && board[2] !=-1 && board[3] !=-1)//as long as every button has been presses, 
			{
				hasSub[pos] = true;//can't edit the pins anymore
				check();//This method calculates how many are right/partially right, and displays it, and if all of them are right, hasFalse becomes true
				showBWPins(pos);
				win(9);
			}
		}else if(hasSub[0] == false)
		{
			if(board[0] !=-1 && board[1] !=-1 && board[2] !=-1 && board[3] !=-1)//as long as every button has been presses, 
			{
				hasSub[pos] = true;//can't edit the pins anymore
				check();//This method calculates how many are right/partially right, and displays it, and if all of them are right, hasFalse becomes true
				showBWPins(pos);
				win();
				resetPin();
			}
		}
	}
	/**
	 * Generates a random numeric code, which represents
	 * the colours used on the board. This is the code the
	 * player tries to crack.
	 */
	public void genCode()
	{
		for(byte b =0; b<code.length; b++)
		{
			code[b] = (int) (Math.random()*6);
		}
	}
	/**
	 * Locks the whole board so that nothing can be edited.
	 */
	public void lock()
	{
		for(byte b = 0; b<hasSub.length; b++)
		{
			hasSub[b] = true;
		}
	}
	/**
	 * Resets all the pins in the board[] array in a certain row.
	 * This is the numerical values and are used for calculations.
	 * The visuals are unaffected.
	 * @param: pos Is used to check that the row before had been submitted,
	 * so that no information is erased prematurely.
	 */
	public void resetPin(int pos)
	{
		if(hasSub[pos-1] == true)
		{
			for(byte b = 0; b<board.length; b++)
			{
				board[b] = -1;//reset it
			}
			for(byte b = 0; b<bwPins.length; b++)
			{
				bwPins[b] = -1;//reset it
			}
		}
	}
	/**
	 * Resets the board array.
	 */
	public void resetPin()
	{

		for(byte b = 0; b<board.length; b++)
		{
			board[b] = -1;//reset it
		}
		for(byte b = 0; b<bwPins.length; b++)
		{
			bwPins[b] = -1;//reset it
		}

	}
	/**
	 * Checks for the amount of black and white dots needed (they are hints).
	 */
	public void check()
	{
		//0 = Black
		//1 = white
		int[] codeCopy = new int[4];
		for(byte b=0; b<4; b++)
		{
			codeCopy[b] = code[b];
		}

		//This method calculates how many are right/partially right, and displays it, and if all of them are right, hasFalse becomes true
		for(byte b =0; b<4; b++)//figure amount of black pins needed, by checking both arrays
		{
			if(codeCopy[b] == board[b])
			{
				//remove pins that match from the board
				board[b] = -3;
				codeCopy[b] = -1;//so that any duplicates won't trigger this again
				bwPins[b] = 0;
			}
		}

		//figure amount of white pins (but make sure that if the player guesses 2 correct colour wrong spot, only 1 white pin 
		//( I could do that by just replacing anything that is a pin in the array to -1)

		for(byte b =0; b<4; b++)//figure amount of white pins needed, by checking both arrays
		{
			for(byte i = 0; i<4; i++)
			{
				if(codeCopy[b] == board[i])//check each one in the code to see if that colour is in the board
				{
					//remove pins that match from the board, but this replaces it where it is
					board[i] = -2;
					codeCopy[b] = -1;
					bwPins[i] = 1;
				}
			}
		}

		//sort it, so that all the open spaces are at the end
		boolean isSorted = false;
		while(isSorted == false)
		{
			isSorted = true;
			for(byte b = 0; b<bwPins.length-1; b++)
			{
				if(bwPins[b]<bwPins[b+1])
				{
					isSorted = false;
					int temp = bwPins[b+1];
					bwPins[b+1] = bwPins[b];
					bwPins[b] = temp;
				}//end if
			}//end for
		}//end while
	}//end check()
	/**
	 * Displays the correct image with Black, White, or no dots, or a combination of the 3.
	 * @param: pos The row the hint applies to and will be placed.
	 */
	public void showBWPins(int pos)
	{
		if(bwPins[0] == 0 && bwPins[1] == 0 && bwPins[2] == 0 && bwPins[3] == 0)//bbbb
		{
			bbbb = new ImageIcon("BBBB.png");
			Image i = bbbb.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			bbbb = new ImageIcon(i);

			bw[pos].setIcon(bbbb);
			hasWon = true;
		}else if(bwPins[0] == 0 && bwPins[1] == 0 && bwPins[2] == 0 && bwPins[3] == -1)//bbb-
		{
			bbb_ = new ImageIcon("BBB-.png");
			Image i = bbb_.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			bbb_ = new ImageIcon(i);

			bw[pos].setIcon(bbb_);

		}else if(bwPins[0] == 0 && bwPins[1] == 0 && bwPins[2] == -1 && bwPins[3] == -1)//bb--
		{
			bb__ = new ImageIcon("BB--.png");
			Image i = bb__.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			bb__ = new ImageIcon(i);

			bw[pos].setIcon(bb__);
		}else if(bwPins[0] == 0 && bwPins[1] == -1 && bwPins[2] == -1 && bwPins[3] == -1)//b---
		{
			b___ = new ImageIcon("B---.png");
			Image i = b___.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			b___ = new ImageIcon(i);

			bw[pos].setIcon(b___);
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == 1 && bwPins[3] == 1)//wwww
		{
			wwww = new ImageIcon("WWWW.png");
			Image i = wwww.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wwww = new ImageIcon(i);

			bw[pos].setIcon(wwww);	
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == 1 && bwPins[3] == -1)//www-
		{
			www_ = new ImageIcon("WWW-.png");
			Image i = www_.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			www_ = new ImageIcon(i);

			bw[pos].setIcon(www_);
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == -1 && bwPins[3] == -1)//ww--
		{
			ww__ = new ImageIcon("WW--.png");
			Image i = ww__.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			ww__ = new ImageIcon(i);

			bw[pos].setIcon(ww__);	
		}else if(bwPins[0] == 1 && bwPins[1] == -1 && bwPins[2] == -1 && bwPins[3] == -1)//w---
		{
			w___ = new ImageIcon("W---.png");
			Image i = w___.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			w___ = new ImageIcon(i);

			bw[pos].setIcon(w___);
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == 0 && bwPins[3] == 0)//wwbb
		{
			wwbb = new ImageIcon("BBWW.png");
			Image i = wwbb.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wwbb = new ImageIcon(i);

			bw[pos].setIcon(wwbb);
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == 1 && bwPins[3] == 0)//wwwb
		{
			wwwb = new ImageIcon("BWWW.png");
			Image i = wwwb.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wwwb = new ImageIcon(i);

			bw[pos].setIcon(wwwb);
		}else if(bwPins[0] == 1 && bwPins[1] == 0 && bwPins[2] == 0 && bwPins[3] == -1)//wbb-
		{
			wbb_ = new ImageIcon("WBB-.png");
			Image i = wbb_.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wbb_ = new ImageIcon(i);

			bw[pos].setIcon(wbb_);
		}else if(bwPins[0] == 1 && bwPins[1] == 1 && bwPins[2] == 0 && bwPins[3] == -1)//wwb-
		{
			wwb_ = new ImageIcon("WWB-.png");
			Image i = wwb_.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wwb_ = new ImageIcon(i);

			bw[pos].setIcon(wwb_);
		}else if(bwPins[0] == 1 && bwPins[1] == 0 && bwPins[2] == -1 && bwPins[3] == -1)//wb--
		{
			wb__ = new ImageIcon("WB--.png");
			Image i = wb__.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			wb__ = new ImageIcon(i);

			bw[pos].setIcon(wb__);
		}else if(bwPins[0] == -1 && bwPins[1] == -1 && bwPins[2] == -1 && bwPins[3] == -1)//----
		{
			____ = new ImageIcon("----.png");
			Image i = ____.getImage();
			i= i.getScaledInstance(79,26, Image.SCALE_SMOOTH);
			____ = new ImageIcon(i);

			bw[pos].setIcon(____);
		}
	}
	/**
	 * Checks to see if there is a winner, as well as congratulates them if they do win.
	 * If they win, the board is locked and it shows the cracked code.
	 */
	public void win()
	{
		if(hasWon == true)
		{
			//reveal the code (Label), and change the title from "Mastermind" to "You cracked it!"
			//else if, it is the last attempt, then show the code (Label) and change the title to "good effort"
			//else do nothing
			showPinColourCode();
			hidCode[1].setText("You Have ");
			hidCode[1].setHorizontalTextPosition(JLabel.CENTER);
			hidCode[2].setText("Cracked It");
			hidCode[2].setHorizontalTextPosition(JLabel.CENTER);

			//Locked so it can't be changed
			lock();
		}
	}
	/**
	 * Checks to see if there is a winner, as well as congratulates them if they do win.
	 * If they win, the board is locked and it shows the cracked code. If it is at the 
	 * last row and the player has not solved it, it will reveal the answer and give them
	 * comforting words.
	 * @param: pos The row it is, used to check the last row.
	 */
	public void win(int pos)
	{
		if(hasWon == true)
		{
			//takes in 1 parameter, that parameter is an int, and that is the vertical position
			//if hasWon is true, then reveal the code (Label), and change "Hidden Code" to "You cracked it!"
			showPinColourCode();
			hidCode[1].setText("You Have ");
			hidCode[1].setHorizontalTextPosition(JLabel.CENTER);
			hidCode[2].setText("Cracked It");
			hidCode[2].setHorizontalTextPosition(JLabel.CENTER);
			//Locked so it can't be changed
			lock();
		}else if(pos == 9)
		{
			showPinColourCode();
			hidCode[1].setText("Good ");
			hidCode[1].setHorizontalTextPosition(JLabel.CENTER);
			hidCode[2].setText("Effort");
			hidCode[2].setHorizontalTextPosition(JLabel.CENTER);
			//Locked so it can't be changed
			lock();
		}
	}

	/**
	 * When triggered, a window with the example image opens.
	 * @author Tim Mah
	 */
	class exListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			//create Image
			ImageIcon example = new ImageIcon("Example.png");
			Image i = example.getImage();
			//resize said Image
			i= i.getScaledInstance(750,750, Image.SCALE_SMOOTH);
			example = new ImageIcon(i); 

			exL.setIcon(example);
			exP.add(exL);
			exF.add(exP);
			exF.pack();
			exF.setVisible(true);
		}
	}
	/**
	 * Resets all the components in the main window so that
	 * a new game can be played.
	 * @author Tim Mah
	 *
	 */
	class resetListen implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			//reset all the variables
			resetPin();
			for(byte b = 0; b<code.length; b++)
			{
				code[b] = 0;
			}
			for(byte b = 0; b<hasSub.length; b++)
			{
				hasSub[b] = false;
			}
			hasWon = false;
			counter = -1;
			for(byte b = 0; b<bwPins.length; b++)
			{
				bwPins[b] = -1;
			}

			for (byte b = 0; b<pins.length; b++)//reset all the pins to their original look
			{
				for(byte i = 0; i<pins[b].length; i++)
				{
					pins[b][i].setIcon(null);
				}
			}

			for (byte b = 0; b<hidCode.length; b++)//reset the code to its original look
			{
				hidCode[b].setIcon(null);
			}

			for (byte b = 0; b<bw.length; b++)//reset dots to their original look
			{
				bw[b].setIcon(null);
			}
			hidCode[1].setText("Hidden ");
			hidCode[1].setHorizontalTextPosition(JLabel.RIGHT);
			hidCode[2].setText("Code");
			hidCode[2].setHorizontalTextPosition(JLabel.LEFT);
			//and change both text to black
			Font black = new Font("Ariel", Font.PLAIN, 12);
			hidCode[1].setForeground(Color.BLACK);
			hidCode[1].setFont(black);

			hidCode[2].setForeground(Color.BLACK);
			hidCode[2].setFont(black);
		}
	}
	/**
	 * When triggered, a window containing the rules and instructions
	 * opens.
	 * @author Tim Mah
	 *
	 */
	class rulesListen implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			//when this is clicked, the rules shows up
			for(byte b = 0; b<wriRule.length; b++)
			{
				rulesP.add(wriRule[b]);
			}
			rulesP.add(exB);

			rulesF.add(rulesP);
			rulesF.pack();
			rulesF.setVisible(true);
		}
	}
	/**
	 * This and every other class that follows the pinListenxx naming scheme
	 * runs the colour cycling method, with the button's position.
	 * @author Tim Mah
	 *
	 */
	class pinListen00 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(0, 0);
		}
	}
	class pinListen01 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(0, 1);
		}
	}
	class pinListen02 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(0, 2);
		}
	}
	class pinListen03 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(0, 3);
		}
	}
	class pinListen10 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(1, 0);
		}
	}
	class pinListen11 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(1, 1);
		}
	}
	class pinListen12 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(1, 2);
		}
	}
	class pinListen13 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(1, 3);
		}
	}
	class pinListen20 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(2, 0);
		}
	}
	class pinListen21 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(2, 1);
		}
	}
	class pinListen22 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(2, 2);
		}
	}
	class pinListen23 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(2, 3);
		}
	}
	class pinListen30 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(3, 0);
		}
	}
	class pinListen31 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(3, 1);
		}
	}
	class pinListen32 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(3, 2);
		}
	}
	class pinListen33 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(3, 3);
		}
	}
	class pinListen40 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(4, 0);
		}
	}
	class pinListen41 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(4, 1);
		}
	}
	class pinListen42 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(4, 2);
		}
	}
	class pinListen43 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(4, 3);
		}
	}
	class pinListen50 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(5, 0);
		}
	}
	class pinListen51 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(5, 1);
		}
	}
	class pinListen52 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(5, 2);
		}
	}
	class pinListen53 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(5, 3);
		}
	}
	class pinListen60 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(6, 0);
		}
	}
	class pinListen61 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(6, 1);
		}
	}
	class pinListen62 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(6,2);
		}
	}
	class pinListen63 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(6, 3);
		}
	}
	class pinListen70 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(7, 0);
		}
	}
	class pinListen71 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(7, 1);
		}
	}
	class pinListen72 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(7, 2);
		}
	}
	class pinListen73 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(7, 3);
		}
	}
	class pinListen80 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(8, 0);
		}
	}
	class pinListen81 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(8, 1);
		}
	}
	class pinListen82 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(8, 2);
		}
	}
	class pinListen83 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(8, 3);
		}
	}
	class pinListen90 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(9, 0);
		}
	}
	class pinListen91 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(9, 1);
		}
	}
	class pinListen92 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(9, 2);
		}
	}
	class pinListen93 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			pinColour(9, 3);
		}
	}
	/**
	 * This and every class with the naming scheme "subListenerx" runs the submit method,
	 * using the row number.
	 * @author Tim Mah
	 *
	 */
	class subListener0 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			submit(0);//makes sure it can be submitted, then locks the answer so it can't be changed

		}
	}
	class subListener1 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(1);
		}
	}
	class subListener2 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(2);
		}
	}
	class subListener3 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(3);
		}
	}
	class subListener4 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(4);
		}
	}
	class subListener5 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(5);
		}
	}
	class subListener6 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			submit(6);
		}
	}
	class subListener7 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(7);
		}
	}
	class subListener8 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(8);
		}
	}
	class subListener9 implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			submit(9);
		}
	}

	/**
	 * Main method, makes a new instance of
	 * the nested Mastermind Class.
	 */
	public static void main(String[] args)
	{
		new Mastermind();
	}//end main

}//end class
