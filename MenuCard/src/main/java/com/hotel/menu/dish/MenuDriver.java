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
	    	System.out.println("1.Get Menu \n2.Cancel Order \n3.Get Bill \n4.Exit");

	    	System.out.println("Enter Your's Choice ?");
	    	int choice=in.nextInt();
	    	
	    	switch(choice) {
	    	case 1:{
	    		menu.getMenu();
	    		break;
	    		}
	    	case 2:{
	    		System.out.println("Enter dish name to cancel ?");
	    		String dish=in.next();
	    		System.out.println("Enter quantity in Kg ?");
	    		int quantity=in.nextInt();
	    		menu.cancelOrder(dish, quantity);
	    		break;
	    	}
	    	case 3:{
	    		if(!menu.orders.isEmpty()) {
	    			menu.GenerateBill(menu.orders,menu.totalPrice);
	    			}
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