package ca.edu.uottawa.csi7631.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.edu.uottawa.csi7631.service.FacePPService;

/**
 * Servlet implementation class for receive picture
 * @author Yicong Li
 *
 */
@WebServlet("/sendImage")
public class SendImageController extends HttpServlet{


    /**
     * 
     */
    private static final long serialVersionUID = -4094640193464905864L;

    /**
     * default constructor
     */
    public SendImageController() {
        // TODO Auto-generated constructor stub
        super();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String data = request.getParameter("data");
        String genderResult = "";
        
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(genderResult);
    }

}
