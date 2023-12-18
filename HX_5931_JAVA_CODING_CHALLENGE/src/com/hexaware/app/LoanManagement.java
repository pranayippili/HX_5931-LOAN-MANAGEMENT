package com.hexaware.app;

import com.hexaware.dao.ILoanRepository;
import com.hexaware.entity.Customer;
import com.hexaware.entity.Loan;
import com.hexaware.exception.InvalidLoanException;
import com.hexaware.repo.ILoanRepositoryImpl;
import com.hexaware.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class LoanManagement {
    public static void main(String[] args) {
    	ILoanRepository loanServiceProvider = new ILoanRepositoryImpl();
        Scanner scanner = new Scanner(System.in);
        DBUtil db = new DBUtil();
        Connection con = db.getConnection();

        while (true) {
        	System.out.println();
        	System.out.println("*************************************************");
            System.out.println("Loan Management System Menu:");
            System.out.println("1. Apply Loan");
            System.out.println("2. Get All Loans");
            System.out.println("3. Get Loan by ID");
            System.out.println("4. Calculate Intrest");
            System.out.println("5. Loan Status");
            System.out.println("6. Calculate EMI");
            System.out.println("7. Loan Repayment");
            System.out.println("8. Exit");
        	System.out.println("*************************************************");
        	System.out.println("*************************************************");
        	System.out.println();

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    // Apply Loan
                	System.out.println("Are you an existing customer?");
                	System.out.println("1. Yes ");
                	System.out.println("2. No ");
                	int choice1 = scanner.nextInt();
                	scanner.nextLine();
                	Customer customer = new Customer();
                	
                	
                	if(choice1 == 1) {
                    	System.out.print("Please enter your coustemer id ");
                    	int customerId = scanner.nextInt();
                    	
                    	String name = "", email = "", phoneNo = "", address = "";
                    	int creditScore = 0;
                    	try {
                    		Statement stmt = con.createStatement();
                    		ResultSet rs = stmt.executeQuery("select * from customer where customer_id = "+customerId);
                    		
                    		while(rs.next()) {
                    			customerId = rs.getInt(1);
                        		name = rs.getString(2);
                        		email = rs.getString(3);
                        		phoneNo = rs.getString(4);
                        		address = rs.getString(5);
                        		creditScore = rs.getInt(6);
                    		}
                    		
                    		
						} catch (SQLException e) {
							e.printStackTrace();
							System.err.println("cant get the customer details for this ed");
						}
                    	Customer c = new Customer(customerId,name,email,phoneNo,address,creditScore);
                    	customer = c;
                		
                	}else if(choice1 == 2) {
                		System.out.println("before applying for loan you have to create an account. Lets create it for you...");
                		
                		int customerId = 0;
                		
                		try {
                    		Statement stmt = con.createStatement();
                    		ResultSet rs = stmt.executeQuery("select * from customer order by customer_id desc limit 1");
                    		
                    		while(rs.next()) {
                    			customerId = rs.getInt(1)+1;
                    		}
    					} catch (SQLException e) {
    						e.printStackTrace();
    						System.err.println("cant get the customer table details");
    					}
                		
                		System.out.println("Please enter your name: ");
                		String name = scanner.nextLine();
                		
                		System.out.println("Please enter your Email: ");
                		String email = scanner.nextLine();
                		
                		System.out.println("please enter your phone number: ");
                		String phoneNo = scanner.nextLine();
                		
                		System.out.println("please enter your address");
                		String address = scanner.nextLine();
                		
                		System.out.println("Please enter your credit score");
                		int creditScore = scanner.nextInt();
                		
                		Customer c = new Customer(customerId,name,email,phoneNo,address,creditScore);
                    	customer = c;
                    	
                    	PreparedStatement ps;
						try {
							ps = con.prepareStatement("insert into customer value(?,?,?,?,?,?)");
							ps.setInt(1, customerId);
	                		ps.setString(2, name);
	                		ps.setString(3, email);
	                		ps.setString(4, phoneNo);
	                		ps.setString(5, address);
	                		ps.setInt(6, creditScore);
	                		int ct = ps.executeUpdate();
	                		System.out.println(ct+" record updated");
						} catch (SQLException e) {
							e.printStackTrace();
						}
                		System.out.println("You have been added as customer in our bank");
                		
                		
                	}else {
                		System.out.println("you typed the wrong input");
                		break;
                	}
                	
                	System.out.print("Please enter your principal amount ");
                	int principal = scanner.nextInt();
                	
                	System.out.println("plaese select your loan type: ");
                	System.out.println("1. Home Loan (intrest rate - 9%)");
                	System.out.println("2. Car Loan (intrest rate - 12%)");
                	
                	int choice2 = scanner.nextInt();
                	
                	String loanType ="";
                	int interest = 0;
                	
                	if(choice2 == 1) {
                		loanType = "homeLoan";
                		interest = 9;
                	}else if(choice2 == 2) {
                		loanType = "carLoan";
                		interest = 12;
                	}
                	System.out.println("Enter loanterm in months");
                	int loanTerm = scanner.nextInt();
                	String loanStatus = (customer.getCreditScore()>650) ? "approved":"pending";
                	int loanId=0;
                	
                	try {
                		Statement stmt = con.createStatement();
                		ResultSet rs = stmt.executeQuery("select * from loan order by loan_id desc limit 1");
                		
                		while(rs.next()) {
                			loanId = rs.getInt(1)+1;
                		}
					} catch (SQLException e) {
						e.printStackTrace();
						System.err.println("cant get the customer details for this for last customer id");
					}
                	
                	Loan loan = new Loan(loanId,customer,principal,interest,loanTerm,loanType,loanStatus);

                	try {
                		loanServiceProvider.applyLoan(loan);
					} catch (SQLException e) {
						e.printStackTrace();
					}
                    break;
                    
                case 2:
                    loanServiceProvider.getAllLoan();
                    break;
                case 3:
                    // Get Loan by ID
                    System.out.print("Enter loan ID: ");
                    String loanId1 = scanner.nextLine();
                    try {
                        loanServiceProvider.getLoanById(loanId1);
                    } catch (InvalidLoanException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case 4:
                    // Calculate Interest
                	System.out.print("Enter loan ID: ");
                    String loanId2 = scanner.nextLine();
                    double ans = 0;
					try {
						ans = loanServiceProvider.calculateEMI(loanId2);
					} catch (InvalidLoanException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("yaur intrest is "+ans);
                    break;
                case 5:
                	System.out.print("Enter loan ID: ");
                    String loanId3 = scanner.nextLine();
					try {
						loanServiceProvider.loanStatus(loanId3);
					} catch (InvalidLoanException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case 6:
                	System.out.print("Enter loan ID: ");
                    String loanId4 = scanner.nextLine();
                    double ans1 = 0;
					try {
						ans1 = loanServiceProvider.calculateEMI(loanId4);
					} catch (InvalidLoanException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("yaur intrest is "+ans1);
                    break;
                case 7:
                	System.out.print("Enter loan ID: ");
                    String loanId5 = scanner.nextLine();
                    System.out.println("Enter the amount you want to enter");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
					try {
						loanServiceProvider.loanRepayment(loanId5,amount);
					} catch (InvalidLoanException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case 8:
                    System.out.println("Exiting Loan Management System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }
}