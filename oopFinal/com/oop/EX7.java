package com.oop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class EX7 extends JFrame{

	int startX;
	int startY;
	int stopX;
	int stopY;
	boolean crop;
	
	public EX7() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(417,500));
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		Image img = new ImageIcon(getClass().getClassLoader().getResource("scream.jpg")).getImage();
		
		JPanel panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if(!crop) {
					g.drawImage(img, 0, 0, 417, 500, this);
					g.setColor(Color.white);
					g.drawRect(startX, startY, stopX - startX, stopY - startY);
				}
				else {
					g.drawImage(img, startX, startY, stopX, stopY,startX ,startY, stopX, stopY, this);
				}
			}
		};
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				crop = false;
				startX = e.getX();
				startY = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				crop = true;
				repaint();
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				stopX = e.getX();
				stopY = e.getY();
				repaint();
			}
		});
		
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX7().setVisible(true);
	}
}
