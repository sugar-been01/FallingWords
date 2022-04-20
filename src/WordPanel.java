
import java.io.File;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.image.*;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class WordPanel extends JPanel implements Runnable {
		public static volatile boolean done=false;
		private WordRecord[] words;
		private int noWords;
		private int maxY;
		public boolean gameOver=false;
		public AtomicInteger ind=new AtomicInteger(0);
		
		
		public void paintComponent(Graphics g) {
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);

		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		   //draw the words
		   //animation must be added 
			if(WordApp.start)//checks if the start button has been pressed
			{

			
				for (int i=0;i<noWords;i++){	    	
					g.drawString(words[i].getWord(),words[i].getX(),words[i].getY()+20);  //y-offset for skeleton so that you can see the words	

				}

			}
			else if(gameOver)
			{
				//displays the gameOver image
				BufferedImage img=null;
				try
				{
					img =ImageIO.read(new File("tools/gameOver.jpg"));
				}
				catch(IOException e)
				{}
				g.drawImage(img,150,0,this);
				
				g.drawString("Press start to play again :)", 350,500);
				WordApp.end();
			}
		
		  }
		  
		  public static  void update()
		  {
				WordApp.update();//UPDATES SCORE
		  }
		
		WordPanel(WordRecord[] words, int maxY) {
			this.words=words; //will this work?
			noWords = words.length;
			done=false;
			this.maxY=maxY;		
		}
		
		public void run()
		 { 
			WordRecord word=words[ind.get()];
			ind.incrementAndGet();
			while(!(WordApp.done))
			{	
				fall(word);//drops word
				
				try
				{
					Thread.sleep(word.getSpeed());//pause the thread to allow 
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				if(WordApp.score.getTotal()==WordApp.totalWords)
				{
					WordApp.done=true;
					WordApp.start=false;
					gameOver=true;
				}	
				repaint();				
			}
			if (WordApp.done)
			{
				//resets the atomic integer 
				ind.set(0);
			}
			
		}
		public  void fall(WordRecord wrd)
		{
			wrd.drop(15);//drops word by 15
				if(wrd.dropped())
				{
					//checks if word has reached red zone ,updates score and gets new word
					WordApp.score.missedWord();
					update();
					wrd.resetWord();

				}
		}

	}


