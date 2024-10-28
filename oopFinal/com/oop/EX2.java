package com.oop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class EX2 extends  JFrame{

	public EX2() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800,600);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				while(true) {
					int h = getHeight();
					setSize(getWidth()-1,getHeight()-1);
					if(h == getHeight()) break;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX2().setVisible(true);
	}
}
