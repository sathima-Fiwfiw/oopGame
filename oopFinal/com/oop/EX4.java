package com.oop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class EX4 extends JFrame{

	int threadX = -100;
	int mouseX;
	int mouseY;
	int move = 1;
	
	public EX4() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(1000,600));
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.yellow);
				g.fillOval(threadX, mouseY-50, 100, 100);
				g.setColor(Color.black);
				g.drawOval(threadX, mouseY-50, 100, 100);
			}
		};
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseY = e.getY();
				mouseX = e.getX();
			}
		});
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					if(threadX < 0) {
						move = 1;
					}
					else if(threadX+100 > mouseX){
						move = -1;
					}
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
		
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX4().setVisible(true);
	}
}
