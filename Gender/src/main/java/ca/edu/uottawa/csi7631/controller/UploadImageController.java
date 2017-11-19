package ca.edu.uottawa.csi7631.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ca.edu.uottawa.csi7631.service.FacePPService;

/**
 * Servlet implementation class for upload image
 * 
 * @author Yicong Li
 *
 */
@WebServlet("/uploadImage")
public class UploadImageController extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 780678210486361181L;

    private static final String UPLOAD_DIRECTORY = "upload";

    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;

    /**
     * default constructor
     */
    public UploadImageController() {
        // TODO Auto-generated constructor stub
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            PrintWriter writer = response.getWriter();
            writer.println("You need enctype=multipart/form-data");
            writer.flush();
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
        upload.setHeaderEncoding("UTF-8");

        String uploadPath = getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;
        
        File uploadDir = new File(uploadPath);
        
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        
        try {
            List<FileItem> formItems = upload.parseRequest(request);
            
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        
                        if (!fileName.equals("")) {
                            String filePath = uploadPath + File.separator + fileName;
                            File storeFile = new File(filePath);
                            System.out.println(filePath);
                            item.write(storeFile);
                            FacePPService facePPService = new FacePPService();
                            String result = facePPService.getGenderResult(filePath);
                            request.setAttribute("image", "/Gender/" + UPLOAD_DIRECTORY + "/" + fileName);
                            request.setAttribute("message", result);
                        } else {
                            request.setAttribute("message", "Please select a picture");
                        };
                        
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            request.setAttribute("message", "error: " + e.getMessage());
        }
        getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
    }
}
