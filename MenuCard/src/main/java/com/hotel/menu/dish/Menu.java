package com.hotel.menu.dish;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Menu {
	
	Scanner in=new Scanner(System.in);
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel","root","root");
		return connection;
	}
	
	public void createTable() throws ClassNotFoundException, SQLException {
		Statement statement=getConnection().createStatement();
		statement.executeUpdate("create table menu(dish varchar(20),quantity_in_kg long,price_per_kg long)");
	}
	
	public void insertDish() throws ClassNotFoundException, SQLException {
		System.out.println("enter dish name ?");
		String dish=in.next();
		System.out.println("enter quantity of a dish in kg ?");
		int quantity=in.nextInt();
		System.out.println("enter price in rupee per kg ?");
		long price=in.nextLong();
		
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
		boolean outOfStock=true;
		PreparedStatement ps=getConnection().prepareStatement("select * from menu where dish=?");
		System.out.println("enter dish name you want ?");
		String dish_search=in.next();
		ps.setString(1, dish_search);
		
		System.out.println("enter the quantity in kg ?");
		int quantity_search=in.nextInt();
		
		ResultSet rs=ps.executeQuery();
		while(rs.next()) {
			outOfStock=false;
			if(quantity_search<=rs.getInt(2)) {
			String dish=dish_search;
			int quantity=quantity_search;
			long price=quantity*rs.getLong(3);
			GenerateBill(dish, quantity, price);
			if(rs.getInt(2)-quantity_search == 0) {
				deleteDish(rs.getString(1));
			}else {
				updateQuantity(rs.getInt(2)-quantity_search,dish);
			}
			}else {
				System.out.println(dish_search+" of "+quantity_search+"kg is not available !");
			}
		}
		
		if(outOfStock){
		System.out.println("sorry,"+dish_search+" is out of stock !");
		getMenu();
		getDish();
	}
	}
	
	public void getMenu() throws ClassNotFoundException, SQLException {
		System.out.println("Available dishes are,");
		Statement st=getConnection().createStatement();
		
		ResultSet rs=st.executeQuery("select * from menu");
		while(rs.next()) {
			System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+rs.getLong(3));
		}
	}
	
	public void orderDish() throws ClassNotFoundException, SQLException {
		getMenu();
		getDish();
	}
	
	public void GenerateBill(String dish,int quantity,long price) throws ClassNotFoundException, SQLException {
		
		System.out.println("----------------BILL-----------------");
		System.out.println("DISH: "+dish);
		System.out.println("QUANTITY: "+quantity);
		System.out.println("PRICE: "+price);
		System.out.println("-------------------------------------");
		
	}

}
