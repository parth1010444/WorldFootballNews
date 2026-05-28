# World Football News ⚽

**World Football News** is a native Android application that provides up-to-the-minute global football/soccer news. It leverages the ESPN Public API to deliver headlines, descriptions, and high-quality images from major leagues around the world.

## 🚀 Features

- **Global Coverage**: Get news from the Premier League, LaLiga, Bundesliga, Serie A, Ligue 1, MLS, and more.
- **League Selection**: Use the integrated league selector to filter news based on your favorite competitions.
- **Dynamic News Feed**: A clean, responsive list view powered by `RecyclerView` for smooth scrolling.
- **Pull-to-Refresh**: Easily update the feed by pulling down on the list.
- **In-App Article View**: Read the full story without leaving the app using the integrated WebView.
- **Offline Resilience**: Robust error handling for network issues and malformed API responses.

## 📸 Screenshots

| News Feed | League Selection | Pull-to-Refresh |
| :---: | :---: | :---: |
| ![News Feed](https://github.com/yourusername/WorldFootballNews/raw/main/screenshots/news_feed.png) | ![League Selection](https://github.com/yourusername/WorldFootballNews/raw/main/screenshots/league_selection.png) | ![Pull-to-Refresh](https://github.com/yourusername/WorldFootballNews/raw/main/screenshots/pull_to_refresh.png) |

## 🛠️ Tech Stack

- **Language**: Java
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) with [OkHttp](https://square.github.io/okhttp/)
- **JSON Parsing**: [Jackson](https://github.com/FasterXML/jackson)
- **Image Loading**: [Glide](https://github.com/bumptech/glide)
- **UI Architecture**: XML Layouts with Material Design components

## 📦 API Source

The application utilizes ESPN's public site API endpoints:

- **Base URL**: `https://site.api.espn.com/`
- **News Endpoint**: `/apis/site/v2/sports/soccer/{league}/news`

No API key is required for public access. Supported league identifiers include:
- `eng.1` (English Premier League)
- `esp.1` (Spanish LaLiga)
- `ger.1` (German Bundesliga)
- `ita.1` (Italian Serie A)
- `uefa.champions` (UEFA Champions League)

## 🛠️ Setup & Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/WorldFootballNews.git
    ```
2.  **Open in Android Studio**:
    File > Open > Select the project folder.
3.  **Sync Gradle**:
    Let Android Studio download the necessary dependencies.
4.  **Run**:
    Press the "Run" button or use `Shift + F10`.

