package com.oop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EX5 extends JFrame{
	
	String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	String passCode;
	boolean wait;
	
	public EX5(int numThread) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(1200,600));
		this.setLayout(new BorderLayout());
		
		passCode = randomCode();
		
		MyLabel pass = new MyLabel(passCode,200);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,numThread));
		panel.setPreferredSize(new Dimension(0,200));
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(!wait) {
					wait = true;
				}
				else {
					wait = false;
				}
			}
		});
		
		for (int i = 0; i < numThread; i++) {
			MyLabel myLabel = new MyLabel(randomCode(), 50);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean cracked = false;
					while(!cracked) {
						while(wait) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
						
						String crackCode = randomCode();
						myLabel.setText(crackCode);
						
						if(passCode.equals(crackCode)) {
							myLabel.setForeground(Color.red);
							wait = true;
							cracked = true;
							break;
						}
						
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}).start();
			panel.add(myLabel);
		}
		
		this.add(pass,BorderLayout.CENTER);
		this.add(panel,BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public String randomCode() {
		String randomCode = "";
		for (int i = 0; i < 3; i++) {
			int ran = new Random().nextInt(code.length());
			randomCode += code.charAt(ran);
		}
		return randomCode;
	}

	public static void main(String[] args) {
		String value = JOptionPane.showInputDialog("Input Number Of Thread","8");
		int numThread = 8;
		try {
			numThread = Integer.parseInt(value);
			if(numThread < 1 || numThread > 8) {
				numThread = 8;
			}
		} catch (Exception e) {
			numThread = 8;
		}
		
		new EX5(numThread).setVisible(true);
	}
}

class MyLabel extends JLabel{
	
	MyLabel(String text,int fontSize){
		this.setText(text);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setVerticalAlignment(JLabel.CENTER);
		this.setFont(new Font(null, Font.PLAIN, fontSize));
	}
}
