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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final Gson GSON = new Gson();
  private static final String FIRST_NAME = "firstname";
  private static final String LAST_NAME = "lastname";
  private static final String COUNTRY = "country";
  private static final String SUBJECT = "subject";
  private static final String SCORE = "score";

  private final static ImmutableList<String> QUOTES =
      ImmutableList.of("A ship in port is safe, but that is not what ships are for. "
              + "Sail out to sea and do new things. - Grace Hopper",
          "They told me computers could only do arithmetic. - Grace Hopper",
          "It is much easier to apologise than it is to get permission. - Grace Hopper",
          "If you can't give me poetry, can't you give me poetical science - Ada Lovelace",
          "I am in a charming state of confusion. - Ada Lovelace",
          "The Analytical Engine weaves algebraic patterns, "
              + "just as the Jacquard loom weaves flowers and leaves. - Ada Lovelace",
          "Sometimes it is the people no one can imagine anything of "
              + "who do the things no one can imagine. - Alan Turing",
          "Those who can imagine anything, can create the impossible. - Alan Turing");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Convert the array list to JSON
    String quote = QUOTES.get((int) (Math.random() * QUOTES.size()));
    String json = GSON.toJson(quote);
    response.setContentType("application/json;");
    response.getWriter().println(json);

    // Load comments from Datastore and print to Home Page
    Query query = new Query("Comment").addSort("firstname", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> Comments = new ArrayList<>();
    List<Comment> listOfComments = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String firstName = (String) entity.getProperty(FIRST_NAME);
      String lastName = (String) entity.getProperty(LAST_NAME);
      String country = (String) entity.getProperty(COUNTRY);
      String subject = (String) entity.getProperty(SUBJECT);
      double score = (double) entity.getProperty(SCORE);

      Comment Comment = new Comment(id, firstName, lastName, country, subject, score);
      listOfComments.add(Comment);
      String info = String.format(
          "%s - %s %s, %s, %s", subject, firstName, lastName, country, "Sentiment score: " + score);
      response.getWriter().println(GSON.toJson(info));
    }
    response.setContentType("application/json;");
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Store entities to Datastore
    String firstName = request.getParameter(FIRST_NAME);
    String lastName = request.getParameter(LAST_NAME);
    String country = request.getParameter(COUNTRY);
    String subject = request.getParameter(SUBJECT);

    // Get sentiment score
    double score = getSentimentScore(subject);

    Entity CommentEntity = new Entity("Comment");
    CommentEntity.setProperty(FIRST_NAME, firstName);
    CommentEntity.setProperty(LAST_NAME, lastName);
    CommentEntity.setProperty(COUNTRY, country);
    CommentEntity.setProperty(SUBJECT, subject);
    CommentEntity.setProperty(SCORE, score);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(CommentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /** Run sentiment analysis and return sentiment score */
  private double getSentimentScore(String message) throws IOException {
    Document doc =
        Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    double score = Math.round(sentiment.getScore() * 100.0) / 100.0;
    languageService.close();
    return score;
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