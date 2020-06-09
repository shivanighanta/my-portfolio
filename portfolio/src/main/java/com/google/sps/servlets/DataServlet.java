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
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.common.collect.ImmutableList;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private static final Gson gson = new Gson();
  private final static ImmutableList<String> QUOTES = ImmutableList.of
    ("A ship in port is safe, but that is not what ships are for. "
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
    String json = gson.toJson(quote);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
