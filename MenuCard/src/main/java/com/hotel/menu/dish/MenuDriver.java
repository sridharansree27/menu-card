package com.hotel.menu.dish;

import java.sql.SQLException;
import java.util.Scanner;

public class MenuDriver {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Menu menu=new Menu();
		Scanner in=new Scanner(System.in);
		
		boolean exit=false;
		while(!exit) {
			System.out.println("------WELCOME TO TOMATO------");
	    	System.out.println("1.Create Table \n2.Add New Dish \n3.Get Menu \n4.Exit");

	    	System.out.println("Enter Your's Choice ?");
	    	int choice=in.nextInt();
	    	switch(choice) {
	    	case 1:{
	    		menu.createTable();
	    		break;
	    		}
	    	case 2:{
	    		menu.insertDish();
	    		break;
	    		}
	    	case 3:{
	    		menu.getMenu();
	    		break;
	    		}
	    	case 4:{
	    		exit=true;
	    		break;
	    		}
	    	default:{
	    		System.out.println("Invalid Choice Entered !");
	    		}
	        	}		
	        	}	
	            }	
                }