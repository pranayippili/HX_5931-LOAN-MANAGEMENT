package com.hexaware.repo;

import com.hexaware.dao.ILoanRepository;
import com.hexaware.exception.InvalidLoanException;
import com.hexaware.entity.CarLoan;
import com.hexaware.entity.HomeLoan;
import com.hexaware.entity.Loan;
import com.hexaware.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ILoanRepositoryImpl implements ILoanRepository {
	

    DBUtil db = new DBUtil();
    Connection con = db.getConnection();
    Scanner sc = new Scanner(System.in);

	@Override
	public void applyLoan(Loan loan) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement ps = con.prepareStatement("insert into loan value(?,?,?,?,?,?,?)");
		
		ps.setInt(1, loan.getLoanId());
		ps.setInt(2, loan.getCustomer().getCustomerId());
		ps.setInt(3, loan.getPrincipalAmount());
		ps.setInt(4, loan.getInterestRate());
		ps.setInt(5, loan.getLoanTerm());
		ps.setString(6, loan.getLoanType());
		ps.setString(7, loan.getLoanStatus());
		
		int c = ps.executeUpdate();
		System.out.println(c+" record updated");
		
		
		if(loan.getLoanType() == "homeLoan") {
			System.out.println("Enter the property address: ");
			String address = sc.nextLine();
			
			System.out.println("Enter the property value");
			int propVal = sc.nextInt();
			
			int homeLoanId=0;
        	
        	try {
        		Statement stmt = con.createStatement();
        		ResultSet rs = stmt.executeQuery("select * from homeLoan order by home_loan_id desc limit 1");
        		
        		while(rs.next()) {
        			homeLoanId = rs.getInt(1)+1;
        		}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("cant get the homeLoan details for this for last customer id");
			}
			
			HomeLoan h1 = new HomeLoan(homeLoanId,loan.getLoanId(),loan.getCustomer(),loan.getPrincipalAmount(),loan.getInterestRate(),loan.getLoanTerm(),loan.getLoanType(),loan.getLoanStatus(),address,propVal);
			PreparedStatement ps1 = con.prepareStatement("insert into homeLoan value(?,?,?,?)");
			
			ps1.setInt(1, h1.getHomeLoanId());
			ps1.setInt(2,h1.getLoanId());
			ps1.setString(3, h1.getPropertyAddress());
			ps1.setInt(4, h1.getPropertyValue());
			
			int c1 = ps1.executeUpdate();
			System.out.println(c1+" record updated");
		}
		else if(loan.getLoanType() == "carLoan") {
			System.out.println("Enter the car modle: ");
			String modle = sc.next();
			
			System.out.println("Enter the car value");
			int carVal = sc.nextInt();
			
			int carLoanId=0;
        	
        	try {
        		Statement stmt = con.createStatement();
        		ResultSet rs = stmt.executeQuery("select * from carLoan order by car_loan_id desc limit 1");
        		
        		while(rs.next()) {
        			carLoanId = rs.getInt(1)+1;
        		}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("cant get the carLoan details for this for last customer id");
			}
			
			CarLoan h2 = new CarLoan(carLoanId,loan.getLoanId(),loan.getCustomer(),loan.getPrincipalAmount(),loan.getInterestRate(),loan.getLoanTerm(),loan.getLoanType(),loan.getLoanStatus(),modle,carVal);
			PreparedStatement ps2 = con.prepareStatement("insert into carLoan value(?,?,?,?)");
			
			ps2.setInt(1, h2.getCarLoanId());
			ps2.setInt(2,h2.getLoanId());
			ps2.setString(3, h2.getCarModel());
			ps2.setInt(4, h2.getCarValue());
			
			int c2 = ps2.executeUpdate();
			System.out.println(c2+" record updated");
		}
		
	}

	@Override
	public double calculateInterest(String loanId) throws InvalidLoanException {
		// TODO Auto-generated method stub
		try {
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from loan where loan_id="+loanId);
    		double ans = 0;
    		
    		while(rs.next()) {

//    			System.out.println("loanId, customerId, principal_amount, interest_rate, loan_term, loan_type, loan_status ");
//    			System.out.println(rs.getInt(1)+"   "+rs.getInt(2)+"   "+rs.getInt(3)+"   "+rs.getInt(4)+" "+rs.getInt(5)+" "+rs.getString(6)+" "+rs.getString(7));
    			int qloanId = rs.getInt(1);
    			int customerId = rs.getInt(2);
    			int principal_amount = rs.getInt(3);
    			int interest_rate = rs.getInt(4);
    			int loan_term = rs.getInt(5);
    			String loan_type = rs.getString(6);
    			String loan_status = rs.getString(7);
    			return ans = (principal_amount*interest_rate*loan_term)/12;
    		}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("cant get the customer table details");
		}
		
		return 0;
	}

	@Override
	public void loanStatus(String loanId) throws InvalidLoanException {
		// TODO Auto-generated method stub
		int creditScore = 0;
		try {
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery("select credit_score from customer where customer_id = ( select customer_id from loan where loan_id =  "+loanId+")");
    		    		
    		while(rs.next()) {
    			creditScore = rs.getInt(1);
    		}
    		
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("cant get the customer table details");
		}
		if(creditScore>650)System.out.println("Loan approved as your credit score is more then 650");
		else System.out.println("Loan not approved as your credit score is less then 650");
	}

	@Override
	public double calculateEMI(String loanId) throws InvalidLoanException {
		// TODO Auto-generated method stub
		
		try {
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from loan where loan_id="+loanId);
    		double emi = 0;
    		
    		while(rs.next()) {

    			int qloanId = rs.getInt(1);
    			int customerId = rs.getInt(2);
    			int principal_amount = rs.getInt(3);
    			int interest_rate = rs.getInt(4);
    			int monthly_intrest_rate = interest_rate/12;
    			int loan_term = rs.getInt(5);
    			String loan_type = rs.getString(6);
    			String loan_status = rs.getString(7);
    			emi = (principal_amount*monthly_intrest_rate*Math.pow((1+monthly_intrest_rate), loan_term))/Math.pow((1+monthly_intrest_rate), loan_term-1);
    		}
    		return emi;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("cant get the customer table details");
		}
		
		return 0;
	}

	@Override
	public void loanRepayment(String loanId, double amount) throws InvalidLoanException {
		// TODO Auto-generated method stub
		double emi = calculateEMI(loanId);
		if(emi>amount) System.out.println("Amount is too less please increase the amount");
		else {
			double noOfEmi = amount/emi;
			System.out.println("With this amount you can pay "+noOfEmi+" months of emi");
		}
	}

	@Override
	public void getAllLoan() {
		// TODO Auto-generated method stub
		try {
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery("select c.name, l.loan_id, l.customer_id, l.principal_amount, l.intrest_rate, l.loan_term, l.loan_type, l.loan_status from loan l left join customer c on l.customer_id = c.customer_id; ");
    		
    		
    		while(rs.next()) {
    			System.out.println("customer_name: "+rs.getString(1)+" loanId: "+rs.getInt(2)+" customerId: "+rs.getInt(3)+" principal_amount: "+rs.getInt(4)+" interest_rate: "+rs.getInt(5)+" loan_term: "+rs.getInt(6)+" loan_type: "+rs.getString(7)+" loan_status: "+rs.getString(8));

    		}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("cant get the customer table details");
		}
		
	}

	@Override
	public void getLoanById(String loanId) throws InvalidLoanException {
		// TODO Auto-generated method stub
		
		try {
    		Statement stmt = con.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from loan where loan_id ="+loanId);
    		
    		while(rs.next()) {

    			System.out.println("loanId, customerId, principal amount, interest_rate, loan_term, loan_type, loan_status");
    			System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getInt(5)+" "+rs.getString(6)+" "+rs.getString(7));
    		}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("cant get the customer table details");
		}
		
	}

}