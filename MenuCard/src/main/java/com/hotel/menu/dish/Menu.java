package com.hotel.menu.dish;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

	Scanner in=new Scanner(System.in);
	 static boolean outOfStock=true;
	 static List<String> orders=new ArrayList<String>();
	 static long totalPrice=0;

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel","root","root");
		return connection;
		}

	public void createTable() throws ClassNotFoundException, SQLException {
		Statement statement=getConnection().createStatement();
		statement.executeUpdate("create table menu(dish varchar(20),quantity_in_kg long,price_per_kg long)");
		}

	public void insertDish(String dish,int quantity,long price) throws ClassNotFoundException, SQLException {

		PreparedStatement search=getConnection().prepareStatement("select * from menu where dish=?");
		search.setString(1, dish);
		ResultSet rs=search.executeQuery();
		if(rs.next()) {
			updatePrice(price, rs.getString(1));
			updateQuantity(quantity+rs.getInt(2), rs.getString(1));
		}else {
			PreparedStatement ps=getConnection().prepareStatement("insert into menu values(?,?,?)");
			ps.setString(1, dish);
			ps.setInt(2, quantity);
			ps.setLong(3, price);

			ps.executeUpdate();
			}
		}

	public void deleteDish(String dish) throws ClassNotFoundException, SQLException {
		PreparedStatement ps=getConnection().prepareStatement("delete from menu where dish=?");
		ps.setString(1, dish);

		if(ps.executeUpdate()==0) {
			System.out.println("sorry,"+dish+" is already not available !");
			}
		}

	public void updatePrice(long price,String dish) throws ClassNotFoundException, SQLException {
		PreparedStatement ps=getConnection().prepareStatement("update menu set price_per_kg=? where dish=?");

		ps.setLong(1, price);
		ps.setString(2, dish);

		ps.executeUpdate();
		}

	public void updateQuantity(int quantity,String dish) throws ClassNotFoundException, SQLException {
		PreparedStatement ps=getConnection().prepareStatement("update menu set quantity_in_kg=? where dish=?");

		ps.setInt(1, quantity);
		ps.setString(2, dish);

		ps.executeUpdate();
		}

	public void getDish() throws ClassNotFoundException, SQLException {
		
		boolean isNext=true;
		System.out.println("Do you want to order true/false ?");
		isNext=in.nextBoolean();

		while(isNext) {
		outOfStock=true;
		boolean isPrevious=false;
		PreparedStatement ps=getConnection().prepareStatement("select * from menu where dish=?");
	    	System.out.println("enter dish name you want ?");
	    	String dish=in.next();
	    	ps.setString(1, dish);

		System.out.println("enter quantity in kg ?");
		int quantity=in.nextInt();

	    	ResultSet rs=ps.executeQuery();
		    while(rs.next()) {
		    	outOfStock=false;
			    if(quantity<=rs.getInt(2)) {
				    if(orders.isEmpty()) {
				        orders.add(dish);
				        orders.add(String.valueOf(quantity));
			        	orders.add(String.valueOf(quantity*rs.getLong(3)));
			        	totalPrice=totalPrice+quantity*rs.getLong(3);
			        	}else {
					        for(String order:orders) {					   
						        if(dish.equals(order)) {						        	
							        isPrevious=true;
						        	orders.set(orders.indexOf(order)+1, String.valueOf(Integer.parseInt(orders.get(orders.indexOf(order)+1)) + quantity ));
						        	orders.set(orders.indexOf(order)+2, String.valueOf(Integer.parseInt(orders.get(orders.indexOf(order)+2)) + quantity*rs.getLong(3)) );
						        	totalPrice=totalPrice+quantity*rs.getLong(3);
						        	break;
						        	}
						        }		        
					        if(!isPrevious) {
						    orders.add(dish);
					    	    orders.add(String.valueOf(quantity));
						    orders.add(String.valueOf(quantity*rs.getLong(3)));
						    totalPrice=totalPrice+quantity*rs.getLong(3);
					    	if(rs.getInt(2)-quantity == 0) {
						    	deleteDish(rs.getString(1));
						    	}else {
						    		updateQuantity(rs.getInt(2)-quantity,dish);
						    		}
					    	break;
					    	}
					        }
				    if(rs.getInt(2)-quantity == 0) {
				    	deleteDish(rs.getString(1));
				    	}else {
				    		updateQuantity(rs.getInt(2)-quantity,dish);
				    		}
				    }else {
				    	System.out.println(dish+" of "+quantity+"kg is not available !");
				    	}
			    }
		    if(outOfStock){
		    	System.out.println(dish+" is out of stock !");
		        getMenu();
		        isNext=false;
		        }else {
		        	getMenu();
			        isNext=false;
			    	}
		    }
		}

	public void getMenu() throws ClassNotFoundException, SQLException {

		boolean outOfStocks=true;

		Statement st=getConnection().createStatement();
		ResultSet rs=st.executeQuery("select * from menu");

		if(true) {
			while(rs.next()) {
				outOfStocks=false;
			    System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+rs.getLong(3));
			    }
			if(!outOfStocks) {
				getDish();
				}else{
					System.out.println("Sorry, no dishes available now !");
					}
			}
		}
	
	public void cancelOrder(String dish, int quantity) throws ClassNotFoundException, SQLException {
		
		int totalQuantity=0;
		boolean isPresent=false;
		
		for(String order:orders) {					   
	        if(dish.equals(order)) {
	        	if(quantity<=Integer.parseInt(orders.get(orders.indexOf(dish)+1))) {
	        		
	        		totalQuantity=Integer.parseInt(orders.get(orders.indexOf(dish)+1));
	        		
	        		if(Integer.parseInt(orders.get(orders.indexOf(dish)+1))-quantity==0) {
	        			
	        			long totalPrice=Long.parseLong(orders.get(orders.indexOf(dish)+2));
	        			
	        			orders.remove(orders.indexOf(dish)+1);
	        			orders.remove(orders.indexOf(dish)+1);
	        			orders.remove(orders.indexOf(dish));
	        					        		
		        		Statement st=getConnection().createStatement();
		        		ResultSet rs=st.executeQuery("select * from menu");
		        		
		        		while(rs.next()) {
		        			if(rs.getString(1).equals(dish)) {
		        				isPresent=true;
		        				Menu.totalPrice=Menu.totalPrice-(quantity*rs.getLong(3));
		        				updateQuantity(rs.getInt(2)+quantity, dish);
		        				}
		        			}
		        		if(!isPresent) {	        				
	        				long pricePerUnit=totalPrice/totalQuantity;
	        				Menu.totalPrice=Menu.totalPrice-(quantity*pricePerUnit);
	        				insertDish(dish, quantity, pricePerUnit);
	        			    }	        			
	        			break;
	        		}else {
	        		orders.set(orders.indexOf(dish)+1,String.valueOf(Integer.parseInt(orders.get(orders.indexOf(dish)+1))-quantity) );
	        		
	        		Statement st=getConnection().createStatement();
	        		ResultSet rs=st.executeQuery("select * from menu");
	        		
	        		while(rs.next()) {
	        			if(rs.getString(1).equals(dish)) {
	        				isPresent=true;
	        				
	        				if(orders.contains(dish)) {
	        				orders.set(orders.indexOf(dish)+2,String.valueOf(Integer.parseInt(orders.get(orders.indexOf(dish)+1))*rs.getLong(3)));
	        				}
	        				
	        				totalPrice=totalPrice-(quantity*rs.getLong(3));
	        				updateQuantity(rs.getInt(2)+quantity, dish);
	        				}
	        			}
	        		if(!isPresent) {
        				long totalPrice=Long.parseLong(orders.get(orders.indexOf(dish)+2));
        				long pricePerUnit=totalPrice/totalQuantity;
        				
        				if(orders.contains(dish)) {
        				orders.set(orders.indexOf(dish)+2, String.valueOf(Long.parseLong(orders.get(orders.indexOf(dish)+2))-(quantity*pricePerUnit)));
        				}
        				
        				Menu.totalPrice=Menu.totalPrice-(quantity*pricePerUnit);
        				insertDish(dish, quantity, pricePerUnit);
        			    }
	        		}
	        		}
	        	}
	        }
		}

	public void GenerateBill(List<String> orders,long total) throws ClassNotFoundException, SQLException {

		System.out.println("----------------BILL-----------------");
		for(int i=0;i<orders.size();++i) {
			System.out.println("DISH : "+orders.get(i)+" QUANTITY : "+orders.get(++i)+"KG AMOUNT : "+orders.get(++i)+"RUPEE");
			}
		System.out.println("TOTAL : "+total+"RUPEE");
		System.out.println("-------------------------------------");
		}
	}
