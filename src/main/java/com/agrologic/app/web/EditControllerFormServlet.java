
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package com.agrologic.app.web;

//~--- non-JDK imports --------------------------------------------------------

import com.agrologic.app.dao.ControllerDao;
import com.agrologic.app.dao.impl.ControllerDaoImpl;
import com.agrologic.app.model.ControllerDto;
import com.agrologic.app.model.UserDto;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author JanL
 */
public class EditControllerFormServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /** Logger for this class and subclasses */
        final Logger logger = Logger.getLogger(EditControllerFormServlet.class);

        logger.error("Unauthorized access!");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            if (!CheckUserInSession.isUserInSession(request)) {
                logger.error("Unauthorized access!");
                request.getRequestDispatcher("./login.jsp").forward(request, response);
            } else {
                UserDto user        = (UserDto) request.getSession().getAttribute("user");
                String  forwardLink = "";

                if (user.getRole() == UserRole.ADMINISTRATOR) {
                    forwardLink = "./cellinkinfo.html";
                } else {
                    forwardLink = "./cellink-setting.html";
                }

                Long   userId                  = Long.parseLong(request.getParameter("userId"));
                Long   cellinkId               = Long.parseLong(request.getParameter("cellinkId"));
                Long   controllerId            = Long.parseLong(request.getParameter("controllerId"));
                String title                   = request.getParameter("Ntitle");
                String netName                 = request.getParameter("Nnetname");
                Long   programId               = Long.parseLong(request.getParameter("NprogramId"));
                String newControllNameList     = request.getParameter("Ncontrollernamelist");
                String newControllName         = request.getParameter("Ncontrollername");
                String newControllNameCheckBox = request.getParameter("newControllerName");
                String active                  = request.getParameter("Nactive");

                try {
                    ControllerDao controllerDao = new ControllerDaoImpl();
                    ControllerDto  controller    = controllerDao.getById(controllerId);

                    controller.setNetName(netName);
                    controller.setTitle(title);
                    controller.setCellinkId(cellinkId);
                    controller.setProgramId(programId);

                    if ((newControllNameCheckBox != null) && "ON".equals(newControllNameCheckBox.toUpperCase())) {
                        controller.setName(newControllName);
                    } else {
                        controller.setName(newControllNameList);
                    }

                    if ((active != null) && "ON".equals(active.toUpperCase())) {
                        controller.setActive(true);
                    } else {
                        controller.setActive(false);
                    }

                    controllerDao.update(controller);
                    logger.info("Controller " + controller + " successfully updated !");
                    request.getSession().setAttribute("message",
                                                      "Controller with ID " + controller.getId()
                                                      + " successfully updated !");
                    request.getSession().setAttribute("error", false);
                    request.getRequestDispatcher(forwardLink + "?userId" + userId + "&cellinkId"
                                                 + cellinkId).forward(request, response);
                } catch (SQLException ex) {
                    logger.error("Error occurs while updating controller !");
                    request.getSession().setAttribute("message", "Error occurs while updating controller  .");
                    request.getSession().setAttribute("error", true);
                    request.getRequestDispatcher(forwardLink + "?userId" + userId + "&cellinkId"
                                                 + cellinkId).forward(request, response);
                }
            }
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }    // </editor-fold>
}


//~ Formatted by Jindent --- http://www.jindent.com
