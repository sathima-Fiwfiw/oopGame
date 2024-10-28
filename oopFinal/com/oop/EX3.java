package com.oop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EX3 extends JFrame{

	int fontSize = 40;
	
	public EX3() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800,600));
		this.setLayout(new BorderLayout());
		
		JTextField input = new JTextField();
		input.setPreferredSize(new Dimension(600,40));
		input.setFont(new Font(null,Font.PLAIN,30));
		
		JPanel inputPanel = new JPanel();
		inputPanel.setPreferredSize(new Dimension(0,50));
		inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		inputPanel.setBackground(Color.DARK_GRAY);
		
		JLabel outputLabel = new JLabel("Hi!");
		outputLabel.setHorizontalAlignment(JLabel.CENTER);
		outputLabel.setFont(new Font(null,Font.PLAIN,fontSize));
		
		JButton smallFont = new JButton("<<");
		smallFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fontSize -= 10;
				if(fontSize < 0) {
					fontSize = 0;
				}
				outputLabel.setFont(new Font(null,Font.PLAIN,fontSize));
			}
		});
		
		JButton bigFont = new JButton(">>");
		bigFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fontSize += 10;
				outputLabel.setFont(new Font(null,Font.PLAIN,fontSize));
			}
		});
		
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				outputLabel.setText(input.getText());
			}
		});
		
		inputPanel.add(input);
		inputPanel.add(smallFont);
		inputPanel.add(bigFont);
		
		this.add(inputPanel,BorderLayout.NORTH);
		this.add(outputLabel,BorderLayout.CENTER);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public static void main(String[] args) {
		new EX3().setVisible(true);
	}
}
