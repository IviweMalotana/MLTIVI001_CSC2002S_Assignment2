import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import java.util.ArrayList;
import javax.swing.JPopupMenu;
import java.util.*;
import java.lang.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.lang.Math;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Flow{
	private static long startTime = 0;
	private static int frameX;
	private static int frameY;
	private static Terrain landdata;
	private static FlowPanel fp;
	public static Flow flow;
	private static Water waterdata;
	public static JFrame frame;
	public static Thread t1;
	public static Thread t2;
	public static Thread t3;
	public static Thread t4;
	private static int x;
	private static int y;
	private static int z;
	private static JButton resetB;
	private static JButton endB;
	private static JButton pauseB;
	private static JButton playB;
	public static JLabel timestep=new JLabel();

	public Flow(){}

	private static void tick(){
		startTime = System.currentTimeMillis();
	}

	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f;
	}

	public static void setupGUI(int frameX,int frameY,Terrain landdata, Water waterdata){

		Dimension fsize = new Dimension(800, 800);
    frame = new JFrame("Waterflow");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());

    JPanel g = new JPanel();
    g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));

		fp = new FlowPanel(flow,frame,landdata,waterdata,0,0,timestep); //creates a flow panel for water and terrain data (at the moment land is full, water is empty)

		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);

		// Create a mouselistener
		fp.addMouseListener(new MouseListener(){ //listens for clicks on panel
			public void mouseClicked(MouseEvent e){ //if mouse is clicked, store array of x and y coords of clicks

				x = e.getX(); //get x coordinate
		 		y = e.getY(); //get y coordinate
				z = 3; //depth is set to 5 water units = 0.05m (There will only be 3 water drop drop drops) (can give max number of water=3) and reach around to 3
				waterdata.PopulateWaterArray(x,y); //Populates the arraylist of values clicked on
				waterdata.CreateWaterSource(x,y,z);
				waterdata.deriveImage();
				frame.repaint();
			}

			public void mousePressed(MouseEvent e){
			}
			//@Override
			public void mouseReleased(MouseEvent e){
			}
			//@Override
			public void mouseEntered(MouseEvent e){
			}
			//@Override
			public void mouseExited(MouseEvent e){
			}
		});

		JPanel b = new JPanel();
	  b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

		// Creates a reset button to clear the water flow
		resetB = new JButton("Reset");
		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				waterdata.ClearAllWater();
				waterdata.ClearWaterArray();
				frame.dispose();
				setupGUI(frameX,frameY,landdata,waterdata);

			}
		});
		b.add(resetB);

		// Creates a pause button to stop the simulation temporarily
		pauseB = new JButton("Pause");

		pauseB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

			}
		});
		b.add(pauseB);

		playB = new JButton("Play");
		playB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				waterdata.Eraser();
				waterdata.deriveImage();
				frame.repaint();

				t1 = new Thread(fp = new FlowPanel(flow,frame,landdata,waterdata,0,(landdata.getPermuteSize()/4),timestep));
				t2 = new Thread(fp = new FlowPanel(flow,frame,landdata,waterdata,(landdata.getPermuteSize()/4),(landdata.getPermuteSize()/2),timestep));
				t3 = new Thread(fp = new FlowPanel(flow,frame,landdata,waterdata,landdata.getPermuteSize()/2,(3*landdata.getPermuteSize()/4),timestep));
				t4 = new Thread(fp = new FlowPanel(flow,frame,landdata,waterdata,3*landdata.getPermuteSize()/4,landdata.getPermuteSize(),timestep));

				t1.start();
				t2.start();
				t3.start();
				t4.start();

			}
		});
		b.add(playB);

		// Creates an end button to exit the simulation
		endB = new JButton("End");
		endB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// to do ask threads to stop

				frame.dispose();
			}
		});
		b.add(endB);
		b.add(timestep);
		g.add(b);

		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
    frame.setLocationRelativeTo(null);  // center window on screen
    frame.add(g); //add contents to window
    frame.setContentPane(g);
    frame.setVisible(true);

		Thread fpt = new Thread(fp);
		fpt.start();

	}

	public static void main(String[] args) {

		Flow flow = new Flow();

		Terrain landdata = new Terrain();

		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
		// landscape information from file supplied as argument

		landdata.readData(args[0]);
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();

		Water waterdata = new Water(frameX,frameY);
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata, waterdata));


		// to do: initialise and start simulation
	}
}
