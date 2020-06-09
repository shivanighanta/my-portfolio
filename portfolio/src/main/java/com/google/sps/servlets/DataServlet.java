// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.Arrays;
import java.util.ArrayList;
import com.google.sps.data.Task;
import java.util.List;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private List<String> quotes;
  Gson gson = new Gson();

      @Override
    public void init() {
    quotes = new ArrayList<>();
    quotes.add(
        "A ship in port is safe, but that is not what ships are for. "
            + "Sail out to sea and do new things. - Grace Hopper");
    quotes.add("They told me computers could only do arithmetic. - Grace Hopper");
    quotes.add("It is much easier to apologise than it is to get permission. - Grace Hopper");
    quotes.add("If you can't give me poetry, can't you give me poetical science - Ada Lovelace");
    quotes.add("I am in a charming state of confusion. - Ada Lovelace");
    quotes.add(
        "The Analytical Engine weaves algebraic patterns, "
            + "just as the Jacquard loom weaves flowers and leaves. - Ada Lovelace");
    quotes.add(
        "Sometimes it is the people no one can imagine anything of "
            + "who do the things no one can imagine. - Alan Turing");
    quotes.add("Those who can imagine anything, can create the impossible. - Alan Turing");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get Random Quote and Print to Home Page
    String quote = quotes.get((int) (Math.random() * quotes.size()));
    String json = gson.toJson(quote);
    response.setContentType("application/json;");
    response.getWriter().println(json);

    // Load comments from Datastore and print to Home Page
    Query query = new Query("Task").addSort("firstname", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> tasks = new ArrayList<>();
    List<Task> listOfTasks = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String firstName = (String) entity.getProperty("firstname");
      String lastName = (String) entity.getProperty("lastname");
      String country = (String) entity.getProperty("country");
      String subject = (String) entity.getProperty("subject");

    Task task = new Task(id, firstName, lastName, country, subject);
    listOfTasks.add(task);

    String info = subject + " - " + firstName + " " + lastName + ", " + country; 
    response.getWriter().println(gson.toJson(info));
    



     // tasks.add(firstName + " " + lastName + ", from " + country + "\n" + "subject: " + subject);
    }

    response.setContentType("application/json;");
    //response.getWriter().println(gson.toJson(tasks));

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Store entities to Datastore
    String firstName = request.getParameter("firstname");
    String lastName = request.getParameter("lastname");
    String country = request.getParameter("country");
    String subject = request.getParameter("subject");


    Entity taskEntity = new Entity("Task");
    taskEntity.setProperty("firstname", firstName);
    taskEntity.setProperty("lastname", lastName);
    taskEntity.setProperty("country", country);
    taskEntity.setProperty("subject", subject);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);
    
    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");

  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

}
