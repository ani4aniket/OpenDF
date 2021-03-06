/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ucsc.score.apps.uploaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lk.ucsc.score.apps.messages.WorkMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;
import lk.ucsc.score.apps.models.Project;
import lk.ucsc.score.apps.models.Diskimage;
import javax.persistence.TypedQuery;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
/**
 *
 * @author Acer
 */
@WebServlet(name = "DiskImageUpload", urlPatterns = {"/DiskImageUpload"})
@MultipartConfig
public class DiskImageUpload extends HttpServlet {

    final String path = "uploads";
    //@PersistenceUnit(unitName="lk.ucsc.score.apps_OpenDF-web_war_1.0-SNAPSHOTPU")
    //private EntityManagerFactory emf; 
    DataSource db;
    @PersistenceContext(unitName="lk.ucsc.score.apps_OpenDF-web_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            


            final Part filePart = request.getPart("file");
            final String fileName = new Date()+"_"+getFileName(filePart);
            OutputStream outStream = null;
            InputStream filecontent = null;
            final PrintWriter writer = response.getWriter();

            try {
                outStream = new FileOutputStream(new File(path + File.separator
                        + fileName));
                filecontent = filePart.getInputStream();

                int read = 0;
                final byte[] bytes = new byte[1024];

                while ((read = filecontent.read(bytes)) != -1) {
                    outStream.write(bytes, 0, read);
                }
                

                String idProject = request.getParameter("idProject");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String createdDate = request.getParameter("createdDate");
                //EntityManager em =  emf.createEntityManager();   
                Project project = em.find(Project.class, Integer.parseInt(idProject));
                System.out.println(project.getName());
                //Diskimage diskimage = new Diskimage(200);
                //diskimage.setName(name);
                System.out.println("ed");
                //diskimage.setProjectidProject(project);
                //em.persist(diskimage);
                //em.getEntityManagerFactory().getCache().evictAll();
                
                out.println("{ \"file\": \""+fileName+"\"}");
                System.out.println("sending mesg");
                //new WorkMessage().send(path);
                System.out.println("Mssg sent ");

            } catch (FileNotFoundException fne) {
                System.out.println("error");
                System.out.println(fne);
                out.println("{\"error\": \"" + fne.getMessage() + "\"}");

            } finally {
                if (out != null) {
                    out.close();
                }
                if (filecontent != null) {
                    filecontent.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
        }catch (Exception fne) {System.out.println("error");
                System.out.println(fne);
                out.println("{\"error\": \"" + fne.getMessage() + "\"}");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void init() throws ServletException {
        super.init();
        File uploads = new File(path);
        if (!uploads.exists()) {
            uploads.mkdir();
        }try{
        InitialContext ic = new InitialContext();
        db = (javax.sql.DataSource)ic.lookup("OpenDF");}catch (Exception e) {
               System.out.println("Mssg sent ");


            }
    }

    private String getFileName(Part filePart) {
        String header = filePart.getHeader("content-disposition");
        for (String content : header.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
