

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubActivityFetcher {

  private static final String GITHUB_API_URL = "https://api.github.com/users/";

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java -jar github-activity.jar <github-username>");
      return;
    }

    String username = args[0];
    fetchGitHubActivity(username);
  }

  private static void fetchGitHubActivity(String username) {
    String urlString = GITHUB_API_URL + username + "/events";
    try {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");

      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line).append("\n");
        }
        reader.close();

        displayActivity(response.toString());
      } else {
        System.out.println("Failed to fetch data. HTTP Response Code: " + responseCode);
      }
    } catch (IOException e) {
      System.out.println("Error fetching data: " + e.getMessage());
    }
  }

  private static void displayActivity(String jsonResponse) {
    if (jsonResponse.isEmpty()) {
      System.out.println("No recent activity found.");
      return;
    }

    String[] events = jsonResponse.split("},"); // Simple split to parse JSON without using external libraries
    for (String event : events) {
      if (event.contains("PushEvent")) {
        System.out.println("- Pushed commits to a repository");
      } else if (event.contains("IssuesEvent")) {
        System.out.println("- Opened a new issue");
      } else if (event.contains("WatchEvent")) {
        System.out.println("- Starred a repository");
      }
    }
  }
}
