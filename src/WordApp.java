

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;
	static boolean start=false;
   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;
	static JLabel caught,missed,scr;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static 	Score score = new Score();

	static WordPanel w;
	
	
	
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	JFrame frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
      JPanel g = new JPanel();
      g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      g.setSize(frameX,frameY);
    	
		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	   g.add(w); 
	    
      JPanel txt = new JPanel();
      txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
      caught =new JLabel("Caught: " + score.getCaught() + "    ");
      missed =new JLabel("Missed:" + score.getMissed()+ "    ");
      scr =new JLabel("Score:" + score.getScore()+ "    ");    
      txt.add(caught);
	   txt.add(missed);
	   txt.add(scr);
    
	    //[snip]
  
	   final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener()
	   {
	      public void actionPerformed(ActionEvent evt) {
	         String text = textEntry.getText();
	         for(WordRecord r :words)
			 {
				 if(r.matchWord(text))//checks if entered word is on screen
				 {
					score.caughtWord(text.length());
					update();
					break;
				 }
				 
			 }
	         textEntry.setText("");
	         textEntry.requestFocus();
	      }
	   });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);
	    
	   JPanel b = new JPanel();
      b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
	   JButton startB = new JButton("Start/Restart");;
	   
		
			// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
			   start=true;
				done=false;
				w.ind.set(0);//resets the atomic integer incase we resting the game
				score.resetScore();
				update();
		      //[snip]
			  newWords();
			  startAnimation();//starts the animation by starting WordPanel Threads for each word
		      textEntry.requestFocus();  //return focus to the text entry field
			  
		   }
		});
		JButton endB = new JButton("End");;
			
				// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
				end();
				w.gameOver=true;
				score.resetScore();
				update();

		   }
		});
		JButton quit = new JButton("Quit");;
		quit.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
		      System.exit(0);//closes App
				

		   }
		});
		
		b.add(startB);
		b.add(endB);
		b.add(quit);
		
		g.add(b);
    	
      frame.setLocationRelativeTo(null);  // Center window on screen.
      frame.add(g); //add contents to window
      frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
      frame.setVisible(true);
	
	 
	}
	public static void end()
	{
		for(WordRecord wrd:words)
			  {
				  wrd.resetWord();// reseting each word for next play
			  }
				
				done=true;
				start=false;
	}

   public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;
	}
	public synchronized static  void update()
	{
		//updates the score
		scr.setText("Score:" + score.getScore()+ "    ");
		caught.setText("Caught: " + score.getCaught() + "    ");
		missed.setText("Missed:" + score.getMissed()+ "    ");
	}
	public static void startAnimation()
	{
		//creates and starts a thread for each word falling
		for(int i=0;i<noWords;i++)
		{
			Thread win=new Thread(w);
			win.start();
			
		}
	}
	public static void newWords()
	{
		//creates an array of new words
		int x_inc=(int)frameX/noWords;
		for (int i=0;i<totalWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}
	}

	public static void main(String[] args) {
    	
		//deal with command line arguments
		System.setProperty("sun.java2d.opengl","True");
		Scanner keyboard=new Scanner(System.in);
		System.out.println("Enter <total words> <words on screen> <dictionary file>:");
		String[] input=(keyboard.nextLine()).split(" ");
		
		totalWords=Integer.parseInt(input[0]);  //total words to fall
		noWords=Integer.parseInt(input[1]); // total words falling at any point
		assert(totalWords>=noWords):"Total words must be greater total falling words"; // this could be done more neatly

		String[] tmpDict=getDictFromFile(input[2]); //file of words
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[totalWords];  //shared array of current words
		
		//[snip]
		
		setupGUI(frameX, frameY, yLimit);  
		//newWords();
		
    	
	}
}