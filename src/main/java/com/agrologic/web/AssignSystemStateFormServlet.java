
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package com.agrologic.web;

//~--- non-JDK imports --------------------------------------------------------

import com.agrologic.dao.ProgSysStateDao;
import com.agrologic.dao.impl.ProgramSystemStateDaoImpl;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author JanL
 */
public class AssignSystemStateFormServlet extends HttpServlet {

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
        final Logger logger = Logger.getLogger(AddProgramServlet.class);

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            if (!CheckUserInSession.isUserInSession(request)) {
                logger.error("Unauthorized access!");
                request.getRequestDispatcher("./login.jsp").forward(request, response);
            } else {
                Long                                  programId               = Long.parseLong(request.getParameter("programId"));
                String                                dataSystemStateMapParam = request.getParameter("datamap");
                SortedMap<Integer, String>            numbersTextMap          = new TreeMap<Integer, String>();
                SortedMap<Long, Map<Integer, String>> dataSystemStateMap      = new TreeMap<Long, Map<Integer, String>>();
                int                                   numberCount             = 0,
                                                      maxNumbers              = 10;
                long                                  dataId                  = 0;
                StringTokenizer                       numbersTextToken        = new StringTokenizer(dataSystemStateMapParam, ";");

                while (numbersTextToken.hasMoreTokens()) {
                    StringTokenizer numbersToken = new StringTokenizer(numbersTextToken.nextToken(), ",");

                    if ((numberCount % maxNumbers) == 0) {
                        String token = numbersToken.nextToken();

                        dataId = Long.parseLong(token);
                    }

                    int    number = Integer.parseInt(numbersToken.nextToken().trim());
                    String text   = numbersToken.nextToken();

                    numbersTextMap.put(number, text);
                    numberCount++;

                    if ((numberCount % maxNumbers) == 0) {
                        dataSystemStateMap.put(dataId, numbersTextMap);
                        numbersTextMap = new TreeMap<Integer, String>();
                    }
                }

                try {
                    ProgSysStateDao programSystemStateDao = new ProgramSystemStateDaoImpl();

                    programSystemStateDao.insertSystemStates(programId, dataSystemStateMap);
                    logger.info("Relays successfuly added!");
                    request.getRequestDispatcher("./program-systemstates.html?programId=" + programId).forward(request,
                                                 response);
                } catch (SQLException ex) {
                    logger.info("Error occurs while retreive controller details!");
                    request.getRequestDispatcher("./program-systemstates.html").forward(request, response);
                }

                System.out.println();
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
