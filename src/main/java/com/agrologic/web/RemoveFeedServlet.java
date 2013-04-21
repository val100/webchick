
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package com.agrologic.web;

//~--- non-JDK imports --------------------------------------------------------

import com.agrologic.dao.ControllerDao;
import com.agrologic.dao.FeedDao;
import com.agrologic.dao.FlockDao;
import com.agrologic.dao.impl.ControllerDaoImpl;
import com.agrologic.dao.impl.FeedDaoImpl;
import com.agrologic.dao.impl.FlockDaoImpl;
import com.agrologic.dto.ControllerDto;
import com.agrologic.dto.FeedDto;
import com.agrologic.dto.FlockDto;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author JanL
 */
public class RemoveFeedServlet extends HttpServlet {

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
        final Logger logger = Logger.getLogger(SaveBeginEndForm.class);

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            Long cellinkId = Long.parseLong(request.getParameter("cellinkId"));
            Long flockId   = Long.parseLong(request.getParameter("flockId"));
            Long feedId    = Long.parseLong(request.getParameter("feedId"));

            try {
                FeedDao feedDao = new FeedDaoImpl();
                FeedDto  feed    = feedDao.getById(feedId);

                if (feed == null) {
                    logger.info("Feed " + feedId + " can't be removed");
                    request.getRequestDispatcher("./rmctrl-add-feed.jsp?celinkId=" + cellinkId + "&flockId="
                                                 + flockId).forward(request, response);
                } else {
                    feedDao.remove(feed.getId());
                    logger.info("Feed removed successfully from the datebase");

                    FlockDao     flockDao      = new FlockDaoImpl();
                    FlockDto      flock         = flockDao.getById(flockId);
                    List<FeedDto> feedList      = feedDao.getAllByFlockId(flockId);
                    int           feedAmount    = 0;
                    float         feedTotalCost = 0;

                    for (FeedDto g : feedList) {
                        feedAmount    += g.getAmount();
                        feedTotalCost += g.getTotal();
                    }

                    flock.setFeedAdd(feedAmount);
                    flock.setTotalFeed(feedTotalCost);
                    flockDao.update(flock);

                    ControllerDao      controllerDao = new ControllerDaoImpl();
                    List<ControllerDto> controllers   = controllerDao.getAllByCellinkId(cellinkId);

                    for (ControllerDto controller : controllers) {
                        List<FlockDto> flocks = flockDao.getAllFlocksByController(controller.getId());

                        controller.setFlocks(flocks);
                    }

                    request.getSession().setAttribute("controllers", controllers);
                    request.getRequestDispatcher("./rmctrl-add-feed.jsp?celinkId=" + cellinkId + "&flockId="
                                                 + flockId).forward(request, response);
                }
            } catch (SQLException ex) {
                logger.info("Error occurs durring removing feed");
                request.getRequestDispatcher("./rmctrl-add-feed.jsp?celinkId=" + cellinkId + "&flockId="
                                             + flockId).forward(request, response);
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
