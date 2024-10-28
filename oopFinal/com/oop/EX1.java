package com.oop;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class EX1 extends JFrame{

	Font font = new Font(null,Font.PLAIN,50);
	public EX1(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(500,200));
		this.setLayout(new GridLayout(2,1));
		
		JTextField input = new JTextField();
		input.setFont(font);
		JTextField output = new JTextField();
		output.setFont(font);
		
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String letter = input.getText().trim();
				char [] character = letter.toCharArray();
				Arrays.sort(character);
				String sort = String.valueOf(character);
				output.setText(sort);
			}
		});
		
		this.add(input);
		this.add(output);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX1().setVisible(true);
	}
}
