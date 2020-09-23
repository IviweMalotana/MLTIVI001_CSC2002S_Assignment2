import java.awt.Graphics;
import javax.swing.JPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.lang.*;
import java.lang.Math;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import java.util.concurrent.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class FlowPanel extends JPanel implements Runnable{

	JFrame frame;
	private Terrain land;
	private Water water;
	private int start;
	private int end;
	public int count;
	public static CyclicBarrier barrier = new CyclicBarrier(4);
	public TimeUnit time = TimeUnit.SECONDS;
	public long timeToSleep = 1L;
	private static Flow flow;
	JLabel timestep;

	FlowPanel(Flow fl,JFrame f, Terrain terrain, Water w, int st, int en, JLabel jl) {
		flow=fl;
		frame=f;
		land=terrain;
		water=w;
		start = st;
		end = en;
		timestep=jl;
	}



	// responsible for painting the terrain and water
	// as images
	@Override
    protected void paintComponent(Graphics g) {
			int width = getWidth();
			int height = getHeight();

			super.paintComponent(g);

			// draw the landscape in greyscale as an image
			if (land.getImage() != null){
				g.drawImage(land.getImage(),0,0,null);
			}
			if (water.getImage()!=null){
				g.drawImage(water.getImage(),0,0,null);
			}
		}

	synchronized void compute(int a, int b){
		// check  if (a,b) is a source of water or has water at all, it must also not be at the edge
		if (water.getDepth(a,b)>0){

			try{
				barrier.await();
			}
			catch (InterruptedException | BrokenBarrierException e){
				e.printStackTrace();
			}
			try{
				time.sleep(timeToSleep);
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}

			timestep.setText(Integer.toString(count));
			water.deriveImage();
			frame.repaint();
			count++;

			if ((a>0)&&(b>0)&&(a<(land.getDimX()-1))&&(b<(land.getDimY()-1))){

				float v = land.getLandHeight(a,b)+water.getWaterHeight(a,b);
				float N = land.getLandHeight(a,b-1)+water.getWaterHeight(a,b-1);
				float S = land.getLandHeight(a,b+1)+water.getWaterHeight(a,b+1);
				float W = land.getLandHeight(a-1,b)+water.getWaterHeight(a-1,b);
				float E = land.getLandHeight(a+1,b)+water.getWaterHeight(a+1,b);
				float NE = land.getLandHeight(a+1,b-1)+water.getWaterHeight(a+1,b-1);
				float NW = land.getLandHeight(a-1,b-1)+water.getWaterHeight(a-1,b-1);
				float SE = land.getLandHeight(a+1,b+1)+water.getWaterHeight(a+1,b+1);
				float SW = land.getLandHeight(a-1,b+1)+water.getWaterHeight(a-1,b+1);

				//Populate an array from smallest to largest value
				float[] arr = {N,S,W,E,NE,NW,SE,SW};
				Arrays.sort(arr);
				//int original = water.getDepth(a,b); //get the initial water depth of the point

				// The source of water can only give "original" amount of water before it will be reduced to nothing.
				// So the array must iterate only up to the maximum amount of water that the source can give

				if (((N<v)&&(arr[0]==N))&&(water.getVisibility(a,b-1)<1)){ //If the block north of (a,b) is shorter, then north gets a unit of water
					water.UpdateWater(a,b-1,water.getDepth(a,b-1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a,b-1);

				}
				else if ((S<v)&&(arr[0]==S)&&(water.getVisibility(a,b+1)<1)){
					water.UpdateWater(a,b+1,water.getDepth(a,b+1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a,b+1);

				}
				else if ((W<v)&&(arr[0]==W)&&(water.getVisibility(a-1,b)<1)){
					water.UpdateWater(a-1,b,water.getDepth(a-1,b)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a-1,b);
				}
				else if ((E<v)&&(arr[0]==E)&&(water.getVisibility(a+1,b)<1)){
					water.UpdateWater(a+1,b,water.getDepth(a+1,b)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a+1,b);
				}
				else if ((NE<v)&&(arr[0]==NE)&&(water.getVisibility(a+1,b-1)<1)){
					water.UpdateWater(a+1,b-1,water.getDepth(a+1,b-1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a+1,b-1);
				}
				else if ((NW<v)&&(arr[0]==NW)&&(water.getVisibility(a-1,b-1)<1)){
					water.UpdateWater(a-1,b-1,water.getDepth(a-1,b-1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a-1,b-1);
				}
				else if ((SE<v)&&(arr[0]==SE)&&(water.getVisibility(a+1,b+1)<1)){
					water.UpdateWater(a+1,b+1,water.getDepth(a+1,b+1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a+1,b+1);
				}
				else if ((SW<v)&&(arr[0]==SW)&&(water.getVisibility(a-1,b+1)<1)){
					water.UpdateWater(a-1,b+1,water.getDepth(a-1,b+1)+1);
					water.UpdateWater(a,b,water.getDepth(a,b)-1);
					compute(a-1,b+1);
				}
				else{
					water.UpdateWater(a,b,water.getDepth(a,b));
				}
			}
			else{
				water.setDepth(a,b,0);
				water.setVisibility(a,b,0);
			}
		}
		else{
			water.setDepth(a,b,0);
			water.setVisibility(a,b,0);
		}

	}

	public void run(){
		count=0;
		for (int i=start;i<end;i++){
				compute(land.getx(i),land.gety(i));
		}

	}

}
