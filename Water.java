import java.io.File;
import java.awt.image.*;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Water{

	private int dimx; //x dimension
	private int dimy; //y dimension
	private BufferedImage img; // greyscale image for displaying the terrain top-down
	private int[][] water;
	private int depth;
	private float waterheight;
	private int[][] visibility;
	ArrayList<Integer> WaterArray; //ArrayList to store all x,y coordinates clicked on

	public Water(int dimX, int dimY){
		visibility=new int[dimX][dimY];
		water=new int[dimX][dimY];
		WaterArray= new ArrayList<Integer>();
		dimx = dimX;
		dimy = dimY;
	}

	void setVisibility(int a,int b,int c){
		visibility[a][b]=c;
	}

	int getVisibility(int a,int b){
		return visibility[a][b];
	}

	void setDepth(int i,int j,int val){
		water[i][j]=val;
	}

	int getDepth(int i,int j){
		return water[i][j];
	}

	float getWaterHeight(int i, int j){
		return ((float) getDepth(i,j)/(float)100);
	}
	//Accessor methods for dimensions
	int getDimX(){
		return dimx;
	}
	int getDimY(){
		return dimy;
	}

	public void ClearAllWater(){
		for (int i=0;i<dimx;i++){
			for (int j=0;j<dimy;j++){
				water[i][j]=0;
				visibility[i][j]=0;
			}
		}
		deriveImage();
	}


	//Add to the Arraylist of values clicked on
	public void PopulateWaterArray(int a,int b){
		WaterArray.add(a);
		WaterArray.add(b);
	}

	//Access the Arraylist of values clicked on
	public ArrayList<Integer> getWaterArray(){
		return WaterArray;
	}

	//Clear the Arraylist of values clicked on
	public void ClearWaterArray(){
		WaterArray.clear();
	}

	public BufferedImage getImage() {
		  return img;
	}


	void deriveImage(){
		img = new BufferedImage(dimy, dimx, BufferedImage.TYPE_INT_ARGB);
		for(int x=0; x < dimx; x++){
			for(int y=0; y < dimy; y++){
				if (visibility[x][y]==0){
					Color col = new Color(0,0,0,0);
					img.setRGB(x,y,col.getRGB());
				}
				else{
					Color col = new Color(0,0,255);
 				 	img.setRGB(x, y, col.getRGB());
					//System.out.println("image derived for "+x+" "+y);
				}
			}
		}
	}

	//creates big water
	void CreateWaterSource(int a,int b,int c){
		if (!((a<(3-1))&&(a>dimx-(3+1))&&(b<(3-1))&&(b>dimy-(3+1)))){
			for (int i=a-3;i<a+(3+1);i++){
				for (int j=b-3;j<b+(3+1);j++){
					water[i][j]=c; //set the water coordinates clicked on to have specified depth
					visibility[i][j]=1; //set the visibility at x,y to true
					//System.out.println(i+" "+j+" = "+water[i][j]);
				}
			}
			for (int i=a-c;i<a+(c+1);i++){
				for (int j=b-c;j<b+(c+1);j++){
					water[i][j]=c; //set the water coordinates clicked on to have specified depth
					//visibility[i][j]=1; //set the visibility at x,y to true
				  //System.out.println(i+" "+j+" = "+water[i][j]);
				}
			}
		}
		else{
			water[a][b]=0;
			visibility[a][b]=0;
		}
	}

	void Eraser(){
		for (int k=0;k<(WaterArray.size()-1);k+=2){
			int a = WaterArray.get(k);
			int b = WaterArray.get(k+1);
			for (int i=a-3;i<(a+3+1);i++){
				for (int j=b-3;j<b+(3+1);j++){
					visibility[i][j]=0; //set the visibility at x,y to true
				}
			}
		}
	}

	void UpdateWater(int a,int b,int c){
		water[a][b]=c;
		visibility[a][b]=1;
	}
}
