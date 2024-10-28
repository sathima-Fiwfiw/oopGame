package com.oop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EX6 extends JFrame{

	int threadX = -100;
	int mouseX;
	int mouseY;
	int move = 1;
	
	public EX6() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(1000,600));
		this.setLayout(new BorderLayout());
		
		JTextField input = new JTextField();
		input.setPreferredSize(new Dimension(50,20));
		
		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.black);
				g.drawLine(0, 0, threadX, 300);
			}
		};
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
//					if(threadX < 0) {
//						move = 1;
//					}
//					else if(threadX+100 > mouseX){
//						move = -1;
//					}
					threadX += move;
					try {
						Thread.sleep(2);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					repaint();
				}
			}
		}).start();
		
		panel.add(input);
		
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX6().setVisible(true);
	}
}
